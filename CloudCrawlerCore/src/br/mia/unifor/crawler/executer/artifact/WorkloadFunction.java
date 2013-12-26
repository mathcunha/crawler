package br.mia.unifor.crawler.executer.artifact;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

public class WorkloadFunction extends CrawlerArtifact {
	
	private String values;
	private String type;

	public void setValues(String values) {
		this.values = values;
	}
	
	protected void generateValues(){
		//TODO
		throw new NotImplementedException();
	}
	
	public String getValues() {
		if (null != type){
			generateValues();
		}
		return values;
	}

	public List<String> getValuesList() {
		return Arrays.asList(values.split(","));
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
