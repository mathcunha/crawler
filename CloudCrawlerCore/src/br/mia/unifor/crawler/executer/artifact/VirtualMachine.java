package br.mia.unifor.crawler.executer.artifact;


import java.util.Hashtable;
import java.util.Map;


public class VirtualMachine extends CrawlerArtifact{
	private VirtualMachineType type;
	private String name;
	private Map<String, Scriptlet> scripts;
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

	public Map<String, Scriptlet> getScripts() {
		return scripts;
	}

	public void setScripts(Map<String, Scriptlet> scripts) {
		this.scripts = scripts;
	}	
}