package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public interface Application {
	
	public boolean setUp();
	
	public boolean teardown();
	
	public List<VirtualMachine> getVirtualMachines();
	
	public String getDescription();
}
