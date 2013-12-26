package br.mia.unifor.crawler.executer.artifact;

import java.util.List;



public class Component extends CrawlerArtifact {

	private List<Scriptlet> config;
	private List<Scriptlet> onStartup;
	private List<Scriptlet> onShutdown;
	
	public List<Scriptlet> getConfig() {
		return config;
	}
	public void setConfig(List<Scriptlet> config) {
		this.config = config;
	}
	public List<Scriptlet> getOnStartup() {
		return onStartup;
	}
	public void setOnStartup(List<Scriptlet> onStartup) {
		this.onStartup = onStartup;
	}
	public List<Scriptlet> getOnShutdown() {
		return onShutdown;
	}
	public void setOnShutdown(List<Scriptlet> onShutdown) {
		this.onShutdown = onShutdown;
	}
	
}
