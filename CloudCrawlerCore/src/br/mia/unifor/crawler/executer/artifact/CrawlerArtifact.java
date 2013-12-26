package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public class CrawlerArtifact {

	private String id;
	private List<String> properties;

	public void setProperties(List<String> propertiesArray) {
		this.properties = propertiesArray;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}
