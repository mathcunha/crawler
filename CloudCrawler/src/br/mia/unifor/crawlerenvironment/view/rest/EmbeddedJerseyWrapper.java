package br.mia.unifor.crawlerenvironment.view.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

//import org.glassfish.grizzly.http.server.HttpServer;

//import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import javax.ws.rs.core.UriBuilder;

public class EmbeddedJerseyWrapper {
	
	public final Properties properties;
	public final URI BASE_URI;
	public static final Logger logger = Logger.getLogger(EmbeddedJerseyWrapper.class.getName());
	//private HttpServer httpServer; 
	
	public EmbeddedJerseyWrapper(Properties properties){
		this.properties = properties;
		BASE_URI = getBaseURI();
	}

	private URI getBaseURI() {		
		return UriBuilder
				.fromUri("http://" + properties.getProperty("host") + "/")
				.port(new Integer(properties.getProperty("port"))).build();
	}
	
	public void startServer() throws IOException {
		logger.info("Starting grizzly...");
		ResourceConfig rc = new PackagesResourceConfig(
				"br.mia.unifor.crawlerenvironment.engine.view.rest.resources");
		//httpServer  = GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
		//httpServer.start();
	}
	
	public void stopServer() {
		//httpServer.stop();
	}
}
