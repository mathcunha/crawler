package br.mia.unifor.crawlerenvironment.mom;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;

import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.WorkloadFunction;
import br.mia.unifor.crawlerenvironment.mom.event.BenchmarkEvent;
import br.mia.unifor.crawlerenvironment.mom.event.CloudCrawlerEnvironmentEvent;
import br.mia.unifor.crawlerenvironment.mom.event.EventSerializer;
import br.mia.unifor.crawlerenvironment.mom.event.WorkloadEvent;

public class BenchmarkProducer {

	public final Benchmark benchmark;
	public final String queueName;
	protected Logger logger = Logger.getLogger(getClass().getName());

	protected BenchmarkProducer(Benchmark benchmark, String queueName) {
		this.benchmark = benchmark;
		this.queueName = queueName;
	}

	public List<CloudCrawlerEnvironmentEvent> getScenarioEvents() {
		List<CloudCrawlerEnvironmentEvent> events = new ArrayList<CloudCrawlerEnvironmentEvent>();
		int count = 0;

		for (Scenario scenario : benchmark.getScenarios()) {
			events.add(new BenchmarkEvent(count++,
					BenchmarkEvent.ACTION_NEW_SCENARIO, scenario, benchmark));
			for (WorkloadFunction workloadFunction : scenario.getWorkload()
					.getFunctions()) {
				for (int i = 0; i < benchmark.getRounds(); i++) {
					events.add(new WorkloadEvent(count++,
							BenchmarkEvent.ACTION_NEW_WORKLOAD,
							workloadFunction, benchmark, scenario));
				}
			}
			if (scenario.getEndable()) {
				events.add(new BenchmarkEvent(count++,
						BenchmarkEvent.ACTION_END_SCENARIO, scenario, benchmark));
			}
		}

		logger.info(events.size() + " events");
		return events;
	}

	public List<CloudCrawlerEnvironmentEvent> getEvents() {
		List<CloudCrawlerEnvironmentEvent> events = new ArrayList<CloudCrawlerEnvironmentEvent>();
		int count = 0;
		events.add(new BenchmarkEvent(count++, BenchmarkEvent.ACTION_NEW,
				benchmark, benchmark));

		for (Scenario scenario : benchmark.getScenarios()) {
			events.add(new BenchmarkEvent(count++,
					BenchmarkEvent.ACTION_NEW_SCENARIO, scenario, benchmark));
			for (WorkloadFunction workloadFunction : scenario.getWorkload()
					.getFunctions()) {
				for (int i = 0; i < benchmark.getRounds(); i++) {
					events.add(new WorkloadEvent(count++,
							BenchmarkEvent.ACTION_NEW_WORKLOAD,
							workloadFunction, benchmark, scenario));
				}
			}
			if (scenario.getEndable()) {
				events.add(new BenchmarkEvent(count++,
						BenchmarkEvent.ACTION_END_SCENARIO, scenario, benchmark));
			}
		}

		if (benchmark.getEndable()) {
			events.add(new BenchmarkEvent(count++, BenchmarkEvent.ACTION_END,
					benchmark, benchmark));
		}

		logger.info(events.size() + " events");
		return events;
	}

	protected Boolean postEvents(List<CloudCrawlerEnvironmentEvent> events) {

		ClientSession session = null;
		ClientProducer producer = null;
		try {
			session = EmbeddedHornetQWrapper.getClientSession();

			session.start();

			producer = session.createProducer(queueName);

			for (CloudCrawlerEnvironmentEvent cloudCrawlerEnvironmentEvent : events) {
				ClientMessage message = session.createMessage(true);

				message.getBodyBuffer().writeString(
						EventSerializer.getYaml(cloudCrawlerEnvironmentEvent));

				producer.send(message);
			}

			return Boolean.TRUE;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client session available", e);
		} finally {
			closeSession(session);
			try {
				producer.close();
			} catch (HornetQException e) {
				logger.log(Level.SEVERE,
						"error trying to close the Message Producer", e);
			}
		}

		return Boolean.FALSE;
	}

	private void closeSession(ClientSession session) {
		try {
			if (session != null)
				session.close();
		} catch (HornetQException e) {
			logger.log(Level.SEVERE, "no client session available", e);
		}
	}

}
