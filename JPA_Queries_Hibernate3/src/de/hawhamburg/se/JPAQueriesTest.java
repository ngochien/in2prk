package de.hawhamburg.se;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vor dem Test muss die Datenbank mit dem Skript sql/createdb.sql erzeugt
 * worden sein.
 * 
 * Die Queries sind an den richtigen Stellen zu ergänzen.
 */
public class JPAQueriesTest {

	private static final String STREET_1 = "Murmelgasse";
	// Die folgenden drei Konstanten müssen jeweils angepasst werden:
	private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/db_name";
	private static final String DB_USER = "...";
	private static final String DB_PASSWORD = "...";
	// Ende

	private static final Logger LOG = LoggerFactory
			.getLogger(JPAQueriesTest.class);

	private static final String SURNAME_1 = "Felix";
	private static final String NAME_1 = "Furchtlos";
	private static final String SURNAME_2 = "Rudi";
	private static final String NAME_2 = "Ratlos";

	private static final String POSTCODE1 = "20000";
	private static final String POSTCODE2 = "28000";

	private static final String CREDITCARDNO_1 = "1234567890123456";
	private static final String CREDITCARDNO_2 = "0123456789012345";

	private static final String BANK_1 = "Money & More";
	private static final String BANK_2 = "Less Money";

	private static final String CARD_ISSUER_1 = "Mister";
	private static final String CARD_ISSUER_2 = "Vasi";

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
		transactionManager.executeSQLDeleteOrUpdate("delete from BANK_OFFICE",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CREDITCARD",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CUSTOMER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from BANK",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.executeSQLDeleteOrUpdate("delete from CARDISSUER",
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

	private void createTestData() {
		em.getTransaction().begin();
		final Address customer_1_home = new Address(POSTCODE1, "HH",
				"Ost-West-Straße");
		final Address customer_2_home = new Address(POSTCODE2, "HB",
				"Nord-Süd-Straße");

		final Address bank_1_office_1 = new Address(POSTCODE1, "HH", STREET_1);
		final Address bank_1_office_2 = new Address(POSTCODE2, "HB",
				"Heinzelstaße");

		final Bank bank1 = new Bank(BANK_1);
		bank1.getOffices().add(bank_1_office_1);
		bank1.getOffices().add(bank_1_office_2);

		final Bank bank2 = new Bank(BANK_2);

		final CardIssuer cardIssuer_1 = new CardIssuer(CARD_ISSUER_1);
		final CardIssuer cardIssuer_2 = new CardIssuer(CARD_ISSUER_2);

		final Customer customer1 = new Customer(SURNAME_1, NAME_1);
		customer1.setHomeAddress(customer_1_home);

		final Customer customer2 = new Customer(SURNAME_2, NAME_2);
		customer2.setHomeAddress(customer_2_home);

		final CreditCard creditCard1 = new CreditCard(CREDITCARDNO_1,
				CreditCardType.CREDIT);
		creditCard1.setIssuer(cardIssuer_1);

		final CreditCard creditCard2 = new CreditCard(CREDITCARDNO_2,
				CreditCardType.DEBIT);
		creditCard2.setIssuer(cardIssuer_2);

		customer1.getCreditCards().add(creditCard1);
		creditCard1.setHolder(customer1);

		customer1.getCreditCards().add(creditCard2);
		creditCard2.setHolder(customer1);

		customer1.getBanks().add(bank1);
		customer1.getBanks().add(bank2);

		customer2.getBanks().add(bank2);

		em.persist(cardIssuer_1);
		em.persist(cardIssuer_2);
		em.persist(bank1);
		em.persist(bank2);
		em.persist(customer1);
		em.persist(customer2);
		em.getTransaction().commit();

	}

	@org.junit.Test
	public void testEntityManager() throws SQLException {
		createEntityManager();
		em.getTransaction().begin();
		em.getTransaction().commit();
	}

	@org.junit.Test
	public void testCreateTestData() throws SQLException {
		createEntityManager();
		createTestData();
	}

	@Test
	public void testJPQLNavigationParameter() {
		createEntityManager();
		createTestData();

		em.getTransaction().begin();

		// Select all customers with a home address that has a certain postcode.
		// Parameters: postcode.
		final String jpqlCustomersPostcode = "from Customer c where c.homeAddress.postcode = :postcode";

		final TypedQuery<Customer> queryCustomersPostcode = em.createQuery(
				jpqlCustomersPostcode, Customer.class);

		queryCustomersPostcode.setParameter("postcode", POSTCODE1);
		final List<Customer> result1 = queryCustomersPostcode.getResultList();
		Assert.assertEquals(1, result1.size());
		Assert.assertEquals(NAME_1, result1.get(0).getName());

		queryCustomersPostcode.setParameter("postcode", POSTCODE2);
		final List<Customer> result2 = queryCustomersPostcode.getResultList();
		Assert.assertEquals(1, result2.size());
		Assert.assertEquals(NAME_2, result2.get(0).getName());
	}

	@Test
	public void testJPQLJoinEnum() {
		createEntityManager();
		createTestData();

		em.getTransaction().begin();

		// Select all customers holding a credit card of certain type.
		// Parameters: creditCardType.
		final String jpqlCustomersCreditCardType = "select c from Customer c JOIN c.creditCards cc where cc.type = :creditCardType";
		final TypedQuery<Customer> queryCustomersCreditCardType = em
				.createQuery(jpqlCustomersCreditCardType, Customer.class);

		queryCustomersCreditCardType.setParameter("creditCardType",
				CreditCardType.CREDIT);
		final List<Customer> result1 = queryCustomersCreditCardType
				.getResultList();
		Assert.assertEquals(1, result1.size());
		Assert.assertEquals(NAME_1, result1.get(0).getName());

		queryCustomersCreditCardType.setParameter("creditCardType",
				CreditCardType.DEBIT);
		final List<Customer> result2 = queryCustomersCreditCardType
				.getResultList();
		Assert.assertEquals(1, result2.size());
		Assert.assertEquals(NAME_1, result2.get(0).getName());
	}

	@Test
	public void testJPQLNamedQueryCollection() {
		createEntityManager();
		createTestData();

		em.getTransaction().begin();

		final TypedQuery<Customer> queryCustomersWithoutCreditCard = em
				.createNamedQuery("selectCustomersWithoutCreditCards",
						Customer.class);

		final List<Customer> result1 = queryCustomersWithoutCreditCard
				.getResultList();
		Assert.assertEquals(1, result1.size());
		Assert.assertEquals(NAME_2, result1.get(0).getName());
	}

	@Test
	public void testJPQLNewObject() {
		createEntityManager();
		createTestData();

		em.getTransaction().begin();

		// Select for each customer his name and the street names of bank
		// offices of his bank in his home town
		// Returns CustomerWithBankOfficeAddress
		
		// Jetzt läuft es. Aber ich verstehe nicht warum @_@
		final String jpqlCustomersOffices =
				"SELECT NEW de.hawhamburg.se.CustomerWithBankOfficeAddress(c.name, o.street)"
				+ "from Customer c join c.banks b join b.offices o where c.homeAddress.postcode = o.postcode";

		final TypedQuery<CustomerWithBankOfficeAddress> query = em.createQuery(
				jpqlCustomersOffices, CustomerWithBankOfficeAddress.class);

		final List<CustomerWithBankOfficeAddress> result1 = query
				.getResultList();
		Assert.assertEquals(1, result1.size());
		Assert.assertEquals(NAME_1, result1.get(0).getCustomerName());
		Assert.assertEquals(STREET_1, result1.get(0).getOfficeStreet());
	}

}
