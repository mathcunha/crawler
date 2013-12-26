package br.mia.unifor.crawler.executer.artifact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Provider extends CrawlerArtifact {
	private String name;
	private String credentialPath;
	private String userName;
	private String privateKey;
	private String password;
	
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setCredentialPath(String credentialPath) {
		this.credentialPath = credentialPath;
	}
	public String getCredentialPath() {
		return credentialPath;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void loadProperties()
			throws FileNotFoundException, IOException {
		Properties properties = getPropertiesClass();

		setUserName(properties.getProperty("userName"));
		setPrivateKey(properties.getProperty("privateKey"));
		setPassword(properties.getProperty("password"));
	}
	
	public Properties getPropertiesClass() throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(getCredentialPath());
		if(input == null){			
			input = new FileInputStream(getCredentialPath());
		}
		
		properties.load(input);
		return properties;
	}
}
