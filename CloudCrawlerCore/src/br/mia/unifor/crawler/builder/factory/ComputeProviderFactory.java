package br.mia.unifor.crawler.builder.factory;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.mia.unifor.crawler.builder.ComputeProvider;
import br.mia.unifor.crawler.builder.EC2ComputeProvider;
import br.mia.unifor.crawler.builder.RackspaceComputeProvider;
import br.mia.unifor.crawler.engine.CrawlException;
import br.mia.unifor.crawler.executer.artifact.Provider;

public class ComputeProviderFactory {
	public static final Map<String, ComputeProvider> map = new Hashtable<String, ComputeProvider>();
	static Logger log = Logger
			.getLogger(ComputeProviderFactory.class.getName());

	public static ComputeProvider getProvider(Provider provider) throws CrawlException {
		ComputeProvider lComputeProvider = map.get(provider.getName());
		try {
			provider.loadProperties();
			if (lComputeProvider == null) {
				log.info("Carregando o provedor " + provider.getName()
						+ "/ userName " + provider.getUserName());

				if ("aws-ec2".equals(provider.getName())) {

					lComputeProvider = new EC2ComputeProvider(provider);

					map.put(provider.getName(), lComputeProvider);
				} else if ("cloudservers-us".equals(provider.getName())) {
					lComputeProvider = new RackspaceComputeProvider(provider);

					map.put(provider.getName(), lComputeProvider);
				} else if ("local".equals(provider)) {
					// do nothing

				}

				log.info("o provedor " + provider.getName()
						+ " foi carregado com sucesso");

			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "error loading credentials provider -> "+provider.getName(), e);
			throw new CrawlException(e.getMessage(), e);
		}

		return lComputeProvider;

	}

}
