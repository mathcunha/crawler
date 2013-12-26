package br.mia.unifor.crawler.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import br.mia.unifor.crawler.executer.artifact.Application;
import br.mia.unifor.crawler.executer.artifact.ApplicationImpl;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Component;
import br.mia.unifor.crawler.executer.artifact.InstanceTypeCapacityLevels;
import br.mia.unifor.crawler.executer.artifact.Metric;
import br.mia.unifor.crawler.executer.artifact.MetricEval;
import br.mia.unifor.crawler.executer.artifact.Provider;
import br.mia.unifor.crawler.executer.artifact.Result;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.Scriptlet;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;
import br.mia.unifor.crawler.executer.artifact.VirtualMachineType;
import br.mia.unifor.crawler.executer.artifact.Workload;
import br.mia.unifor.crawler.executer.artifact.WorkloadFunction;


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;


public class YamlLoader {
	public static Benchmark loadTest(InputStream in) throws YamlException{
		YamlReader reader = new YamlReader(new InputStreamReader(in));
		reader.getConfig().setClassTag("virtualMachineType",VirtualMachineType.class);
		reader.getConfig().setClassTag("virtualMachine",VirtualMachine.class);
		reader.getConfig().setClassTag("metric",Metric.class);
		reader.getConfig().setClassTag("instanceTypeCapacityLevel",InstanceTypeCapacityLevels.class);
		reader.getConfig().setClassTag("workload",Workload.class);
		reader.getConfig().setClassTag("workloadFunction",WorkloadFunction.class);
		reader.getConfig().setClassTag("scenario",Scenario.class);
		reader.getConfig().setClassTag("provider",Provider.class);
		reader.getConfig().setClassTag("scriptlet",Scriptlet.class);
		reader.getConfig().setClassTag("component",Component.class);
		reader.getConfig().setClassTag("application",ApplicationImpl.class);
		
		reader.getConfig().setClassTag("benchmark",Benchmark.class);
		//reader.getConfig().setPropertyElementType(InstanceTypeClass.class, "instanceTypes", InstanceType.class);
		//reader.getConfig().setPropertyElementType(br.unifor.ow2.onaga.cloud.model.Test.class, "scenarios", br.unifor.ow2.onaga.cloud.model.Scenario.class);
		//reader.getConfig().setPropertyElementType(br.unifor.ow2.onaga.cloud.model.Scenario.class, "instances", br.unifor.ow2.onaga.cloud.model.Instance.class);
		
		
		Benchmark test = (Benchmark) reader.read();
		
		return test;
	}
	
	public static Result getMetricResult(String result) throws YamlException{
		YamlReader reader = new YamlReader(result);
		reader.getConfig().setClassTag("result",Result.class);
		reader.getConfig().setClassTag("metric",Metric.class);
		reader.getConfig().setClassTag("metricEval",MetricEval.class);
		reader.getConfig().setClassTag("collectedMetrics",MetricEval.CollectedMetric.class);
		
		
		return (Result) reader.read();
	}
	
	public static String getYaml(Object obj) {
		StringWriter strWriter = new StringWriter();
		YamlWriter writer = new YamlWriter(strWriter);
		try {			
			writer.write(obj);
			writer.close();
		} catch (YamlException e) {
			e.printStackTrace();
		}
		
		return strWriter.getBuffer().toString();
	}
	
	public static void main(String[] args) {
		try {
			
			Benchmark test = loadTest(YamlLoader.class.getClass().getResourceAsStream(
			"/br/template.yml"));
			
			Application appliance = test.getScenarios().get(0).getApplication();
			
			VirtualMachine instance = appliance.getVirtualMachines().get(2);
			
			ScriptParser.parse(appliance, instance.getOnStartup().get(0), instance);
			
			System.out.println(test);
			
			
		} catch (YamlException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}