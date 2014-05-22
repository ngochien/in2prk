package de.hawhamburg.se;

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

/**
 * A class that consumes text messages and sends responses to a replyTo
 * destination extracted from the message sent.
 */
public class Server implements MessageListener {
	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	private static final boolean NON_TRANSACTED = false;

	private final String name;
	private final ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;

	public Server(final String name, final String messageBrokerURL)
			throws JMSException {
		this.name = name;
		this.connectionFactory = new ActiveMQConnectionFactory(messageBrokerURL);
		createConnection();
	}

	private void createConnection() throws JMSException {
		this.connection = connectionFactory.createConnection();
		this.connection.setClientID(name);
	}

	public void startMessageListenerOnQueue(final String msgQueueName)
			throws JMSException {
		LOG.debug(name + ": Starting server listening on queue " + msgQueueName);

		// TODO fill in here
		session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
		session.createConsumer(session.createQueue(msgQueueName))
				.setMessageListener(this);
		// end fill in
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
			final Destination replyTo = msg.getJMSReplyTo();
			LOG.debug(name + ": Received message: " + text + " on "
					+ msg.getJMSDestination() + " with correlation id "
					+ correlationID);
			LOG.debug(name + ": Sending reply to " + replyTo
					+ " using correlation id " + correlationID);

			final String txt = extractTextFromMessage(msg);
			final String responseTxt = "Response to: " + txt;

			// TODO fill in here
			session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
			Message responseMsg = session.createTextMessage(responseTxt);
			responseMsg.setJMSCorrelationID(correlationID);
			session.createProducer(replyTo).send(responseMsg);
			// end fill in
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
