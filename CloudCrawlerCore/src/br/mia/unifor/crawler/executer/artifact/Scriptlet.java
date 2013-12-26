package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public class Scriptlet {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Scriptlet [scripts=" + scripts + "]";
	}

	private List<String> scripts;
	private Integer id;

	public void setScripts(List<String> scripts) {
		this.scripts = scripts;
	}

	public List<String> getScripts() {
		return scripts;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	
}
