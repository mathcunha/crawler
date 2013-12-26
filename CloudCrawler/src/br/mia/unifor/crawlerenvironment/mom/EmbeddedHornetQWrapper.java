package br.mia.unifor.crawlerenvironment.mom;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.core.server.embedded.EmbeddedHornetQ;

public class EmbeddedHornetQWrapper {

	private static EmbeddedHornetQ embedded;
	public static ServerLocator locator;
	public static ClientSessionFactory factory; 

	protected static Logger logger = Logger
			.getLogger(EmbeddedHornetQWrapper.class.getName());

	public static EmbeddedHornetQ getEmbeddedHornetQ() {
		if (embedded == null) {
			embedded = new EmbeddedHornetQ();
			start();
		}
		return embedded;
	}

	
	private static void config() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TransportConstants.PORT_PROP_NAME, 5445);
		params.put(TransportConstants.HOST_PROP_NAME, "127.0.0.1");

		TransportConfiguration config = new TransportConfiguration(
				NettyConnectorFactory.class.getName(), params);
		
		locator = HornetQClient.createServerLocatorWithoutHA(config);
		
		factory = locator.createSessionFactory();
	}
	
	public static void tearDown() throws Exception{
		factory.close();
		
		locator.close();
	}
	
	
	private static void start() {
		try {
			embedded.start();
			config();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "HornetQ not started", e);
			e.printStackTrace();
		}
	}
	
	public static void close(){
		
		factory.getConnection().disconnect(false);
		
		factory.close();
		
		try {
			locator.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ServerLocator not closed", e);
		}
		try {
			embedded.getHornetQServer().stop();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "HornetQ not stopped", e);
		}
	}

	public static ClientSession getClientSession() throws Exception {

		if(factory == null){
			config();
		}
		
		return factory.createSession("crawler", "crawler", false, true, true,
				false, 65536);
	}

	private static void execTest() throws Exception {

		ClientSession session = getClientSession();

		session.createQueue("example", "example", true);

		ClientProducer producer = session.createProducer("example");

		ClientMessage message = session.createMessage(true);

		message.getBodyBuffer().writeString("Hello");

		producer.send(message);

		session.start();

		ClientConsumer consumer = session.createConsumer("example");

		ClientMessage msgReceived = consumer.receive();

		System.out.println("message = "
				+ msgReceived.getBodyBuffer().readString());

		session.close();
	}

	public static void main(String[] args) {
		EmbeddedHornetQWrapper.getEmbeddedHornetQ();
		try {
			EmbeddedHornetQWrapper.execTest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}