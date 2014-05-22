package de.hawhamburg.se;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
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

public class Client implements MessageListener {
	private static final Logger LOG = LoggerFactory.getLogger(Client.class);

	private static final boolean NON_TRANSACTED = false;

	private final String name;
	private final ConnectionFactory connectionFactory;
	private final Map<String, String> messagesReceived = Collections
			.synchronizedMap(new HashMap<String, String>());

	private Connection connection;

	public Client(final String name, final String messageBrokerURL)
			throws JMSException {
		this.name = name;
		this.connectionFactory = new ActiveMQConnectionFactory(messageBrokerURL);
		createConnection();
	}

	private void createConnection() throws JMSException {
		LOG.debug(name + ": Creating connection");
		this.connection = connectionFactory.createConnection();
		this.connection.setClientID(name);
	}

	public String getReplyMessage(final String correlationId) {
		return messagesReceived.get(correlationId);
	}

	private void activateMessageListenerOnQueue(final Destination destination)
			throws JMSException {
		LOG.debug(name + ": Activating message listener on queue "
				+ destination);

		// TODO start fill in here
		Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
		session.createConsumer(destination).setMessageListener(this);
//		closeSession(session);	// never close session here!!!
		// end of fill in
	}

	public String sendMessage(final String msgQueueName, final String msg,
			final String replyMsgQueueName) throws JMSException {
		final String correlationId = UUID.randomUUID().toString();
		LOG.debug(name + ": Sending message: " + msg + " to queue "
				+ msgQueueName + " with correlation id " + correlationId
				+ ", awaiting reply on queue " + replyMsgQueueName);

		final String txtToSend = "{" + name + " says on " + msgQueueName + ": "
				+ msg + "}";

		// TODO start fill in here
		Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
		Destination replyTo = session.createQueue(replyMsgQueueName);
		
		Message txtMsg = session.createTextMessage(txtToSend);
		txtMsg.setJMSCorrelationID(correlationId);
		txtMsg.setJMSReplyTo(replyTo);
		txtMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);	// should do this
		txtMsg.setJMSExpiration(1000);	// see lecture notes. no idea why I'm doing this
		
		session.createProducer(session.createQueue(msgQueueName)).send(txtMsg);
		activateMessageListenerOnQueue(replyTo);
		closeSession(session);
		// end of fill in

		return correlationId;
	}

	public String sendMessageUsingTemporyReplyQueue(final String msgQueueName,
			final String msg) throws JMSException {
		final String correlationId = UUID.randomUUID().toString();
		LOG.debug(name + ": Sending message: " + msg + " to queue "
				+ msgQueueName + " with correlation id " + correlationId
				+ ", awaiting reply on temporary queue ");

		final String txtToSend = "{" + name + " says on " + msgQueueName + ": "
				+ msg + "}";

		// TODO start fill in here
		Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
		Destination replyTo = session.createTemporaryQueue();
		
		Message txtMsg = session.createTextMessage(txtToSend);
		txtMsg.setJMSCorrelationID(correlationId);
		txtMsg.setJMSReplyTo(replyTo);
		txtMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		txtMsg.setJMSExpiration(1000);
		
		session.createProducer(session.createQueue(msgQueueName)).send(txtMsg);
		activateMessageListenerOnQueue(replyTo);
		closeSession(session);
		// end of fill in

		return correlationId;
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
			final String text = extractTextFromMessage(msg);
			final String correlationID = msg.getJMSCorrelationID();
			LOG.debug(name + ": Received message: " + text + " on "
					+ msg.getJMSDestination() + " with correlation id "
					+ correlationID);
			messagesReceived.put(correlationID,
					"{" + name + " hears on " + msg.getJMSDestination() + ": "
							+ text + "}");
		} catch (final JMSException e) {
			LOG.error("while receiving message", e);
		}
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

}
