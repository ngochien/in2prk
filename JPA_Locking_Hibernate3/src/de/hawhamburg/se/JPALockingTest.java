package de.hawhamburg.se;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vor dem Test muss die Datenbank mit dem Skript sql/createdb.sql erzeugt
 * worden sein.
 * 
 * Das SQL-Skript muss auch angepasst werden, um diesen Test zum Laufen zu
 * bringen!
 */
public class JPALockingTest {

	// Die folgenden drei Konstanten müssen jeweils angepasst werden:
	private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";
	private static final String DB_USER = "hr";
	private static final String DB_PASSWORD = "3113";
	// Ende

	private static final Logger LOG = LoggerFactory
			.getLogger(JPALockingTest.class);

	private static final String SURNAME_1 = "Felix";
	private static final String NAME_1 = "Furchtlos";
	private static final String SURNAME_2 = "Rudi";
	private static final String NAME_2 = "Ratlos";

	private static TransactionManager transactionManager;

	private EntityManagerFactory emf = null;
	private EntityManager em1 = null;
	private EntityManager em2 = null;

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
		closeEM(em1);
		em1 = null;
		closeEM(em2);
		em2 = null;
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

	private void closeEM(final EntityManager em) {
		if (em != null) {
			try {
				em.close();
			} catch (final Throwable ex) {
				LOG.warn("while closing entity manager", ex);
			}
		}
	}

	private void createEntityManagers() {
		emf = Persistence.createEntityManagerFactory("haw_demo");
		em1 = emf.createEntityManager();
		em2 = emf.createEntityManager();
	}

	@org.junit.Test
	public void testEntityManagers() throws SQLException {
		createEntityManagers();
		em1.getTransaction().begin();
		em2.getTransaction().begin();
		em1.getTransaction().commit();
		em2.getTransaction().commit();
	}

	@org.junit.Test
	public void testOptimisticLocking() throws SQLException {
		createEntityManagers();
		final long id1 = insertCustomerInDB(SURNAME_1, NAME_2);
		final long id2 = insertCustomerInDB(SURNAME_2, NAME_1);

		em1.getTransaction().begin();
		em2.getTransaction().begin();

		final Customer customer_1_1 = em1.find(Customer.class, new Long(id1));
		final Customer customer_1_2 = em1.find(Customer.class, new Long(id2));
		
		final Customer customer_2_1 = em2.find(Customer.class, new Long(id1));
		final Customer customer_2_2 = em2.find(Customer.class, new Long(id2));
		
		customer_1_1.setName(NAME_1);
		customer_1_2.setName(NAME_2);
		em1.persist(customer_1_1);
		em1.persist(customer_1_2);
		
		customer_2_1.setSurname(SURNAME_2);
		customer_2_2.setSurname(SURNAME_1);
		em2.persist(customer_2_1);
		em2.persist(customer_2_1);
		
		em1.getTransaction().commit();
		try {
			em2.getTransaction().commit();
			Assert.fail("RollbackException expected");
		} catch (final RollbackException ex) {
			Assert.assertTrue(causedByOptimisticLockException(ex));
		}

		Assert.assertTrue(isCustomerOnDB(id1, SURNAME_1, NAME_1));
		Assert.assertTrue(isCustomerOnDB(id2, SURNAME_2, NAME_2));
	}

	private boolean causedByOptimisticLockException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		LOG.info(ex.toString());
		if (ex instanceof OptimisticLockException) {
			return true;
		}
		return causedByOptimisticLockException(ex.getCause());
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

}
