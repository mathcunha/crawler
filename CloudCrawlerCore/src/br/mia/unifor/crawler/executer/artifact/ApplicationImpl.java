package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public class ApplicationImpl  extends CrawlerArtifact implements Application {
	
	private String description;
	private List<VirtualMachine> virtualMachines;

	@Override
	public boolean setUp() {
		return true;
	}

	@Override
	public boolean teardown() {
		return true;
	}

	
	
	public void setDescription(String description){
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}

	public void setVirtualMachines(List<VirtualMachine> virtualMachines) {
		this.virtualMachines = virtualMachines;
	}

}
