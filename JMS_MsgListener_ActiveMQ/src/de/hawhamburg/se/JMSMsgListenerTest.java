package de.hawhamburg.se;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verändere die Klasse Component so, dass dieser Test grün wird.
 */
public class JMSMsgListenerTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(JMSMsgListenerTest.class);

	private static final String MSG_BROKER_URL = "tcp://localhost:61616";

	private static final String A_QUEUE = "aQueue";
	private static final String ANOTHER_QUEUE = "anotherQueue";

	private static final String MAX = "Max";

	private static final String MONA = "Mona";
	private static final String LISA = "Lisa";

	private static final String MSG_1 = "Can you hear me?";
	private static final String MSG_2 = "Listen to me!";

	private static BrokerService broker;

	private List<Component> components = new ArrayList<Component>();

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

	private void startComponent(final Component component) throws Exception {
		component.start();
		this.components.add(component);
	}

	private void closeComponent(final Component component) {
		component.close();
		this.components.remove(component);
	}

	@After
	public void tearDown() {
		LOG.debug("Tearing down");
		// copy
		final List<Component> componentsToStop = new ArrayList<Component>(
				components);
		for (final Component component : componentsToStop) {
			closeComponent(component);
		}
	}

	@Test
	public void testSendReceive() throws Exception {
		final Component max = new Component(MAX, MSG_BROKER_URL);
		final Component mona = new Component(MONA, MSG_BROKER_URL);
		final Component lisa = new Component(LISA, MSG_BROKER_URL);

		max.activateMessageListenerOnQueue(A_QUEUE);
		max.activateMessageListenerOnQueue(ANOTHER_QUEUE);
		startComponent(max);

		startComponent(mona);
		startComponent(lisa);

		mona.sendMessage(A_QUEUE, MSG_1);
		lisa.sendMessage(ANOTHER_QUEUE, MSG_2);

		Thread.sleep(1000);

		Assert.assertEquals(2, max.getMessagesReceived().size());
		LOG.info(max.getMessagesReceived().toString());
	}

	@Test
	public void testSendReceiveAynchronous() throws Exception {
		final Component mona = new Component(MONA, MSG_BROKER_URL);
		final Component lisa = new Component(LISA, MSG_BROKER_URL);

		startComponent(mona);
		startComponent(lisa);

		mona.sendMessage(A_QUEUE, MSG_1);
		lisa.sendMessage(ANOTHER_QUEUE, MSG_2);

		closeComponent(mona);
		closeComponent(lisa);

		final Component max = new Component(MAX, MSG_BROKER_URL);
		max.activateMessageListenerOnQueue(A_QUEUE);
		max.activateMessageListenerOnQueue(ANOTHER_QUEUE);
		startComponent(max);

		Thread.sleep(1000);

		Assert.assertEquals(2, max.getMessagesReceived().size());
		LOG.info(max.getMessagesReceived().toString());
	}

	@Test
	public void testSendReceiveConcurrentReceivers() throws Exception {
		final Component max = new Component(MAX, MSG_BROKER_URL);
		final Component mona = new Component(MONA, MSG_BROKER_URL);
		final Component lisa = new Component(LISA, MSG_BROKER_URL);

		mona.activateMessageListenerOnQueue(A_QUEUE);
		lisa.activateMessageListenerOnQueue(A_QUEUE);
		startComponent(mona);
		startComponent(lisa);

		startComponent(max);
		max.sendMessage(A_QUEUE, MSG_1);

		Thread.sleep(1000);

		Assert.assertEquals(1, mona.getMessagesReceived().size()
				+ lisa.getMessagesReceived().size());

		max.sendMessage(A_QUEUE, MSG_2);

		Thread.sleep(1000);

		Assert.assertEquals(2, mona.getMessagesReceived().size()
				+ lisa.getMessagesReceived().size());
	}

}
