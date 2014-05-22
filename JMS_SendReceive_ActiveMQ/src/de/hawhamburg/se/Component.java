package de.hawhamburg.se;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Component {
	private static final boolean NON_TRANSACTED = false;

	private static final Logger LOG = LoggerFactory.getLogger(Component.class);

	private final String name;
	private final ConnectionFactory connectionFactory;
	private Connection connection;

	public Component(final String name, final String messageBrokerURL)
			throws JMSException {
		this.name = name;
		this.connectionFactory = new ActiveMQConnectionFactory(messageBrokerURL);
		createConnection();
	}

	private void createConnection() throws JMSException {
		this.connection = connectionFactory.createConnection();
		this.connection.setClientID(name);
	}

	public void sendMessage(final String msgQueueName, final String msg)
			throws JMSException {
		LOG.debug(name + ": Sending message: " + msg + " to queue "
				+ msgQueueName);

		final String txt = "{" + name + " says on " + msgQueueName + ": " + msg
				+ "}";

		// TODO fill in here
		Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
//		Destination queue = session.createQueue(msgQueueName);
//		MessageProducer producer = session.createProducer(queue);
//		producer.send(session.createTextMessage(txt));
		session.createProducer(session.createQueue(msgQueueName))
				.send(session.createTextMessage(txt));
		closeSession(session);
		// end of fill in
	}

	public String receiveMessage(final String msgQueueName) throws JMSException {
		LOG.debug(name + ": Receiving message on queue " + msgQueueName);

		Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
//		Destination queue = session.createQueue(msgQueueName);
//		MessageConsumer consumer = session.createConsumer(queue);
		// fill this variable with the message received
		final Message message = session.createConsumer(session.createQueue(msgQueueName))
											.receive();

		// TODO start fill in here
		closeSession(session);
		// end of fill in

		final String text = extractTextFromMessage(message);
		LOG.debug(name + ": Received message: " + text);
		return "{" + name + " hears on " + msgQueueName + ": " + text + "}";
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
}
