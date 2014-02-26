package br.mia.unifor.crawlerenvironment.mom;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientRequestor;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.management.ManagementHelper;
import org.hornetq.api.core.management.ResourceNames;

import br.mia.unifor.crawler.engine.EngineAsync;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawlerenvironment.mom.event.BenchmarkEvent;
import br.mia.unifor.crawlerenvironment.mom.event.CloudCrawlerEnvironmentEvent;
import br.mia.unifor.crawlerenvironment.mom.event.EventSerializer;

public class BenchmarkController implements Runnable {
	protected static Logger logger = Logger.getLogger(BenchmarkController.class
			.getName());
	public final Benchmark benchmark;
	public final String action;

	public BenchmarkController(Benchmark benchmark, String action) {
		this.benchmark = benchmark;
		this.action = action;
	}

	public static String getQueueName(String type, String benchmark_id) {
		return type + "_" + benchmark_id;
	}

	public static String getQueueControllerName(String benchmark_id) {
		return getQueueName("benchmark_control", benchmark_id);
	}

	public static String getQueueExecutionName(String benchmark_id) {
		return getQueueName("benchmark_exec", benchmark_id);
	}

	private void abortExecution() {

		postMessage(getQueueControllerName(benchmark.getId().toString()),
				BenchmarkEvent.ACTION_ABORT);

	}

	private void suspendExecution() {
		logger.info(getQueueControllerName(benchmark.getId().toString())
				+ " posting " + action);
		postMessage(getQueueControllerName(benchmark.getId().toString()),
				action);

	}

	protected void endExecution() {

		postMessage(getQueueControllerName(benchmark.getId().toString()),
				BenchmarkEvent.ACTION_END);

	}

	public static Integer listMessages(String queueName) {
		ClientSession session = null;
		ClientRequestor requestor = null;
		logger.info("messageCount for queue " + queueName);
		try {
			session = EmbeddedHornetQWrapper.getClientSession();
			requestor = new ClientRequestor(session,
					"jms.queue.hornetq.management");
			ClientMessage message = session.createMessage(false);
			ManagementHelper.putAttribute(message, ResourceNames.CORE_QUEUE
					+ queueName, "messageCount");
			session.start();
			ClientMessage reply = requestor.request(message);
			
			Object response = (Object) ManagementHelper.getResult(reply);
			logger.info("ManagementHelper messageCount ("+response+")");
			return (Integer) response;

		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client session available", e);
		} finally {

			try {
				requestor.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "no client session available", e);
			}

			try {
				session.close();
			} catch (HornetQException e) {
				logger.log(Level.SEVERE, "no client session available", e);
			}
		}

		return -1;
	}

	private void postMessage(String queueName, String action) {

		ClientSession session = null;
		ClientProducer producer = null;
		try {
			session = EmbeddedHornetQWrapper.getClientSession();

			session.start();

			producer = session.createProducer(queueName);
			ClientMessage message = session.createMessage(true);

			message.getBodyBuffer().writeString(
					EventSerializer.getYaml(new BenchmarkEvent(-1, action,
							benchmark, benchmark)));

			producer.send(message);

			logger.info("message " + action + " sent to queue " + queueName);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client session available", e);
		} finally {

			try {
				producer.close();
			} catch (HornetQException e) {
				logger.log(Level.SEVERE,
						"error trying to close the Message Producer", e);
			}
			closeSession(session);
		}
	}

	@Override
	public void run() {
		logger.info("beginning " + action);
		if (BenchmarkEvent.ACTION_NEW.equals(action)
				|| BenchmarkEvent.ACTION_RESUME.equals(action)) {
			executeBenchmark();
		} else if (BenchmarkEvent.ACTION_SUSPEND.equals(action)) {
			suspendExecution();
		} else if (BenchmarkEvent.ACTION_ABORT.equals(action)) {
			abortExecution();
		}
		logger.info("ending " + action);
	}
	
	public BenchmarkProducer getProducer(){
		return new BenchmarkProducer(benchmark, getQueueExecutionName(benchmark.getId().toString()));
	}

	private void executeBenchmark() {
		final String queueExecutionName = getQueueExecutionName(benchmark
				.getId().toString());
		final String queueControllerName = getQueueControllerName(benchmark
				.getId().toString());
		logger.info("findind queue " + queueExecutionName + " and "
				+ queueControllerName);

		BenchmarkProducer producer = getProducer();

		if (thereIsQueue(queueExecutionName)
				|| thereIsQueue(queueControllerName)) {
			logger.info(queueExecutionName + " and " + queueControllerName
					+ " already exist, resuming execution ");

			resumeExecution();
		} else {
			logger.info("creating the queue " + queueExecutionName + " and "
					+ queueControllerName);
			if (createQueue(queueControllerName)
					&& createQueue(queueExecutionName)) {

				logger.info("creating the producer ");

				List<CloudCrawlerEnvironmentEvent> events = producer
						.getEvents();

				logger.info("posting messages " + producer.postEvents(events));

				resumeExecution();
			} else {
				logger.info("failure creating queue " + queueExecutionName
						+ " and " + queueControllerName);
			}
		}
	}

	private void resumeExecution() {
		logger.info("creating the consumer ");

		BenchmarkConsumer consumer = new BenchmarkConsumer(benchmark);

		consumer.run();

	}

	public Boolean createQueue(String queueName) {
		ClientSession session = null;
		try {
			session = EmbeddedHornetQWrapper.getClientSession();

			session.createQueue(queueName, queueName, true);

			session.start();

			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client session available", e);
		} finally {

			closeSession(session);
		}

		return false;

	}

	private static void closeSession(ClientSession session) {
		try {
			if (session != null)
				session.close();
		} catch (HornetQException e) {
			logger.log(Level.SEVERE, "no client session available", e);
		}
	}

	public static ClientMessage sendManagementMessage(ClientSession session,
			String action) {

		ClientRequestor requestor = null;
		try {

			requestor = new ClientRequestor(session,
					"jms.queue.hornetq.management");

			ClientMessage message = session.createMessage(true);

			ManagementHelper.putAttribute(message, ResourceNames.CORE_SERVER,
					action);

			return requestor.request(message, 2000);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client requestor available", e);
		} finally {
			try {
				requestor.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "error closing the requestor", e);
			}
		}

		return null;
	}

	public static List<String> getBenchmarkQueueNames() {
		return getQueueNames();
	}

	public static List<String> getQueueNames() {

		ClientSession session = null;

		try {

			session = EmbeddedHornetQWrapper.getClientSession();
			session.start();

			ClientMessage reply = sendManagementMessage(session, "queueNames");
			if (reply != null) {
				if (ManagementHelper.hasOperationSucceeded(reply)) {
					List<String> queueList = new ArrayList<String>();
					for (Object arrayQueue : ManagementHelper.getResults(reply)) {
						for (Object queue : (Object[]) arrayQueue) {
							queueList.add(queue.toString());
						}
					}
					return queueList;
				}
			} else {
				logger.log(Level.SEVERE, "no reply to queueNames at "
						+ ResourceNames.CORE_SERVER);
			}
			reply.acknowledge();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client session available", e);
		} finally {

			closeSession(session);
		}

		return null;

	}

	public Boolean thereIsQueue(String queueName) {
		List<String> queueList = getQueueNames();
		if (queueList != null) {
			return queueList.contains(queueName);
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		BenchmarkController lBenchmarkController = new BenchmarkController(
				EngineAsync.load(Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("specjvm2008.yml"), true),
				BenchmarkEvent.ACTION_NEW);

		lBenchmarkController.run();

		EmbeddedHornetQWrapper.tearDown();
	}

}
