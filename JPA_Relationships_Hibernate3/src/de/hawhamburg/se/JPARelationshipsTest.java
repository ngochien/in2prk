package de.hawhamburg.se;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vor dem Test muss die Datenbank mit dem Skript sql/createdb.sql erzeugt
 * worden sein.
 */
public class JPARelationshipsTest {

	// Die folgenden drei Konstanten müssen jeweils angepasst werden:
	private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/db_name";
	private static final String DB_USER = "...";
	private static final String DB_PASSWORD = "...";
	// Ende

	private static final Logger LOG = LoggerFactory
			.getLogger(JPARelationshipsTest.class);

	private static final String SURNAME_1 = "Felix";
	private static final String NAME_1 = "Furchtlos";
	private static final String SURNAME_2 = "Rudi";
	private static final String NAME_2 = "Ratlos";

	private static final String STREET_1 = "Murmelgasse";

	private static final String CREDITCARDNO_1 = "1234567890123456";
	private static final String CREDITCARDNO_2 = "0123456789012345";

	private static final String BANK_1 = "Money & More";
	private static final String BANK_2 = "Less Money";

	private static TransactionManager transactionManager;

	private EntityManagerFactory emf = null;
	private EntityManager em = null;

	@org.junit.BeforeClass
	public static void setUpClass() throws SQLException {
		transactionManager = new TransactionManager(DB_URL);
		transactionManager.connect(DB_USER, DB_PASSWORD);
	}

	@org.junit.AfterClass
	public static void tearDownClass() {
		transactionManager.disconnect();
		transactionManager = null;
	}

	@org.junit.Before
	public void setUp() throws SQLException {
		transactionManager.executeSQLDeleteOrUpdate(
				"delete from BANK_CUSTOMER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CREDITCARD",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CUSTOMER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from BANK",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from ADDRESS",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.commit();
	}

	@org.junit.After
	public void tearDown() throws SQLException {
		if (em != null) {
			try {
				em.close();
			} catch (final Throwable ex) {
				LOG.warn("while closing entity manager", ex);
			} finally {
				em = null;
			}
		}
		if (emf != null) {
			try {
				emf.close();
			} catch (final Throwable ex) {
				LOG.warn("while closing entity manager factory", ex);
			} finally {
				emf = null;
			}
		}
	}

	private void createEntityManager() {
		emf = Persistence.createEntityManagerFactory("haw_demo");
		em = emf.createEntityManager();
	}

	@org.junit.Test
	public void testEntityManager() throws SQLException {
		createEntityManager();
		em.getTransaction().begin();
		em.getTransaction().commit();
	}

	@org.junit.Test
	public void testCreateCustomerWithAddress() throws SQLException {
		createEntityManager();
		em.getTransaction().begin();
		final Address address = new Address(STREET_1);
		final Customer customer = new Customer(SURNAME_1, NAME_1);
		customer.setHomeAddress(address);
		em.persist(customer);
		em.getTransaction().commit();

		Assert.assertTrue(hasCustomerHomeAddressOnDB(customer.getId(),
				address.getId()));
	}

	@org.junit.Test
	public void testCreateCustomerWithCreditCards() throws SQLException {
		createEntityManager();
		em.getTransaction().begin();
		final Customer customer = new Customer(SURNAME_1, NAME_1);
		final CreditCard creditCard1 = new CreditCard(CREDITCARDNO_1);
		final CreditCard creditCard2 = new CreditCard(CREDITCARDNO_2);
		final Set<CreditCard> creditCards = new HashSet<CreditCard>();
		creditCards.add(creditCard1);
		creditCards.add(creditCard2);
		customer.setCreditCards(creditCards);
		creditCard1.setHolder(customer);
		creditCard2.setHolder(customer);
		em.persist(customer);
		em.getTransaction().commit();

		Assert.assertTrue(hasCustomerCreditCardOnDB(customer.getId(),
				creditCard1.getId()));
		Assert.assertTrue(hasCustomerCreditCardOnDB(customer.getId(),
				creditCard2.getId()));
	}

	@org.junit.Test
	public void testCreateCustomerAndBank() throws SQLException {
		createEntityManager();
		em.getTransaction().begin();
		final Customer customer1 = new Customer(SURNAME_1, NAME_1);
		final Customer customer2 = new Customer(SURNAME_2, NAME_2);
		final Bank bank1 = new Bank(BANK_1);
		final Bank bank2 = new Bank(BANK_2);
		customer1.getBanks().add(bank1);
		customer1.getBanks().add(bank2);
		customer2.getBanks().add(bank2);
		em.persist(customer1);
		em.persist(customer2);
		em.persist(bank1);
		em.persist(bank2);
		em.getTransaction().commit();

		Assert.assertTrue(hasCustomerBankOnDB(customer1.getId(), bank1.getId()));
		Assert.assertTrue(hasCustomerBankOnDB(customer1.getId(), bank2.getId()));
		Assert.assertTrue(hasCustomerBankOnDB(customer2.getId(), bank2.getId()));
	}

	private boolean hasCustomerHomeAddressOnDB(final long customerId,
			final long addressId) throws SQLException {
		final List<Object> parameters = new ArrayList<Object>();
		parameters.add(new Long(customerId));
		parameters.add(new Long(addressId));
		return BigDecimal.ONE
				.equals(transactionManager
						.executeSQLQuerySingleResult(
								"select count(*) from CUSTOMER where ID = ? and HOME_ADDRESS_ID = ?",
								parameters));
	}

	private boolean hasCustomerCreditCardOnDB(final long customerId,
			final long creditCardId) throws SQLException {
		final List<Object> parameters = new ArrayList<Object>();
		parameters.add(new Long(creditCardId));
		parameters.add(new Long(customerId));
		return BigDecimal.ONE
				.equals(transactionManager
						.executeSQLQuerySingleResult(
								"select count(*) from CREDITCARD where ID = ? and CUSTOMER_ID = ?",
								parameters));
	}

	private boolean hasCustomerBankOnDB(final long customerId, final long bankId)
			throws SQLException {
		final List<Object> parameters = new ArrayList<Object>();
		parameters.add(new Long(customerId));
		parameters.add(new Long(bankId));
		return BigDecimal.ONE
				.equals(transactionManager
						.executeSQLQuerySingleResult(
								"select count(*) from BANK_CUSTOMER where CUSTOMER_ID = ? and BANK_ID = ?",
								parameters));
	}

}
