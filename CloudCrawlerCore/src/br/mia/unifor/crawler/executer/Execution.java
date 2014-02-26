package br.mia.unifor.crawler.executer;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.mia.unifor.crawler.builder.ComputeProvider;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.Scriptlet;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;
import br.mia.unifor.crawler.executer.artifact.WorkloadFunction;
import br.mia.unifor.crawler.parser.ScriptParser;

public class Execution {
	protected static Logger logger = Logger
			.getLogger(Execution.class.getName());	

	private static Boolean runScript(Scenario scenario, VirtualMachine target, Scriptlet script) {
		
		Scriptlet scriptParsed = ScriptParser.parse(scenario, script, target);
		
		return ComputeProvider.runScript(target, scriptParsed, target.getPublicIpAddress(), logger) != null;
	}

	private static void sendWorkloadValue(Scenario scenario, VirtualMachine target, String value) {

		Scriptlet scriptParsed = ScriptParser.parse(scenario, target.getScripts().get("submit_workload"), target);
				
		for (int i = 0; i < scriptParsed.getScripts().size(); i++) {
			scriptParsed.getScripts().set(i, scriptParsed.getScripts().get(i)+" "+value);
		}
		
		logger.info(ComputeProvider.runScript(target, scriptParsed, target.getPublicIpAddress(), logger));
	}

	private static Boolean isThereExecutionRunning(Scenario scenario, VirtualMachine target) {
		Scriptlet script = target.getScripts().get("running");
		String output = null;
		
		if(script != null)
				output = ComputeProvider.runScript(target, script, target.getPublicIpAddress(), logger);
		
		if (output == null){
			script = new Scriptlet();
			script.setScripts(Arrays
					.asList(new String[] { "echo '#self-generated script, return YES if there is execution running, otherwise NO' > ~/running.sh", "echo 'echo \"NO\"' >> ~/running.sh" , "chmod 755 ~/running.sh"}));
			
			logger.log(Level.WARNING, "generating script ~/running.sh");
			
			runScript(scenario, target, script);
		}

		return "YES".equals(output);

	}	
	
	public static void execTests(Scenario scenario, Benchmark benchmark,
			WorkloadFunction workload, List<VirtualMachine> targets)
			throws Exception, InterruptedException {		

		for (String workloadValue : workload.getValuesList()) {
			
			for (VirtualMachine target : scenario.getMetric().values()){
				runScript(scenario, target, target.getScripts().get("start_metric"));
			}
			
			for (VirtualMachine target : targets) {
				logger.info("Sending workload "+workloadValue);
				sendWorkloadValue(scenario, target, workloadValue);
				logger.info("workload "+workloadValue+" sent");
			}

			for (VirtualMachine target : targets) {
				while (isThereExecutionRunning(scenario, target)) {
					Thread.sleep(10000);
				}
			}			
			
			for (VirtualMachine target : scenario.getMetric().values()){
				runScript(scenario, target, target.getScripts().get("stop_metric"));
			}
			
			/*BenchmarkPartialResult execution = new BenchmarkPartialResult();

			execution.setBenchmark(benchmark);

			execution.setWhen(Calendar.getInstance().getTimeInMillis());

			execution.setScenario(scenario);

			execution.setWorkload(workloadValue);

			retorno |= persistResults(execution, results, benchmark);*/
			

		}
		
	}
	
	/*private static Scriptlet generateScript(String scriptName) {
		Scriptlet script = new Scriptlet();
		script.setScripts(Arrays
				.asList(new String[] { "echo '#self-generated script' > ~/"+scriptName, "chmod 755 ~/"+scriptName}));
		
		logger.log(Level.WARNING, "generating script ~/"+scriptName);
		return script;
	}*/
}