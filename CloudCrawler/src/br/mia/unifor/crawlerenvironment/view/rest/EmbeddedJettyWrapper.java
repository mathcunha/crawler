package br.mia.unifor.crawlerenvironment.view.rest;


import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import javax.ws.rs.core.UriBuilder;

public class EmbeddedJettyWrapper {
	
	public final Properties properties;
	public final URI BASE_URI;
	public static final Logger logger = Logger.getLogger(EmbeddedJerseyWrapper.class.getName());
	private Server server; 
	
	public EmbeddedJettyWrapper(Properties properties){
		this.properties = properties;
		BASE_URI = getBaseURI();
	}

	private URI getBaseURI() {		
		return UriBuilder
				.fromUri("http://" + properties.getProperty("host") + "/")
				.port(new Integer(properties.getProperty("port"))).build();
	}
	
	public void startServer() throws Exception {
		logger.info("Starting jetty...");
		
		server = new Server();
		
		setConnectors();
		//setResourceHandler();
		Handler jerseyHandler = getJerseyHandler();
		Handler webAppHandler = getWebApp();
		
		HandlerList handlers = new HandlerList();
        	handlers.setHandlers(new Handler[] { jerseyHandler, webAppHandler });
        
        	server.setHandler(handlers);

		logger.info("http://" + properties.getProperty("host") + ":" + properties.getProperty("port") + "/CloudCrawler");
		
        	server.start();
		//server.join();
	}
	
	private Handler getJerseyHandler(){
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/api");
        
        
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "br.mia.unifor.crawlerenvironment.engine.view.rest.resources");
        
        context.addServlet(sh, "/*");
        
        return context;
        
	}
	
	private void setResourceHandler() {
		ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
 
        resource_handler.setResourceBase( properties.getProperty("fs.base.dir"));
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
        server.setHandler(handlers);
	}
	
	private Handler getWebApp(){
		WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/CloudCrawler");
        webapp.setWar("/vagrant/crawler/CloudCrawlerView/target/CloudCrawlerView-0.0.1.war");        
        
        return webapp;
	}

	private void setConnectors() {
		SelectChannelConnector connector0 = new SelectChannelConnector();
        connector0.setPort(BASE_URI.getPort());
        connector0.setHost(BASE_URI.getHost());
        connector0.setThreadPool(new QueuedThreadPool(20));
        connector0.setMaxIdleTime(30000);
        connector0.setRequestHeaderSize(8192);
        server.setConnectors(new Connector[]{connector0});
        
	}
	
	public void stopServer() throws Exception {
		server.stop();
	}
}
