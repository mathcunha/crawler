package br.mia.unifor.crawler.executer.artifact;

public class Scenario extends CrawlerArtifact{
	private Workload workload;
	private Application application;
	private Metric metric;
	
	
	@Override
	public Scenario clone(){
		Scenario scenario = new Scenario();
		
		scenario.setWorkload(getWorkload());
		scenario.setApplication(getApplication());
		scenario.setMetric(getMetric());
		
		return scenario;
	}

	public String getDescription(){
		return getApplication().getDescription();
	}


	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Application getApplication() {
		return application;
	}

	public void setWorkload(Workload workload) {
		this.workload = workload;
	}

	public Workload getWorkload() {
		return workload;
	}
}
