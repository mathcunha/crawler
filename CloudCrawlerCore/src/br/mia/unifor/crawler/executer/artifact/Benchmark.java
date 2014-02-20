package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

import br.mia.unifor.crawler.engine.ValidationException;


public class Benchmark extends CrawlerArtifact{
	private String name;
	private Integer beginHour;
	private Integer beginMinute;
	private Long interval;
	private Integer rounds;
	private String className;
	private String resultEndPoint;
	private List<VirtualMachineType> virtualMachineTypes;
	private List<VirtualMachine> virtualMachines;	
	private List<InstanceTypeCapacityLevels> instanceTypeCapacityLevels;
	private List<Workload> workloads;
	private List<Scenario> scenarios;
	private List<Provider> providers;
	private List<Scriptlet> scriptlets;
	private List<Component> components;

	public Integer getBeginHour() {
		return beginHour;
	}

	public void setBeginHour(Integer beginHour) {
		this.beginHour = beginHour;
	}

	public Integer getBeginMinute() {
		return beginMinute;
	}

	public void setBeginMinute(Integer beginMinute) {
		this.beginMinute = beginMinute;
	}

	public Long getInterval() {
		return interval;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}

	public Integer getRounds() {
		return rounds;
	}

	public void setRounds(Integer round) {
		this.rounds = round;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<VirtualMachineType> getVirtualMachineTypes() {
		return virtualMachineTypes;
	}

	public void setVirtualMachineTypes(List<VirtualMachineType> virtualMachineTypes) {
		this.virtualMachineTypes = virtualMachineTypes;
	}

	public void setVirtualMachines(List<VirtualMachine> virtualMachine) {
		this.virtualMachines = virtualMachine;
	}

	public List<VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}	

	public void setInstanceTypeCapacityLevels(
			List<InstanceTypeCapacityLevels> instanceTypeCapacityLevels) {
		this.instanceTypeCapacityLevels = instanceTypeCapacityLevels;
	}

	public List<InstanceTypeCapacityLevels> getInstanceTypeCapacityLevels() {
		return instanceTypeCapacityLevels;
	}

	public void setScenarios(List<Scenario> scenario) {
		this.scenarios = scenario;
	}

	public List<Scenario> getScenarios() {
		return scenarios;
	}

	public void setWorkloads(List<Workload> workloads) {
		this.workloads = workloads;
	}

	public List<Workload> getWorkloads() {
		return workloads;
	}

	public void setProviders(List<Provider> providers) {
		this.providers = providers;
	}

	public List<Provider> getProviders() {
		return providers;
	}

	public void setScriptlets(List<Scriptlet> scriptlets) {
		this.scriptlets = scriptlets;
	}

	public List<Scriptlet> getScriptlets() {
		return scriptlets;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public List<Component> getComponents() {
		return components;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static void validate(Benchmark benchmark) throws ValidationException{
		//TODO - 
	}

	public String getResultEndPoint() {
		return resultEndPoint;
	}

	public void setResultEndPoint(String resultEndPoint) {
		this.resultEndPoint = resultEndPoint;
	}

}
