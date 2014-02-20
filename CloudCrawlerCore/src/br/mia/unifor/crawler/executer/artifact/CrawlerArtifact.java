package br.mia.unifor.crawler.executer.artifact;

import java.util.Map;

public class CrawlerArtifact {

	private String id;
	private Map<String, Object> properties;

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

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

}
