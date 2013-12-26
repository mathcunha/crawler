package br.mia.unifor.crawler.executer.artifact.security;

import br.mia.unifor.crawler.executer.artifact.CrawlerArtifact;

public class Credential extends CrawlerArtifact{
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
