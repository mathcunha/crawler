package br.mia.unifor.crawlerenvironment.mom.event;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Component;
import br.mia.unifor.crawler.executer.artifact.InstanceTypeCapacityLevels;
import br.mia.unifor.crawler.executer.artifact.Provider;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.Scriptlet;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;
import br.mia.unifor.crawler.executer.artifact.VirtualMachineType;
import br.mia.unifor.crawler.executer.artifact.Workload;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

public class EventSerializer {
	public static String getYaml(CloudCrawlerEnvironmentEvent obj) {
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

	public static BenchmarkEvent loadBenchmarkEvent(String body)
			throws YamlException {
		InputStream in = new ByteArrayInputStream(body.getBytes());

		YamlReader reader = new YamlReader(new InputStreamReader(in));
		reader.getConfig().setClassTag("benchmarkEvent", BenchmarkEvent.class);
		reader.getConfig().setClassTag("virtualMachineType",
				VirtualMachineType.class);
		reader.getConfig().setClassTag("virtualMachine", VirtualMachine.class);
		reader.getConfig().setClassTag("instanceTypeCapacityLevel",
				InstanceTypeCapacityLevels.class);
		reader.getConfig().setClassTag("workload", Workload.class);
		reader.getConfig().setClassTag("scenario", Scenario.class);
		reader.getConfig().setClassTag("provider", Provider.class);
		reader.getConfig().setClassTag("scriptlet", Scriptlet.class);
		reader.getConfig().setClassTag("component", Component.class);

		reader.getConfig().setClassTag("benchmark", Benchmark.class);

		BenchmarkEvent lBenchmarkEvent = (BenchmarkEvent) reader.read();

		return lBenchmarkEvent;
	}

	/*public static void main(String[] args) throws ValidationException, FileNotFoundException, IOException {

		Benchmark benchmark = EngineAsync.load(new FileInputStream(
				"../examples/wordpress/wordpress.yml"), Boolean.FALSE);
		BenchmarkEvent event = new BenchmarkEvent(1, BenchmarkEvent.ACTION_NEW,
				benchmark, benchmark);

		String body = getYaml(event);

		System.out.println(body);

		BenchmarkEvent benchmark2 = loadBenchmarkEvent(body);
		
		Scenario scenario = benchmark2.getBenchmark().getScenarios().get(0);
		scenario.getVirtualMachines().get("gatling").setPublicIpAddress("ip.gatling");
		VirtualMachine vm = scenario.getVirtualMachines().get("wordpress");
		vm.setPublicIpAddress("ip.wordpress");
		Scriptlet scriptlet = vm.getScripts().get("start_vm");
		System.out.println(ScriptParser.parse(scenario, scriptlet , vm));

		System.out.println(benchmark2);

	}*/
}
