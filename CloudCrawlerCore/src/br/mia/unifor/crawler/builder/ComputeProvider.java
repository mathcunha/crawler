package br.mia.unifor.crawler.builder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.telnet.TelnetClient;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import br.mia.unifor.crawler.executer.artifact.Application;
import br.mia.unifor.crawler.executer.artifact.Component;
import br.mia.unifor.crawler.executer.artifact.Provider;
import br.mia.unifor.crawler.executer.artifact.Scriptlet;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;
import br.mia.unifor.crawler.executer.artifact.VirtualMachineType;
import br.mia.unifor.crawler.parser.ScriptParser;

public abstract class ComputeProvider {
	protected ComputeServiceContext context = null;
	protected static Logger logger = Logger.getLogger(ComputeProvider.class.getName());
	protected final Provider provider;

	public ComputeProvider(Provider provider) throws IOException {
		this.provider = provider;
		Properties properties = new Properties();

		InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(provider.getCredentialPath());
		if (input == null) {
			input = new FileInputStream(provider.getCredentialPath());
		}

		properties.load(input);
		 
		this.context = ContextBuilder.newBuilder(provider.getName())
                .credentials(properties.getProperty("accessKey"), properties.getProperty("secretKey"))
                .overrides(properties)
                .modules(ImmutableSet.<Module> of(new Log4JLoggingModule(),
                                                  new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);
	}

	public ComputeServiceContext getContext() {
		return context;
	}

	public Set<? extends Image> listImages() {
		return (Set<? extends Image>) context.getComputeService().listImages();
	}

	public void stopInstance(VirtualMachine instance) {
		NodeMetadata metadata = context.getComputeService().getNodeMetadata(
				instance.getProviderId());

		String ip = metadata.getPublicAddresses().iterator().next();

		runScript(instance, instance.getOnShutdown(), ip, logger);
		stopInstanceAction(instance);
	}

	protected void stopInstanceAction(VirtualMachine instance) {
		context.getComputeService().suspendNode(instance.getProviderId());
	}

	public static boolean validateSSHConnection(String ip) {
		TelnetClient tc = new TelnetClient();
		try {
			logger.info("trying to connect to " + ip + ":22");
			tc.connect(ip, 22);
			tc.disconnect();
			return true;
		} catch (SocketException e) {
			logger.log(Level.SEVERE, "erro ao tentar conexao"
					+ NodeMetadata.Status.RUNNING, e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "erro ao tentar conexao"
					+ NodeMetadata.Status.RUNNING, e);
		}

		return false;
	}
	
	public void createInstance(VirtualMachine instance, Application appliance){
		List <Scriptlet> scriptlets = new ArrayList<Scriptlet>();
		
		if(instance.getCreate() != null && instance.getCreate().size() > 0){
			for (Scriptlet scriptlet : instance.getCreate()) {
				scriptlets.add(ScriptParser.parse(appliance, scriptlet, instance));
			}
		}else{
			try {
				Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup(instance.getName(), 1, getTemplate(instance.getType(), instance.getImage()));
				instance.setProviderId(nodes.iterator().next().getId());
			} catch (RunNodesException e) {
				logger.log(Level.SEVERE, "unable to create the virtualMachine with id "+instance.getId());
			}
		}		
	}
	
	protected Template getTemplate(VirtualMachineType type, String image){
		Template template = context.getComputeService().templateBuilder().hardwareId(type.getProviderProfile()).imageId(image).build();
		
		getTemplate(template);
		
		return template;
	}
	
	
	public abstract void getTemplate(Template template);

	public void startInstance(VirtualMachine instance, Application appliance) {

		logger.info("the instance will be started " + instance.getProviderId());
		
		if(instance.getProviderId() == null){
			createInstance(instance, appliance);
		}

		NodeMetadata metadata = context.getComputeService().getNodeMetadata(
				instance.getProviderId());

		logger.info("the state of intance id " + instance.getProviderId() + " is ("
				+ metadata.getStatus().name() + ")");
		if (!NodeMetadata.Status.RUNNING.equals(metadata.getStatus())) {
			changeInstanceType(instance, metadata);

			startInstanceAction(instance);
			metadata = context.getComputeService().getNodeMetadata(
					instance.getProviderId());

			String ip = metadata.getPublicAddresses().iterator().next();
			
			for (int i = 0; i < 3 && !(validateSSHConnection(ip)); i++);

			if (!validateSSHConnection(ip)) {
				stopInstance(instance);
				startInstance(instance, appliance);
			} else {

				instance.setPublicIpAddress(metadata.getPublicAddresses()
						.iterator().next());

				instance.setPrivateIpAddress(metadata.getPrivateAddresses()
						.iterator().next());

				List<Scriptlet> scriptlets = new ArrayList<Scriptlet>();

				if (instance.getComponents() != null) {
					for (Component app : instance.getComponents()) {

						if (app.getConfig() != null) {
							for (Scriptlet scriptlet : app.getConfig()) {
								scriptlets.add(ScriptParser.parse(appliance,
										scriptlet, instance));
							}
						}

						if (app.getOnStartup() != null) {
							for (Scriptlet scriptlet : app.getOnStartup()) {
								scriptlets.add(ScriptParser.parse(appliance,
										scriptlet, instance));
							}
						}
					}
				}

				if (instance.getOnStartup() != null) {
					for (Scriptlet scriptlet : instance.getOnStartup()) {
						scriptlets.add(ScriptParser.parse(appliance, scriptlet,
								instance));
					}
				}

				instance.setOnStartup(scriptlets);

				runScript(instance, instance.getOnStartup(), ip, logger);
			}

		} else {
			if (!metadata.getHardware().getId()
					.equals(instance.getType().getProviderProfile())) {
				stopInstance(instance);

				changeInstanceType(instance, metadata);

				startInstance(instance, appliance);
			} else {
				instance.setPublicIpAddress(metadata.getPublicAddresses()
						.iterator().next());

				instance.setPrivateIpAddress(metadata.getPrivateAddresses()
						.iterator().next());

				List<Scriptlet> scriptlets = new ArrayList<Scriptlet>();

				if (instance.getComponents() != null) {

					for (Component app : instance.getComponents()) {
						if (app.getConfig() != null)
							for (Scriptlet scriptlet : app.getConfig()) {
								scriptlets.add(ScriptParser.parse(appliance,
										scriptlet, instance));
							}
					}
				}

				runScript(instance, scriptlets, instance.getPublicIpAddress(),
						logger);
			}
		}

		logger.info("the instace was started " + instance.getProviderId());
	}

	protected void startInstanceAction(VirtualMachine instance) {
		context.getComputeService().resumeNode(instance.getProviderId());
	}

	public static void runScript(VirtualMachine instance,
			List<Scriptlet> script, String ip, Logger logger) {
		if (script != null)
			for (Scriptlet scriptlet : script) {
				runScript(instance, scriptlet, ip, logger);
			}
	}

	public static String runScript(VirtualMachine instance, Scriptlet script,
			String ip, Logger logger) {
		if (script != null && script.getScripts().size() > 0) {

			logger.info(script.toString()+" userName "+instance.getType().getProvider().getUserName());

			if (instance.getType().getProvider().getPrivateKey() != null) {
				return SSHClient.exec(script, instance.getType().getProvider()
						.getUserName(), ip, instance.getType().getProvider()
						.getPrivateKey(), Boolean.FALSE);
			} else {
				return SSHClient.exec(script, instance.getType().getProvider()
						.getUserName(), ip, instance.getType().getProvider()
						.getPassword(), Boolean.TRUE);
			}
		}
		
		return null;
	}

	public boolean changeInstanceType(VirtualMachine instance,
			NodeMetadata metadata) {

		logger.info("change instance resource " + instance.getProviderId());

		if (!metadata.getHardware().getId()
				.equals(instance.getType().getProviderProfile())) {
			changeInstanceType(instance);
		}

		logger.info("instance resource changed " + instance.getProviderId());

		return true;
	}

	public abstract boolean changeInstanceType(VirtualMachine instance);

	public static void main(String[] args) {
		Properties overrides = new Properties();

		   // set AMI queries to nothing
		   overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "");
		   overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY, "");

		   ComputeServiceContext context = ContextBuilder.newBuilder("aws-ec2")
		                      .credentials("opa", "opa")
		                      .overrides(overrides)
		                      .modules(ImmutableSet.<Module> of(new Log4JLoggingModule(),
		                                                        new SshjSshClientModule()))
		                      .buildView(ComputeServiceContext.class);

		   Template template = context.getComputeService().templateBuilder().imageId(
		            "us-east-1/ami-ccb35ea5").hardwareId("t1.micro").build();
		   
		   System.out.println(template.getImage().getProviderId());
			System.out.println(template.getImage().getDescription().indexOf("test"));
			System.out.println(template.getImage().getDescription().indexOf("daily"));
			System.out.println(template.getImage().getVersion());
			System.out.println(template.getImage().getOperatingSystem().getVersion());
			System.out.println(template.getImage().getOperatingSystem().is64Bit());
			System.out.println(template.getImage().getOperatingSystem().getFamily());
			System.out.println(template.getImage().getUserMetadata().get("rootDeviceType"));
			System.out.println(template.getLocation().getId());
			System.out.println(template.getHardware().getId());
			System.out.println(template.getImage().getOperatingSystem().getArch());
	}

}
