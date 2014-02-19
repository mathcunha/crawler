package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public class Scriptlet extends CrawlerArtifact{
	private List<String> scripts;
	private String name;

	public void setScripts(List<String> scripts) {
		this.scripts = scripts;
	}

	public List<String> getScripts() {
		return scripts;
	}	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scriptlet other = (Scriptlet) obj;
		if (super.getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!super.getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Scriptlet [scripts=" + scripts + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}