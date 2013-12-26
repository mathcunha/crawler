package br.mia.unifor.crawlerenvironment.view.model;

import java.util.List;

public class FileStore {
	private String name;
	private String status;
	private List<FileStore> files;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<FileStore> getFiles() {
		return files;
	}
	public void setFiles(List<FileStore> files) {
		this.files = files;
	}
}
