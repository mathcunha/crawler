package br.mia.unifor.crawler.builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jclouds.compute.domain.Template;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;

import br.mia.unifor.crawler.executer.artifact.Provider;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest;

public class EC2ComputeProvider extends ComputeProvider {
	protected static AmazonEC2 ec2;

	public EC2ComputeProvider(Provider provider) throws IOException {
		super(provider);

		AWSCredentials credentials;
		
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(provider.getCredentialPath());
		if(input == null){			
			input = new FileInputStream(provider.getCredentialPath());
		}

		credentials = new PropertiesCredentials(input);
		ec2 = new AmazonEC2Client(credentials);
		input.close();

	}

	public boolean changeInstanceType(VirtualMachine instance) {
		
		String id = instance.getProviderId().split("/")[1];
		
		ModifyInstanceAttributeRequest request = new ModifyInstanceAttributeRequest(
				id, "instanceType");
		request.setValue(instance.getType().getProviderProfile());

		ec2.modifyInstanceAttribute(request);

		return true;
	}

	@Override
	public void getTemplate(Template template) {
		Properties properties;
		try {
			properties = provider.getPropertiesClass();
			template.getOptions().as(EC2TemplateOptions.class).keyPair(properties.getProperty("key-pair"));			
			template.getOptions().as(EC2TemplateOptions.class).securityGroups(properties.getProperty("sec-group"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
