package br.mia.unifor.crawler.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import br.mia.unifor.crawler.builder.ComputeProvider;
import br.mia.unifor.crawler.builder.factory.ComputeProviderFactory;
import br.mia.unifor.crawler.executer.Execution;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;
import br.mia.unifor.crawler.executer.artifact.WorkloadFunction;
import br.mia.unifor.crawler.parser.CrawlerParserYml;
import br.mia.unifor.crawler.parser.YamlLoader;

public class EngineAsync {

	protected static Logger logger = Logger.getLogger(EngineAsync.class
			.getName());

	public static Benchmark load(InputStream input, boolean validate)
			throws IOException, ValidationException {
		CrawlerParserYml parser = new CrawlerParserYml(input);
		Benchmark benchmark = YamlLoader.loadTest(parser.processLineByLine());

		if (validate) {
			ValidationEngine.validate(benchmark);
		}

		return benchmark;
	}
	public static Scenario loadScenario(InputStream input, boolean validate)
			throws IOException, ValidationException {
		CrawlerParserYml parser = new CrawlerParserYml(input);
		Scenario scenario = YamlLoader.loadScenario(parser.processLineByLine());
		
		if (validate) {
			ValidationEngine.validateScenario(scenario);
		}
		
		return scenario;
	}

	public static void stopInstances(List<VirtualMachine> instances)
			throws CrawlException {
		for (VirtualMachine instance : instances) {
			if (!"local".equals(instance.getType().getProvider().getName())
					&& instance.getProviderId() != null) {
				ComputeProvider provider = ComputeProviderFactory
						.getProvider(instance.getType().getProvider());

				logger.info("provider name=["
						+ instance.getType().getProvider().getName()
						+ "], instanceId=[" + instance.getId()
						+ "] stopping...");

				provider.stopInstance(instance);

				logger.info("provider name=["
						+ instance.getType().getProvider().getName()
						+ "], instanceId=[" + instance.getId() + "] STOPPED");

			}
		}
	}

	public static void startInstances(Scenario scenario) throws CrawlException {
		startInstances(scenario, scenario.getVirtualMachines().values());
		startInstances(scenario, scenario.getMetric().values());
	}

	private static void startInstances(Scenario scenario,
			Collection<VirtualMachine> instances) throws CrawlException {

		for (VirtualMachine instance : instances) {
			if (!"local".equals(instance.getType().getProvider().getName())) {
				ComputeProvider provider = ComputeProviderFactory
						.getProvider(instance.getType().getProvider());

				provider.startInstance(instance, scenario);

				logger.info(instance.getId() + " ready "+instance.getProviderId());
			}
		}
	}

	public static void stopLocalInstances(Scenario scenario, Benchmark benchmark)
			throws CrawlException {
		List<VirtualMachine> instances = new ArrayList<VirtualMachine>(scenario.getVirtualMachines().values());

		// removendo as instancias globais
		for (int i = 0; i < instances.size(); ++i) {
			VirtualMachine instance = instances.get(i);
			if (benchmark.getVirtualMachines().contains(instance)) {
				instances.remove(i);
			}
		}

		stopInstances(instances);
	}

	public static void startInstances(Benchmark benchmark) throws CrawlException {
		startInstances(null, benchmark.getVirtualMachines());
	}

	public static void execTests(Scenario scenario, Benchmark benchmark,
			WorkloadFunction workloadFunction) throws CrawlException,
			InterruptedException {

		Execution.execTests(scenario, benchmark, workloadFunction, scenario.getWorkload().getTargets());
		
	}
	
	/*public static void main(String[] args) throws ValidationException, FileNotFoundException, IOException {
		Benchmark benchmark = EngineAsync.load(new FileInputStream("../examples/wordpress/wordpress.yml"), Boolean.FALSE);
		Scenario scenario = benchmark.getScenarios().get(0);
		scenario.getVirtualMachines().get("gatling").setPublicIpAddress("ip.gatling");
		VirtualMachine vm = scenario.getVirtualMachines().get("wordpress");
		vm.setPublicIpAddress("ip.wordpress");
		Scriptlet scriptlet = vm.getScripts().get("start_vm");
		System.out.println(ScriptParser.parse(scenario, scriptlet , vm));
	}*/
}
