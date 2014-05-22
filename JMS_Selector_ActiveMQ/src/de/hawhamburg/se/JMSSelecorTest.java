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
 * Ver�ndere die Klasse Component so, dass dieser Test gr�n wird.
 */
public class JMSSelecorTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(JMSSelecorTest.class);

	private static final String MSG_BROKER_URL = "tcp://localhost:61616";

	private static final String NEWS_TOPIC = "NewsTopic";

	private static final String MAX = "Max";
	private static final String LEO = "Leo";

	private static final String MONA = "Mona";
	private static final String LISA = "Lisa";

	private static final String MSG_1 = "Can you hear me?";
	private static final String MSG_2 = "Listen to me!";

	private static final String PROPERTY_NAME_INFO_TYPE = "InfoType";

	private static final String PROPERTY_VALUE_INFO_TYPE_SPORTS = "Sports";
	private static final String PROPERTY_VALUE_INFO_TYPE_FASHION = "Fashion";

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
	public void testSendReceiveOnTopic() throws Exception {
		final Component max = new Component(MAX, MSG_BROKER_URL);
		final Component leo = new Component(LEO, MSG_BROKER_URL);
		final Component mona = new Component(MONA, MSG_BROKER_URL);
		final Component lisa = new Component(LISA, MSG_BROKER_URL);

		mona.activateMessageListenerOnTopic(NEWS_TOPIC,
				PROPERTY_NAME_INFO_TYPE, PROPERTY_VALUE_INFO_TYPE_SPORTS);
		startComponent(mona);

		lisa.activateMessageListenerOnTopic(NEWS_TOPIC,
				PROPERTY_NAME_INFO_TYPE, PROPERTY_VALUE_INFO_TYPE_FASHION);
		startComponent(lisa);

		leo.activateMessageListenerOnTopic(NEWS_TOPIC, PROPERTY_NAME_INFO_TYPE,
				"%");
		startComponent(leo);

		startComponent(max);
		max.sendMessageToTopic(NEWS_TOPIC, MSG_1, PROPERTY_NAME_INFO_TYPE,
				PROPERTY_VALUE_INFO_TYPE_SPORTS);
		max.sendMessageToTopic(NEWS_TOPIC, MSG_2, PROPERTY_NAME_INFO_TYPE,
				PROPERTY_VALUE_INFO_TYPE_FASHION);

		Thread.sleep(1000);

		Assert.assertEquals(1, mona.getMessagesReceived().size());
		Assert.assertEquals(1, lisa.getMessagesReceived().size());
		Assert.assertTrue(mona.getMessagesReceived().get(0)
				.contains(PROPERTY_VALUE_INFO_TYPE_SPORTS));
		Assert.assertTrue(lisa.getMessagesReceived().get(0)
				.contains(PROPERTY_VALUE_INFO_TYPE_FASHION));
		Assert.assertEquals(2, leo.getMessagesReceived().size());
	}

}
