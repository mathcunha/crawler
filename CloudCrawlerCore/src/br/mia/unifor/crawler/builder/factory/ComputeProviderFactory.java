package br.mia.unifor.crawler.builder.factory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import br.mia.unifor.crawler.builder.ComputeProvider;
import br.mia.unifor.crawler.builder.EC2ComputeProvider;
import br.mia.unifor.crawler.builder.RackspaceComputeProvider;
import br.mia.unifor.crawler.executer.artifact.Provider;

public class ComputeProviderFactory {
	public static final Map<String, ComputeProvider> map = new Hashtable<String, ComputeProvider>();
	static Logger log = Logger.getLogger(ComputeProviderFactory.class.getName());

	public static ComputeProvider getProvider(Provider provider)
			throws IOException {
		ComputeProvider lComputeProvider = map.get(provider.getName());

		provider.loadProperties();
		if (lComputeProvider == null) {
			log.info("Carregando o provedor "+provider.getName()+ "/ userName "+provider.getUserName());

			if ("aws-ec2".equals(provider.getName())) {

				lComputeProvider = new EC2ComputeProvider(provider);
				
				map.put(provider.getName(), lComputeProvider);
			} else if ("cloudservers-us".equals(provider.getName())) {
				lComputeProvider = new RackspaceComputeProvider(provider);

				map.put(provider.getName(), lComputeProvider);
			} else if ("local".equals(provider)) {
				// do nothing

			}
			
			log.info("o provedor "+provider.getName()+" foi carregado com sucesso");

			
		}

		return lComputeProvider;
	}

	
}
