package br.mia.unifor.crawlerenvironment.mom.event;

import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.CrawlerArtifact;
import br.mia.unifor.crawler.executer.artifact.Scenario;

public class WorkloadEvent extends BenchmarkEvent {
	
	private Scenario scenario ;
	
	public WorkloadEvent(){
		
	}
	
	public WorkloadEvent(Integer id, String action, CrawlerArtifact target, Benchmark benchmark, Scenario scenario) {
		super(id, action, target, benchmark);
		this.setScenario(scenario);
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
}
