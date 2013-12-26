package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public class InstanceTypeCapacityLevels extends CrawlerArtifact {
	
	private List<VirtualMachineType> instanceTypes;

	public void setInstanceTypes(List<VirtualMachineType> instanceTypes) {
		this.instanceTypes = instanceTypes;
	}

	public List<VirtualMachineType> getInstanceTypes() {
		return instanceTypes;
	}

	
}
