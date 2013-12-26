package br.mia.unifor.crawler.executer.artifact;


import java.util.List;

public class Workload extends CrawlerArtifact {
	
	private List<WorkloadFunction> functions;
	private List<VirtualMachine> targets;
	
	
	public List<VirtualMachine> getTargets() {
		return targets;
	}
	public void setTargets(List<VirtualMachine> targets) {
		this.targets = targets;
	}
	public List<WorkloadFunction> getFunctions() {
		return functions;
	}
	public void setFunctions(List<WorkloadFunction> functions) {
		this.functions = functions;
	}
}
