package br.mia.unifor.crawlerenvironment.engine.view.rest.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import br.mia.unifor.crawler.builder.factory.ComputeProviderFactory;
import br.mia.unifor.crawler.engine.EngineAsync;
import br.mia.unifor.crawler.engine.ValidationException;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Provider;
import br.mia.unifor.crawlerenvironment.Main;
import br.mia.unifor.crawlerenvironment.engine.view.JSONHelper;
import br.mia.unifor.crawlerenvironment.mom.BenchmarkController;
import br.mia.unifor.crawlerenvironment.mom.event.BenchmarkEvent;

@Path("/v1/benchmark")
public class BenchmarkResource extends CloudCrawlerEnvironmentResource {

	public String baseDir;
	public String baseDirTemp;

	public BenchmarkResource() {
		baseDir = Main.properties.getProperty("fs.base.dir")
				+ System.getProperty("file.separator") + "benchmarks"
				+ System.getProperty("file.separator");

		baseDirTemp = Main.properties.getProperty("fs.base.dir")
				+ System.getProperty("file.separator") + "tmp"
				+ System.getProperty("file.separator");
		//To Ensure the existence of the directories
		(new File(baseDir)).mkdirs();
		(new File(baseDirTemp)).mkdirs();
	}

	@GET
	@Produces("application/json")
	public String listExecutions() {
		return JSONHelper.getJSON(BenchmarkController.getBenchmarkQueueNames());
	}

	@GET
	@Path("{benchmark_id}")
	@Produces("application/json")
	public String getExecutionStatus(
			@PathParam("benchmark_id") String benchmarkId) throws FileNotFoundException, IOException, ValidationException {
		Integer count = BenchmarkController.listMessages(BenchmarkController
				.getQueueControllerName(benchmarkId));
		logger.info("controller " + count);

		count = BenchmarkController.listMessages(BenchmarkController
				.getQueueExecutionName(benchmarkId));

		logger.info("execution " + count);
		
		String name = baseDir + benchmarkId + ".yml";
		File file = new File(name);
		Benchmark benchmark = EngineAsync.load(new FileInputStream(file), false);
		
		if(count > 0)
			return getBenchmarkJSON(benchmarkId, "READY", (new BenchmarkController(benchmark, "LIST")).getProducer().getEvents().size()+"", count.toString());
		else
			return getBenchmarkJSON(benchmarkId, "NOTFOUND", "-1","-1");
	}

	@POST
	@Path("{benchmark_id}/suspend")
	@Produces("application/json")
	public String getSuspendExecution(
			@PathParam("benchmark_id") String benchmarkId) {
		Benchmark benchmark = new Benchmark();
		benchmark.setId(benchmarkId);

		Thread lBenchmarkController = new Thread(new BenchmarkController(
				benchmark, BenchmarkEvent.ACTION_SUSPEND));
		lBenchmarkController.start();
		return getBenchmarkJSON(benchmarkId, "SUSPEND", "-1",""+ BenchmarkController.listMessages(BenchmarkController.getQueueExecutionName(benchmarkId)));
	}

	@POST
	@Path("{benchmark_id}/abort")
	@Produces("application/json")
	public String getAbortExecution(
			@PathParam("benchmark_id") String benchmarkId) {
		Benchmark benchmark = new Benchmark();
		benchmark.setId(benchmarkId);

		Thread lBenchmarkController = new Thread(new BenchmarkController(
				benchmark, BenchmarkEvent.ACTION_ABORT));
		lBenchmarkController.start();
		return getBenchmarkJSON(benchmarkId, "ABORT", "-1",""+ BenchmarkController.listMessages(BenchmarkController.getQueueExecutionName(benchmarkId)));
	}
	
	private void setResourcePaths(Benchmark benchmark){
		for (Provider provider : benchmark.getProviders()) {
			provider.setCredentialPath(Main.properties.getProperty("fs.base.dir")
					+ System.getProperty("file.separator") +provider.getCredentialPath());
			provider.setPrivateKey(Main.properties.getProperty("fs.base.dir")
					+ System.getProperty("file.separator") +provider.getPrivateKey());
		}		
	}

