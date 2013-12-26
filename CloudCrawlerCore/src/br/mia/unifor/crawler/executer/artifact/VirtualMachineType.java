package br.mia.unifor.crawler.executer.artifact;

public class VirtualMachineType extends CrawlerArtifact{
	private Integer cpu;
	private Integer ram;
	private String providerProfile;
	
	private Provider provider;
	
	public Integer getCpu() {
		return cpu;
	}
	public void setCpu(Integer cpu) {
		this.cpu = cpu;
	}
	public Integer getRam() {
		return ram;
	}
	public void setRam(Integer ram) {
		this.ram = ram;
	}
	public String getProviderProfile() {
		return providerProfile;
	}
	public void setproviderProfile(String providerProfile) {
		this.providerProfile = providerProfile;
	}
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	public Provider getProvider() {
		return provider;
	}	
}
