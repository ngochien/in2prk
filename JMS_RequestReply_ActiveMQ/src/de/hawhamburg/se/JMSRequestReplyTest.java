package de.hawhamburg.se;

import javax.jms.JMSException;

import junit.framework.Assert;

import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verändere die Klassen Client und Server, so dass der Test grün wird.
 */
public class JMSRequestReplyTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(JMSRequestReplyTest.class);

	private static final String MSG_BROKER_URL = "tcp://localhost:61616";

	private static final String QUEUE_1 = "aQueue";
	private static final String QUEUE_2 = "anotherQueue";
	private static final String QUEUE_3 = "oneMoreQueue";

	private static final String MAX = "Max";
	private static final String LEO = "Leo";

	private static final String MSG_1 = "Can you hear me?";
	private static final String MSG_2 = "Listen to me!";

	private static BrokerService broker;

	private Server server;
	private Client client;

	@BeforeClass
	public static void setUpClass() throws Exception {
		LOG.debug("Starting up");
		broker = new BrokerService();
		broker.setBrokerName("TestBroker");
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.addConnector(MSG_BROKER_URL);
		broker.start();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		LOG.debug("Shutting down");
		try {
			broker.stop();
		} catch (final Throwable t) {
			LOG.warn("while shutting down", t);
		}
	}

	@Before
	public void setUp() throws JMSException {
		LOG.debug("Setting up");
		server = new Server(MAX, MSG_BROKER_URL);
		client = new Client(LEO, MSG_BROKER_URL);
	}

	@After
	public void tearDown() {
		LOG.debug("Tearing down");
		if (client != null) {
			client.close();
			client = null;
		}
		if (server != null) {
			server.close();
			server = null;
		}
	}

	@Test
	public void testRequestReplyFixedReplyQueue() throws Exception {
		server.startMessageListenerOnQueue(QUEUE_1);
		server.start();
		client.start();
		final String correlationId = client
				.sendMessage(QUEUE_1, MSG_1, QUEUE_2);

		Thread.sleep(1000);

		final String messageReceived = client.getReplyMessage(correlationId);
		Assert.assertNotNull(messageReceived);
		Assert.assertTrue(messageReceived.contains("Response to"));
		Assert.assertTrue(messageReceived.contains(MSG_1));

		final String correlationId2 = client.sendMessage(QUEUE_1, MSG_2,
				QUEUE_3);

		Thread.sleep(1000);

		final String messageReceived2 = client.getReplyMessage(correlationId2);
		Assert.assertNotNull(messageReceived2);
		Assert.assertTrue(messageReceived2.contains("Response to"));
		Assert.assertTrue(messageReceived2.contains(MSG_2));
	}

	@Test
	public void testRequestReplyTempReplyQueue() throws Exception {
		server.startMessageListenerOnQueue(QUEUE_1);
		server.start();
		client.start();
		final String correlationId = client.sendMessageUsingTemporyReplyQueue(
				QUEUE_1, MSG_1);

		Thread.sleep(1000);

		final String messageReceived = client.getReplyMessage(correlationId);
		Assert.assertNotNull(messageReceived);
		Assert.assertTrue(messageReceived.contains("Response to"));
		Assert.assertTrue(messageReceived.contains(MSG_1));

		final String correlationId2 = client.sendMessageUsingTemporyReplyQueue(
				QUEUE_1, MSG_2);

		Thread.sleep(1000);

		final String messageReceived2 = client.getReplyMessage(correlationId2);
		Assert.assertNotNull(messageReceived2);
		Assert.assertTrue(messageReceived2.contains("Response to"));
		Assert.assertTrue(messageReceived2.contains(MSG_2));
	}

}
