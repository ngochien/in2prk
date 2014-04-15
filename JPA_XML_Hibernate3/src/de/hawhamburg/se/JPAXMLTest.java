package de.hawhamburg.se;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
public class JPAXMLTest {

	// Die folgenden drei Konstanten m�ssen jeweils angepasst werden:
	private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/db_name";
	private static final String DB_USER = "...";
	private static final String DB_PASSWORD = "...";
	// Ende

	private static final Logger LOG = LoggerFactory.getLogger(JPAXMLTest.class);

	private static final String SURNAME_1 = "Felix";
	private static final String NAME_1 = "Furchtlos";
	private static final String SURNAME_2 = "Rudi";
	private static final String NAME_2 = "Ratlos";

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
		transactionManager.executeSQLDeleteOrUpdate("delete from CUSTOMER",
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
	public void testCreateCustomer() throws SQLException {
		createEntityManager();
		final Customer customer = new Customer(SURNAME_1, NAME_1);
		// TODO
		em.getTransaction().begin();
		em.persist(customer);
		em.getTransaction().commit();
		Assert.assertTrue(isCustomerOnDB(customer.getId(), SURNAME_1, NAME_1));
	}

	@org.junit.Test
	public void testFindCustomer() throws SQLException {
		final long id = insertCustomerInDB(SURNAME_1, NAME_1);
		//TODO
		createEntityManager();
		final Customer customer = em.find(Customer.class, new Long(id));
		Assert.assertNotNull(customer);
		Assert.assertEquals(id, customer.getId());
		Assert.assertEquals(SURNAME_1, customer.getSurname());
		Assert.assertEquals(NAME_1, customer.getName());
	}

	@org.junit.Test
	public void testQueryCustomer() throws SQLException {
		final long id = insertCustomerInDB(SURNAME_1, NAME_1);
		createEntityManager();
		em.getTransaction().begin();
		final List<Customer> customers = em.createQuery("from Customer",
				Customer.class).getResultList();
		Assert.assertEquals(1, customers.size());
		final Customer customer = customers.get(0);
		Assert.assertEquals(id, customer.getId());
		Assert.assertEquals(SURNAME_1, customer.getSurname());
		Assert.assertEquals(NAME_1, customer.getName());
	}
	
	private long insertCustomerInDB(final String surname, final String name)
			throws SQLException {
		final long id = getNextCustomerId();
		final List<Object> parameters = new ArrayList<Object>();
		parameters.add(new Long(id));
		parameters.add(surname);
		parameters.add(name);
		transactionManager.executeSQLInsert(
				"insert into CUSTOMER (ID, SURNAME, NAME) values (?, ?, ?)",
				parameters);
		transactionManager.commit();
		return id;
	}


	@org.junit.Test
	public void testUpdateCustomer() throws SQLException {
		final long id = insertCustomerInDB(SURNAME_1, NAME_1);
		Assert.assertFalse(isCustomerOnDB(id, SURNAME_2, NAME_2));
		createEntityManager();
		em.getTransaction().begin();
		final Customer customer = em.find(Customer.class, new Long(id));
		customer.setName(NAME_2);
		customer.setSurname(SURNAME_2);
		em.persist(customer);
		Assert.assertFalse(isCustomerOnDB(id, SURNAME_2, NAME_2));
		em.flush();
		Assert.assertFalse(isCustomerOnDB(id, SURNAME_2, NAME_2));
		em.getTransaction().commit();
		Assert.assertTrue(isCustomerOnDB(id, SURNAME_2, NAME_2));
	}

	@org.junit.Test
	public void testDeleteCustomer() throws SQLException {
		final long id = insertCustomerInDB(SURNAME_1, NAME_1);
		Assert.assertTrue(isCustomerOnDB(id, SURNAME_1, NAME_1));
		createEntityManager();
		em.getTransaction().begin();
		final Customer customer = em.find(Customer.class, new Long(id));
		em.remove(customer);
		em.getTransaction().commit();
		Assert.assertFalse(isCustomerOnDB(id, SURNAME_1, NAME_1));
	}

	private boolean isCustomerOnDB(final long id, final String surname,
			final String name) throws SQLException {
		final List<Object> parameters = new ArrayList<Object>();
		parameters.add(new Long(id));
		parameters.add(surname);
		parameters.add(name);
		return BigDecimal.ONE
				.equals(transactionManager
						.executeSQLQuerySingleResult(
								"select count(*) from CUSTOMER where ID = ? and SURNAME = ? and NAME = ?",
								parameters));
	}

	public long getNextCustomerId() throws SQLException {
		final Object result = transactionManager.executeSQLQuerySingleResult(
				"select CUSTOMERSEQ.NEXTVAL from DUAL",
				TransactionManager.EMPTY_PARAMETERS);
		assert result != null;
		assert result instanceof BigDecimal : "Is: " + result.getClass();
		return ((BigDecimal) result).longValue();
	}


}
