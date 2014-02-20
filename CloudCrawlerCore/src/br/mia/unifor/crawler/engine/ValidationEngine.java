package br.mia.unifor.crawler.engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Provider;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;

public class ValidationEngine {
	public static void validate(Benchmark benchmark) throws ValidationException, FileNotFoundException, IOException{
		StringBuffer buffer = new StringBuffer();
		
		boolean deployment = validateVirtualMachines(benchmark.getVirtualMachines(), buffer);
		
		for (Scenario scenario : benchmark.getScenarios()) {
			validateVirtualMachines(new ArrayList<VirtualMachine>(scenario.getVirtualMachines().values()), buffer);
		}
		
		if(buffer.length() > 0){//has errors
			throw new ValidationException(buffer.toString());
		}
		
	}

	private static boolean validateVirtualMachines(List<VirtualMachine> virtualMachines, StringBuffer buffer) {
		boolean deployment = false;
		for (VirtualMachine virtualMachine : virtualMachines) {
			
			if(isStringEmpty(virtualMachine.getId())){
				buffer.append("The virtualMachine must have a unique id \n");
			}
			
			if(isStringEmpty(virtualMachine.getProviderId()) && isStringEmpty(virtualMachine.getPublicIpAddress()) && isStringEmpty(virtualMachine.getImage())){
				buffer.append("The virtualMachine must have one of this attributes providerId, or image, or publicIpAddress \n");
			}
			
			deployment = deployment || !isStringEmpty(virtualMachine.getImage());
			if(!isStringEmpty(virtualMachine.getImage())){
				if(isStringEmpty(virtualMachine.getName())){
					buffer.append("The virtualMachine must have a name \n");
				}
			}
		}
		
		return deployment; 
	}
	
	private static boolean isStringEmpty(String string){
		return (string == null || string.trim().length() == 0);
	}
}
