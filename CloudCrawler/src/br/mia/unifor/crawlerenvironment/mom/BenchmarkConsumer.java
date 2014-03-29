package br.mia.unifor.crawlerenvironment.mom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;

import br.mia.unifor.crawler.engine.CrawlException;
import br.mia.unifor.crawler.engine.EngineAsync;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;
import br.mia.unifor.crawler.executer.artifact.WorkloadFunction;
import br.mia.unifor.crawlerenvironment.mom.event.BenchmarkEvent;
import br.mia.unifor.crawlerenvironment.mom.event.EventSerializer;
import br.mia.unifor.crawlerenvironment.mom.event.WorkloadEvent;

public class BenchmarkConsumer implements Runnable {
	protected Logger logger = Logger.getLogger(getClass().getName());
	private Boolean execute = true;
	private final Benchmark benchmark;
	private final Map<String, VirtualMachine> virtualMachines;

	protected BenchmarkConsumer(Benchmark benchmark) {
		this.benchmark = benchmark;
		virtualMachines = new HashMap<String, VirtualMachine>();
		getControllerClient(
				BenchmarkController.getQueueControllerName(benchmark.getId()),
				this).start();
	}

	private void stopInstances() throws CrawlException {

		Set<VirtualMachine> virtualMachines = new HashSet<VirtualMachine>();
		virtualMachines.addAll(benchmark.getVirtualMachines());
		for (Scenario scenario : benchmark.getScenarios()) {
			virtualMachines.addAll(scenario.getVirtualMachines().values());
		}

		for (VirtualMachine virtualMachine : virtualMachines) {
			logger.info("stopping instances " + virtualMachine.getId());
		}

		EngineAsync.stopInstances(new ArrayList<>(virtualMachines));
	}

	private Thread getControllerClient(final String queueControllerName,
			final BenchmarkConsumer benchmarkConsumer) {
		Thread client = new Thread(new Runnable() {
			@Override
			public void run() {
				while (execute) {
					ClientSession session = null;
					try {
						session = EmbeddedHornetQWrapper.getClientSession();

						session.start();

						ClientConsumer consumer = session
								.createConsumer(queueControllerName);

						logger.info(queueControllerName
								+ "[Controller Client] waiting for benchmark messages ");
						ClientMessage msgReceived = consumer.receive();

						session.commit();

						msgReceived.acknowledge();

						if (msgReceived != null) {
							BenchmarkEvent benchmarkEvent = EventSerializer
									.loadBenchmarkEvent(msgReceived
											.getBodyBuffer().readString());

							if (BenchmarkEvent.ACTION_END.equals(benchmarkEvent
									.getAction())) {
								benchmarkConsumer.setExecute(false);
								logger.info("End Action received");
								execute = false;
								deleteQueues();
							} else if (BenchmarkEvent.ACTION_SUSPEND
									.equals(benchmarkEvent.getAction())) {
								benchmarkConsumer.setExecute(false);
								logger.info("SUSPEND Action received");
								execute = false;
							} else if (BenchmarkEvent.ACTION_ABORT
									.equals(benchmarkEvent.getAction())) {
								benchmarkConsumer.setExecute(false);
								logger.info("ABORT Action received, deleting queues");
								execute = false;
								stopInstances();
								deleteQueues();
							} else {
								logger.warning("Invalid event action "
										+ benchmarkEvent.getAction());
							}
						}

					} catch (Exception e) {
						logger.log(Level.SEVERE, "no client session available",
								e);
					} finally {
						closeSession(session);
					}

				}
			}

		});

		return client;
	}

	@Override
	public void run() {

		ClientSession session = null;
		ClientConsumer consumer = null;
		try {
			session = EmbeddedHornetQWrapper.getClientSession();

			session.start();

			consumer = session.createConsumer(BenchmarkController
					.getQueueExecutionName(benchmark.getId()));

			while (execute) {

				ClientMessage msgReceived = consumer.receive();

				String messageBody = msgReceived.getBodyBuffer().readString();

				logger.log(Level.FINE, messageBody);

				BenchmarkEvent event = EventSerializer
						.loadBenchmarkEvent(messageBody);

				logger.log(Level.INFO, "Consumer Action =>" + event.getAction());

				onMessage(event);

				session.commit();

				msgReceived.acknowledge();

			}
			stopInstances();
		} catch (Exception e) {

			logger.log(Level.SEVERE, "a error has occured, so aborting execution",
					e);
			new BenchmarkController(benchmark, BenchmarkEvent.ACTION_ABORT).run();

		} finally {
			closeSession(session);

			if (consumer != null) {
				try {
					consumer.close();
				} catch (HornetQException e) {
					logger.log(Level.SEVERE, "error closing the consumer", e);
				}
			}
		}
	}
	
