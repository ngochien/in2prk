package de.hawhamburg.se;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Component implements MessageListener {
	private static final boolean NON_TRANSACTED = false;

	private static final Logger LOG = LoggerFactory.getLogger(Component.class);

	private final String name;
	private final ConnectionFactory connectionFactory;
	private Connection connection;
	private final List<String> messagesReceived = new ArrayList<String>();

	public Component(final String name, final String messageBrokerURL)
			throws JMSException {
		this.name = name;
		this.connectionFactory = new ActiveMQConnectionFactory(messageBrokerURL);
		createConnection();
	}

	private void createConnection() throws JMSException {
		LOG.debug(name + ": Creating connection");
		this.connection = connectionFactory.createConnection();
		this.connection.setClientID(this.name);
	}

	public List<String> getMessagesReceived() {
		return Collections.unmodifiableList(messagesReceived);
	}

	public void sendMessageToTopic(final String msgTopicName, final String msg,
			final String propertyName, final String propertyValue)
			throws JMSException {
		LOG.debug(name + ": Sending message: " + msg + " to topic "
				+ msgTopicName + " with property " + propertyName + " as "
				+ propertyValue);

		final String txt = "{" + name + " says on " + msgTopicName + ": " + msg
				+ "}";

		// TODO start fill in here
		Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
		Message textMsg = session.createTextMessage(txt);
		textMsg.setStringProperty(propertyName, propertyValue);
		session.createProducer(session.createTopic(msgTopicName)).send(textMsg);
		closeSession(session);
		// end of fill in
	}

	public void activateMessageListenerOnTopic(final String msgTopicName,
			final String propertyName, final String propertyValue)
			throws JMSException {
		LOG.debug(name + ": Activating message listener on topic "
				+ msgTopicName + " with property " + propertyName + " as "
				+ propertyValue);

		// TODO start fill in here
		Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
		String msgSelector = propertyName + " LIKE '" + propertyValue + "'";
		session.createConsumer(session.createTopic(msgTopicName), msgSelector)
				.setMessageListener(this);
//		closeSession(session);	// don't close session!!?
		// end of fill in
	}

	private String extractTextFromMessage(final Message msg)
			throws JMSException {
		if (msg instanceof TextMessage) {
			final TextMessage textMsg = (TextMessage) msg;
			return textMsg.getText();
		} else {
			return "No TextMessage, but: " + msg.getClass().toString();
		}
	}

	private void closeSession(final Session session) {
		LOG.debug(name + ": Closing session");
		try {
			session.close();
		} catch (final JMSException e) {
			LOG.warn("while closing session", e);
		}
	}

	public void start() throws Exception {
		LOG.debug(name + ": Starting conection");
		connection.start();
	}

	public void close() {
		LOG.debug(name + ": Closing connection");
		if (connection != null) {
			try {
				connection.close();
			} catch (final JMSException e) {
				LOG.warn("while closing connection", e);
			} finally {
				connection = null;
			}
		}
	}

	@Override
	public void onMessage(final Message msg) {
		try {
			final String properties = getProperties(msg);
			final String text = extractTextFromMessage(msg);
			LOG.debug(name + ": Received message: " + text + " on "
					+ msg.getJMSDestination() + " with " + properties);
			messagesReceived.add("{" + name + " hears on "
					+ msg.getJMSDestination() + ": " + text + "[" + properties
					+ "]}");
		} catch (final JMSException e) {
			LOG.error("while receiving message", e);
		}
	}

	private String getProperties(final Message msg) throws JMSException {
		final StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		final Enumeration<String> propertyNames = msg.getPropertyNames();
		boolean isFirst = true;
		while (propertyNames.hasMoreElements()) {
			final String propertyName = propertyNames.nextElement();
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(",");
			}
			sb.append(propertyName);
			sb.append("=");
			sb.append(msg.getStringProperty(propertyName));
		}
		final String properties = sb.toString();
		return properties;
	}
}
