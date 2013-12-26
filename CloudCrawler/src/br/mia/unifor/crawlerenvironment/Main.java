package br.mia.unifor.crawlerenvironment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import br.mia.unifor.crawlerenvironment.mom.EmbeddedHornetQWrapper;
import br.mia.unifor.crawlerenvironment.view.rest.EmbeddedJettyWrapper;

public class Main {

	public static final String PROP_FILE_NAME = "crawlerweb.properties";
	public static Properties properties;

	public static final Logger logger = Logger.getLogger(Main.class.getName());

	private static void load() {
		try {
			properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(PROP_FILE_NAME));
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "crawlerweb.properties not found", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE,
					"could not read the file crawlerweb.properties", e);
		}
	}

	public static void main(String[] args) throws Exception {

		load();

		EmbeddedHornetQWrapper.getEmbeddedHornetQ();

		EmbeddedJettyWrapper engine = new EmbeddedJettyWrapper(properties);
		engine.startServer();
		
		
		TailerListener listener = new ConsoleTailerListener(properties);
		Tailer tailer = new Tailer(new File("/tmp/cloud-crawler-output.log"),
				listener, 5000);
		Thread thread = new Thread(tailer,"tailer");
		thread.setDaemon(true); // optional
		thread.start();
		
		System.out
				.println(String
						.format("Jersey app started with WADL available at "
								+ "%sapplication.wadl\nTry out %shelloworld\nHit enter to stop it...",
								engine.BASE_URI, engine.BASE_URI));
		System.in.read();
		engine.stopServer();
		tailer.stop();
		EmbeddedHornetQWrapper.getEmbeddedHornetQ().stop();

	}
}
