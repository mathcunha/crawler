package br.mia.unifor.crawlerenvironment.mom.event;

import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.CrawlerArtifact;

public class BenchmarkEvent extends CloudCrawlerEnvironmentEvent {
	
	public static String ACTION_NEW_SCENARIO = "new_scenario";
	public static String ACTION_END_SCENARIO = "end_scenario";
	public static String ACTION_NEW_WORKLOAD = "new_workload";
	
	private Benchmark benchmark;
	private CrawlerArtifact target;
	private Integer id;
	
	public BenchmarkEvent(){
		
	}

	public BenchmarkEvent(Integer id, String action, CrawlerArtifact target, Benchmark benchmark) {
		super(action);
		this.setId(id);
		this.setBenchmark(benchmark);
		this.setTarget(target);
	}

	public Benchmark getBenchmark() {
		return benchmark;
	}

	public void setBenchmark(Benchmark benchmark) {
		this.benchmark = benchmark;
	}

	public CrawlerArtifact getTarget() {
		return target;
	}

	public void setTarget(CrawlerArtifact target) {
		this.target = target;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	

}
