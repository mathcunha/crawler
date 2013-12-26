package br.mia.unifor.crawlerenvironment.engine.view.rest.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientRequestor;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.management.ManagementHelper;
import org.hornetq.api.core.management.ResourceNames;

import br.mia.unifor.crawlerenvironment.engine.view.JSONHelper;
import br.mia.unifor.crawlerenvironment.mom.EmbeddedHornetQWrapper;
import br.mia.unifor.crawlerenvironment.view.model.Queue;

@Path("/queue")
public class QueueResource extends CloudCrawlerEnvironmentResource {

	// The Java method will process HTTP GET requests
	@GET
	@Path("{queueName}")
	@Produces("text/plain")
	public String listMessagesByQueue(@PathParam("queueName") String queueName) {

		/*
		 * ClientMessage message = session.createMessage(false);
		 * ManagementHelper.putAttribute(message, ResourceNames.CORE_QUEUE +
		 * queueName, "messageCount");
		 */

		ClientSession session = null;
		ClientRequestor requestor = null;
		try {
			session = EmbeddedHornetQWrapper.getClientSession();
			session.start();
			requestor = new ClientRequestor(session,
					"jms.queue.hornetq.management");

			ClientMessage message = session.createMessage(true);

			// ManagementHelper.putAttribute(message, ResourceNames.CORE_QUEUE +
			// queueName, "messageCount");
			ManagementHelper.putOperationInvocation(message, ResourceNames.CORE_QUEUE+ queueName, "listMessages", "");

			ClientMessage reply = requestor.request(message, 2000);
			if (reply != null) {
				if (ManagementHelper.hasOperationSucceeded(reply)) {

					for (Object arrayQueue : ManagementHelper.getResults(reply)) {
						System.out.println("one "+arrayQueue);
						for (Object queue : (Object[]) arrayQueue) {
							System.out.println("two" + queue);

						}
					}

					return "almost there";

				}
			} else {
				logger.log(Level.SEVERE, "no reply to queueNames at "
						+ ResourceNames.CORE_SERVER);
			}
			reply.acknowledge();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client session available", e);
		} finally {

			try {
				requestor.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "no client session available", e);
			}

			try {
				session.close();
			} catch (HornetQException e) {
				logger.log(Level.SEVERE, "no client session available", e);
			}
		}

		return null;

	}

	@GET
	@Produces("text/plain")
	public String getQueueNames() {
		ClientSession session = null;
		ClientRequestor requestor = null;
		try {
			session = EmbeddedHornetQWrapper.getClientSession();
			session.start();
			requestor = new ClientRequestor(session,
					"jms.queue.hornetq.management");

			ClientMessage message = session.createMessage(true);

			ManagementHelper.putAttribute(message, ResourceNames.CORE_SERVER,
					"queueNames");

			ClientMessage reply = requestor.request(message, 2000);
			if (reply != null) {
				if (ManagementHelper.hasOperationSucceeded(reply)) {
					List<Queue> queueList = new ArrayList<Queue>();
					for (Object arrayQueue : ManagementHelper.getResults(reply)) {
						for (Object queue : (Object[]) arrayQueue) {
							Queue lQueue = new Queue();
							lQueue.setName(queue.toString());
							queueList.add(lQueue);
						}
					}

					return JSONHelper.getJSON(queueList);

				}
			} else {
				logger.log(Level.SEVERE, "no reply to queueNames at "
						+ ResourceNames.CORE_SERVER);
			}
			reply.acknowledge();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "no client session available", e);
		} finally {

			try {
				requestor.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "no client session available", e);
			}

			try {
				session.close();
			} catch (HornetQException e) {
				logger.log(Level.SEVERE, "no client session available", e);
			}
		}

		return null;
	}

}
