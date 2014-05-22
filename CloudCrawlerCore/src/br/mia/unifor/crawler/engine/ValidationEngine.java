package br.mia.unifor.crawler.engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;

public class ValidationEngine {
	public static void validate(Benchmark benchmark) throws ValidationException, FileNotFoundException, IOException{
		StringBuffer buffer = new StringBuffer();
		
		validateVirtualMachines(benchmark.getVirtualMachines(), buffer);		
		
		for (Scenario scenario : benchmark.getScenarios()) {
			validateScenario(scenario, buffer);
			validateVirtualMachines(new ArrayList<VirtualMachine>(scenario.getLocalVirtualMachines()), buffer);
		}
		
		if(buffer.length() > 0){//has errors
			throw new ValidationException(buffer.toString());
		}
		
	}
	
	private static void validateScenario(Scenario scenario, StringBuffer buffer){
		if(isStringEmpty(scenario.getName())){
			buffer.append("The scenario must have a name \n");
		}
		
		if(scenario.getWorkload() == null || scenario.getWorkload().getTargets() == null || scenario.getWorkload().getTargets().size() == 0){
			buffer.append("At least one virtual machine must be target of the workload\n");
		}else{
			for (VirtualMachine vm : scenario.getWorkload().getTargets()) {
				if(vm.getScripts().get("submit_workload") == null){
					buffer.append("the workload vm must have a script named submit_workload\n");
				}
			}
		}
		
		for (VirtualMachine vm : scenario.getMetric().values()){
			if(vm.getScripts().get("start_metric") == null){
				buffer.append("the metric vm must have a script named start_metric\n");
			}
			if(vm.getScripts().get("stop_metric") == null){
				buffer.append("the metric vm must have a script named stop_metric\n");
			}
		}
	}
	
	public static void validateScenario(Scenario scenario) throws ValidationException{
		StringBuffer buffer = new StringBuffer();
		
		validateScenario(scenario, buffer);
		validateVirtualMachines(new ArrayList<VirtualMachine>(scenario.getLocalVirtualMachines()), buffer);
		
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
