package br.mia.unifor.crawler.executer.artifact;

import java.util.Map;

public class Scenario extends CrawlerArtifact{
	private Workload workload;	
	private Map<String, VirtualMachine> metric;
	private String name;
	private Map<String, VirtualMachine> virtualMachines;
	
	
	@Override
	public Scenario clone(){
		Scenario scenario = new Scenario();
		
		scenario.setWorkload(getWorkload());
		scenario.setName(getName());
		scenario.setMetric(getMetric());
		
		return scenario;
	}

	

	
	public void setWorkload(Workload workload) {
		this.workload = workload;
	}

	public Workload getWorkload() {
		return workload;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}

	public void setVirtualMachines(Map<String, VirtualMachine> virtualMachines) {
		this.virtualMachines = virtualMachines;
	}




	public Map<String, VirtualMachine> getMetric() {
		return metric;
	}




	public void setMetric(Map<String, VirtualMachine> metric) {
		this.metric = metric;
	}
}