	@POST
	@Path("{benchmark_id}/resume")
	@Produces("application/json")
	public String getResumeExecution(
			@PathParam("benchmark_id") String benchmarkId) {

		String name = baseDir + benchmarkId + ".yml";
		try {

			File file = new File(name);
			Benchmark benchmark = EngineAsync.load(new FileInputStream(file), false);
			
			setResourcePaths(benchmark);
			BenchmarkController lBenchmarkController = new BenchmarkController(
					benchmark, BenchmarkEvent.ACTION_RESUME);

			(new Thread(lBenchmarkController)).start();
			
			
			
			return getBenchmarkJSON(benchmarkId, "RUNNING", lBenchmarkController.getProducer().getEvents().size()+"", ""+BenchmarkController.listMessages(BenchmarkController.getQueueExecutionName(benchmarkId)));

		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "failure loading the benchmark file "
					+ name, e);
			return getStackTrace(e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "failure reading the benchmark file", e);
			return getStackTrace(e);
		} catch (ValidationException e) {
			logger.log(Level.WARNING, "the benchmark file has validation's issues", e);
			return getStackTrace(e);
		}

	}
	
	private void loadProvidersProperties(Benchmark benchmark){
		for (Provider provider : benchmark.getProviders()) {
			try {
				ComputeProviderFactory.getProvider(provider);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "error loading the providers context",e);
			}
		}
	}
	
	@POST
	@Path("json")
	@Produces("application/json")
	@Consumes("application/json")
	public String startJsonExecution(String crawlJson){
		
		logger.info(crawlJson);
		
		return getBenchmarkJSON("-1","-1","-1","-1");
	}
			

	@POST
	@Produces("application/json")
	@Consumes("text/plain")
	public String startExecution(String crawlFile) {

		logger.log(Level.FINE, crawlFile);

		File file = new FileStoreResource().insertFileResource(crawlFile,
				baseDirTemp);
		if (file != null) {
			try {
				
				Benchmark benchmark = EngineAsync.load(new FileInputStream(file), true);

				File saveFile = new File(baseDir + benchmark.getId() + ".yml");
				logger.log(Level.FINE, saveFile.getCanonicalPath());
				

				if (!saveFile.exists())
					saveFile.createNewFile();

				FileStoreResource.copyFile(file, saveFile);

				logger.info("beginning execution");
				
				setResourcePaths(benchmark);
				BenchmarkController lBenchmarkController = new BenchmarkController(benchmark,BenchmarkEvent.ACTION_NEW);
				
				loadProvidersProperties(benchmark);

				(new Thread(lBenchmarkController)).start();
				
				return getBenchmarkJSON(benchmark.getId(), "RUNNING", (new Integer(lBenchmarkController.getProducer().getEvents().size())).toString(), "0");
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, "failure loading the benchmark file",
						e);
				file.delete();
				return getStackTrace(e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "failure reading the benchmark file",
						e);
				file.delete();
				return getStackTrace(e);
			} catch (ValidationException e) {
				logger.log(Level.SEVERE, "the benchmark file has validation's issues",
						e);
				file.delete();
				return getStackTrace(e);
			}
		} else {
			return "maybe the benchmark already exists - try to list the executions";
		}

	}
	
	public static String getBenchmarkJSON(String id, String status, String totalEvents, String currentEvents){
		StringBuffer result = new StringBuffer();
		
		result.append("{ \"benchmark\":{")
		.append("	  \"id\":\""+id+"\",")
		.append("	  \"status\": \""+status+"\",")
		.append("	  \"totalEvents\": \""+totalEvents+"\",")
		.append("	  \"currentEvents\": \""+currentEvents+"\"")
		.append("   }")
		.append("}");
		
		return result.toString();
	}
}