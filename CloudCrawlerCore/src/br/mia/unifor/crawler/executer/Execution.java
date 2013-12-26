package br.mia.unifor.crawler.executer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.NotImplementedException;

import com.esotericsoftware.yamlbeans.YamlException;

import br.mia.unifor.crawler.builder.ComputeProvider;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Metric;
import br.mia.unifor.crawler.executer.artifact.MetricEval;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.Scriptlet;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;
import br.mia.unifor.crawler.executer.artifact.WorkloadFunction;
import br.mia.unifor.crawler.executer.client.BenchmarkPartialResult;
import br.mia.unifor.crawler.parser.YamlLoader;

public class Execution {
	protected static Logger logger = Logger
			.getLogger(Execution.class.getName());
	
	private static Boolean stopMetricCollection(VirtualMachine target) {
		Scriptlet script = new Scriptlet();
		script.setScripts(Arrays
				.asList(new String[] { "~/stopMetricCollection.sh"}));

		if (!runScript(target, script)){
			return runScript(target, generateScript("stopMetricCollection.sh"));
		}
		return Boolean.TRUE;
	}

	private static Scriptlet generateScript(String scriptName) {
		Scriptlet script = new Scriptlet();
		script.setScripts(Arrays
				.asList(new String[] { "echo '#self-generated script' > ~/"+scriptName, "chmod 755 ~/"+scriptName}));
		
		logger.log(Level.WARNING, "generating script ~/"+scriptName);
		return script;
	}
	
	private static Boolean startMetricCollection(VirtualMachine target) {
		Scriptlet script = new Scriptlet();
		script.setScripts(Arrays
				.asList(new String[] { "~/startMetricCollection.sh "+target.getId()}));

		if (!runScript(target, script)){
			return runScript(target, generateScript("startMetricCollection.sh"));
		}
		return Boolean.TRUE;
	}

	private static Boolean runScript(VirtualMachine target, Scriptlet script) {
		return ComputeProvider.runScript(target, script, target.getPublicIpAddress(), logger) != null;
	}

	private static Boolean sendWorkloadValue(VirtualMachine target, String value) {
		Scriptlet script = new Scriptlet();
		script.setScripts(Arrays
				.asList(new String[] { "~/workload.sh " + value }));

		if (!runScript(target, script)){
			script = new Scriptlet();
			script.setScripts(Arrays
					.asList(new String[] { "echo '#self-generated script' > ~/workload.sh", "echo 'echo \"please specify what to do with workload\" $1' >> ~/workload.sh" , "chmod 755 ~/workload.sh"}));
			
			logger.log(Level.WARNING, "generating script ~/+workload.sh");
			
			return runScript(target, script);
			
		}
		return Boolean.TRUE;
	}

	private static Boolean isThereExecutionRunning(VirtualMachine target) {
		Scriptlet script = new Scriptlet();
		script.setScripts(Arrays.asList(new String[] { "~/running.sh" }));		
		String output = ComputeProvider.runScript(target, script, target.getPublicIpAddress(), logger);
		
		if (output == null){
			script = new Scriptlet();
			script.setScripts(Arrays
					.asList(new String[] { "echo '#self-generated script, return YES if there is execution running, otherwise NO' > ~/running.sh", "echo 'echo \"NO\"' >> ~/running.sh" , "chmod 755 ~/running.sh"}));
			
			logger.log(Level.WARNING, "generating script ~/running.sh");
			
			runScript(target, script);
		}

		return "YES".equals(output);

	}
	
	private static List<MetricEval> getResults(List<VirtualMachine> targets) throws YamlException {
		Scriptlet script = new Scriptlet();
		script.setScripts(Arrays.asList(new String[] { "~/results.sh" }));
		
		StringBuffer buffer = new StringBuffer();
		for (VirtualMachine target : targets) {
			String targetResult = ComputeProvider.runScript(target, script,
					target.getPublicIpAddress(), logger);
			if (targetResult == null){
				throw new NotImplementedException("You must specify a script named ~/results.sh that returns the collected metrics in YAML");
			}else{
				buffer.append(targetResult);
			}
		}
		
		logger.info(buffer.toString());
		
		return YamlLoader.getMetricResult(buffer.toString()).getMetrics();
	}
	
	public static Boolean execTests(Scenario scenario, Benchmark benchmark,
			WorkloadFunction workload, List<VirtualMachine> targets)
			throws Exception, InterruptedException {
		Boolean retorno = Boolean.FALSE;

		for (String workloadValue : workload.getValuesList()) {
			
			
			for (Metric metrics : scenario.getMetrics()) {
				for (VirtualMachine target : metrics.getTargets()){
					startMetricCollection(target);
				}
			}
			

			for (VirtualMachine target : targets) {
				sendWorkloadValue(target, workloadValue);
			}

			for (VirtualMachine target : targets) {
				while (isThereExecutionRunning(target)) {
					Thread.sleep(10000);
				}
			}

			List<MetricEval> results = getResults(targets);
			
			for (Metric metrics : scenario.getMetrics()) {
				for (VirtualMachine target : metrics.getTargets()){
					stopMetricCollection(target);
				}
			}
			
			BenchmarkPartialResult execution = new BenchmarkPartialResult();

			execution.setBenchmark(benchmark);

			execution.setWhen(Calendar.getInstance().getTimeInMillis());

			execution.setScenario(scenario);

			execution.setWorkload(workloadValue);

			retorno |= persistResults(execution, results, benchmark);

			if (!retorno) {
				logger.info("no successful execution for this workload");
			}

		}

		return retorno;
	}
	
	private static Boolean persistResults(BenchmarkPartialResult execution,
			List<MetricEval> results, Benchmark benchmark) {
		Boolean passed = Boolean.TRUE;		

		logger.info("results " + results);
		execution.setMetricsEval(results);
		execution.setSuccess(passed);

		BenchmarkPartialResult.publishResult(execution,
				benchmark.getResultEndPoint());

		return passed;
	}

}