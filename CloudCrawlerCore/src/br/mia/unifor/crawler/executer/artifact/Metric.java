package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public class Metric extends CrawlerArtifact{
	private String name;
	
	private List<VirtualMachine> targets;

	public List<VirtualMachine> getTargets() {
		return targets;
	}

	public void setTargets(List<VirtualMachine> targets) {
		this.targets = targets;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
