package br.mia.unifor.crawler.executer.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scenario extends CrawlerArtifact{
	private Workload workload;	
	private Map<String, VirtualMachine> metric;
	private String name;
	private Boolean endable = Boolean.TRUE;
	
	
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

	public Map<String, VirtualMachine> getMetric() {
		return metric;
	}

	public void setMetric(Map<String, VirtualMachine> metric) {
		this.metric = metric;
	}

	public Boolean getEndable() {
		return endable;
	}

	public void setEndable(Boolean endable) {
		this.endable = endable;
	}
	
	public Collection<VirtualMachine> getLocalVirtualMachines(){
		Set<VirtualMachine> virtualMachines = new HashSet<VirtualMachine>(getMetric().values());
		virtualMachines.addAll(getWorkload().getTargets());
		return virtualMachines;
	}
	
}
