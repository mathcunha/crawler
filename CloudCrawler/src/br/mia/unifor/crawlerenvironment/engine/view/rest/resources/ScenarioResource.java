package br.mia.unifor.crawlerenvironment.engine.view.rest.resources;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import br.mia.unifor.crawler.engine.EngineAsync;
import br.mia.unifor.crawler.engine.ValidationException;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawlerenvironment.mom.BenchmarkController;
import br.mia.unifor.crawlerenvironment.mom.event.BenchmarkEvent;

@Path("/v1/scenario")
public class ScenarioResource extends CloudCrawlerEnvironmentResource {

	@POST
	@Path("{benchmark_id}")
	@Produces("application/json")
	@Consumes("text/plain")
	public String addScenario(@PathParam("benchmark_id") String benchmarkId,
			String strScenario) throws IOException {
		logger.log(Level.INFO, strScenario);

		InputStream inputScenario = null;

		try {
			String strBenchmark = addScenarioToYaml(benchmarkId, strScenario);

			logger.info("new benchmark crawl file " + strBenchmark);

			inputScenario = new ByteArrayInputStream(
					strBenchmark.getBytes(Charset.forName("UTF-8")));

			Benchmark benchmark = EngineAsync.load(inputScenario, true);

			logger.info("new scenario loaded");

			BenchmarkResource.setResourcePaths(benchmark);
			BenchmarkController lBenchmarkController = new BenchmarkController(
					benchmark, BenchmarkEvent.ACTION_NEW_SCENARIO);
			(new Thread(lBenchmarkController)).start();

			Integer count = BenchmarkController
					.listMessages(BenchmarkController
							.getQueueExecutionName(benchmarkId));

			if (count > 0)
				return BenchmarkResource.getBenchmarkJSON(benchmarkId, "READY",
						"-1", count.toString());
			else
				return BenchmarkResource.getBenchmarkJSON(benchmarkId,
						"NOTFOUND", "-1", "-1");

		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "failure loading the benchmark file", e);
			return getStackTrace(e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "failure reading the benchmark file", e);
			return getStackTrace(e);
		} catch (ValidationException e) {
			logger.log(Level.SEVERE,
					"the benchmark file has validation's issues", e);
			return getStackTrace(e);
		} finally {
			if (inputScenario != null) {
				inputScenario.close();
			}
		}

	}

	public static String addScenarioToYaml(String benchmarkId, String scenario)
			throws IOException {

		logger.info("openning the benchmark " + benchmarkId + ".yml file");
		BufferedReader reader = new BufferedReader(new FileReader(
				BenchmarkResource.BASE_DIR + benchmarkId + ".yml"));
		StringBuffer buffer = new StringBuffer();

		try {
			String line = reader.readLine();
			boolean insideScenario = false;

			while (line != null) {
				if (!insideScenario) {
					if (line.startsWith("scenarios:")) {
						logger.info("old scenario: beginning found");
						insideScenario = true;

						buffer.append(line + "\n - ");
						buffer.append(scenario + "\n");

						line = reader.readLine();
						continue;
					}
				}

				if (insideScenario) {
					if (line.matches("^[\\w].*")) {
						logger.info("old scenario: ending found");
						insideScenario = false;
					}
				}

				if (!insideScenario) {
					buffer.append(line + "\n");
				}

				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "failure loading the benchmark file", e);
			return getStackTrace(e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "failure reading the benchmark file", e);
			return getStackTrace(e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return buffer.toString();
	}	
}