	private void verifyAlreadyCreatedVirtualMachines(Collection<VirtualMachine> virtualMachines){
		//As the user can let the crawler create yours VMs, and the providersId information is only known after the serialization of the events, we have to store this information in order to do not create this VM at each new scenario
		VirtualMachine lVirtualMachine;
		for (VirtualMachine virtualMachine : virtualMachines) {
			lVirtualMachine = this.virtualMachines.get(virtualMachine.getId());
			if(lVirtualMachine != null){
				virtualMachine.setProviderId(lVirtualMachine.getProviderId());
			}
		}
	}
	
	private void updateIdsCreatedVirtualMachines(Collection<VirtualMachine> virtualMachines){
		for (VirtualMachine virtualMachine : virtualMachines) {
			this.virtualMachines.put(virtualMachine.getId(), virtualMachine);
		}
	}

	private void onMessage(BenchmarkEvent event) throws 
			InterruptedException, CrawlException {
		logger.info("Starting ACTION "+event.getAction());
		if (BenchmarkEvent.ACTION_END.equals(event.getAction())) {
			execute = false;
			BenchmarkController controller = new BenchmarkController(benchmark,
					BenchmarkEvent.ACTION_END);
			verifyAlreadyCreatedVirtualMachines(event.getBenchmark().getVirtualMachines());
			EngineAsync
					.stopInstances(event.getBenchmark().getVirtualMachines());
			controller.endExecution();
		} else if (BenchmarkEvent.ACTION_NEW.equals(event.getAction())) {

			verifyAlreadyCreatedVirtualMachines(event.getBenchmark().getVirtualMachines());
			EngineAsync.startInstances(event.getBenchmark());
			updateIdsCreatedVirtualMachines(event.getBenchmark().getVirtualMachines());

		} else if (BenchmarkEvent.ACTION_NEW_SCENARIO.equals(event.getAction())) {

			verifyAlreadyCreatedVirtualMachines(((Scenario) event.getTarget()).getVirtualMachines().values());
			EngineAsync.startInstances((Scenario) event.getTarget());
			updateIdsCreatedVirtualMachines(((Scenario) event.getTarget()).getVirtualMachines().values());

		} else if (BenchmarkEvent.ACTION_NEW_WORKLOAD.equals(event.getAction())) {
			WorkloadEvent lWorkloadEvent = (WorkloadEvent) event;

			verifyAlreadyCreatedVirtualMachines(lWorkloadEvent.getScenario().getVirtualMachines().values());
			EngineAsync.startInstances(lWorkloadEvent.getScenario());
			updateIdsCreatedVirtualMachines(lWorkloadEvent.getScenario().getVirtualMachines().values());

			EngineAsync.execTests(lWorkloadEvent.getScenario(),
					lWorkloadEvent.getBenchmark(),
					(WorkloadFunction) lWorkloadEvent.getTarget());

		} else if (BenchmarkEvent.ACTION_END_SCENARIO.equals(event.getAction())) {
			verifyAlreadyCreatedVirtualMachines(((Scenario) event.getTarget()).getVirtualMachines().values());
			EngineAsync.stopLocalInstances((Scenario) event.getTarget(),
					event.getBenchmark());
		} else {
			logger.log(Level.WARNING, event.getAction());
		}
		
		logger.info("Ending ACTION "+event.getAction());
	}

	private void closeSession(ClientSession session) {
		try {
			if (session != null)
				session.close();
		} catch (HornetQException e) {
			logger.log(Level.SEVERE, "no client session available", e);
		}
	}

	public Boolean getExecute() {
		return execute;
	}

	public void setExecute(Boolean execute) {
		this.execute = execute;
	}

	protected final void deleteQueues() {
		final String queueExecutionName = BenchmarkController
				.getQueueControllerName(benchmark.getId().toString());
		final String queueControllerName = BenchmarkController
				.getQueueExecutionName(benchmark.getId().toString());

		deleteQueue(queueExecutionName);
		deleteQueue(queueControllerName);

	}

	public Boolean deleteQueue(String queueName) {
		ClientSession session = null;
		try {
			session = EmbeddedHornetQWrapper.getClientSession();

			session.deleteQueue(queueName);

			session.start();

			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client session available", e);
		} finally {

			closeSession(session);
		}

		return false;
	}

}
