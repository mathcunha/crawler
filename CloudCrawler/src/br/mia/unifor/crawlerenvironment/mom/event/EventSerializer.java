package br.mia.unifor.crawlerenvironment.mom.event;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import br.mia.unifor.crawler.executer.artifact.ApplicationImpl;
import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Component;
import br.mia.unifor.crawler.executer.artifact.InstanceTypeCapacityLevels;
import br.mia.unifor.crawler.executer.artifact.Metric;
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
		reader.getConfig().setClassTag("metric", Metric.class);
		reader.getConfig().setClassTag("instanceTypeCapacityLevel",
				InstanceTypeCapacityLevels.class);
		reader.getConfig().setClassTag("workload", Workload.class);
		reader.getConfig().setClassTag("scenario", Scenario.class);
		reader.getConfig().setClassTag("provider", Provider.class);
		reader.getConfig().setClassTag("scriptlet", Scriptlet.class);
		reader.getConfig().setClassTag("component", Component.class);
		reader.getConfig().setClassTag("application", ApplicationImpl.class);

		reader.getConfig().setClassTag("benchmark", Benchmark.class);

		BenchmarkEvent lBenchmarkEvent = (BenchmarkEvent) reader.read();

		return lBenchmarkEvent;
	}

	// public static void main(String[] args) {
	//
	// try {
	// Benchmark benchmark =
	// EngineAsync.load(Thread.currentThread().getContextClassLoader()
	// .getResourceAsStream("specjvm2008.yml"));
	// BenchmarkEvent event = new BenchmarkEvent(BenchmarkEvent.ACTION_NEW,
	// benchmark, benchmark);
	//
	// String body = getYaml(event);
	//
	// System.out.println(body);
	//
	// InputStream is = new ByteArrayInputStream(body.getBytes());
	//
	// BenchmarkEvent benchmark2 = loadBenchmarkEvent(is);
	//
	// System.out.println(benchmark2);
	//
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
