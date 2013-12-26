package br.mia.unifor.crawler.executer.artifact;


import java.util.List;


public class VirtualMachine extends CrawlerArtifact{
	private VirtualMachineType type;
	private String name;
	
	private List<Scriptlet> create;
	private List<Scriptlet> onStartup;
	private List<Scriptlet> onShutdown;
	private List<Component> components;
	
	private Boolean temporary = false;
	private String image;
	private String providerId;
	private String publicIpAddress;
	private String privateIpAddress;


	public VirtualMachineType getType() {
		return type;
	}

	public void setType(VirtualMachineType type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VirtualMachine other = (VirtualMachine) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public void setPublicIpAddress(String publicIpAddress) {
		this.publicIpAddress = publicIpAddress;
	}

	public String getPublicIpAddress() {
		return publicIpAddress;
	}

	public void setPrivateIpAddress(String privateIpAddress) {
		this.privateIpAddress = privateIpAddress;
	}

	public String getPrivateIpAddress() {
		return privateIpAddress;
	}

	public void setOnStartup(List<Scriptlet> onStartup) {
		this.onStartup = onStartup;
	}

	public List<Scriptlet> getOnStartup() {
		return onStartup;
	}

	public void setOnShutdown(List<Scriptlet> onShutdown) {
		this.onShutdown = onShutdown;
	}

	public List<Scriptlet> getOnShutdown() {
		return onShutdown;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public List<Component> getComponents() {
		return components;
	}

	public List<Scriptlet> getCreate() {
		return create;
	}

	public void setCreate(List<Scriptlet> create) {
		this.create = create;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getTemporary() {
		return temporary;
	}

	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}	
}