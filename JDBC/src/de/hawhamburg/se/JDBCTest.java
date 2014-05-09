package de.hawhamburg.se;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

/**
 * Vor dem Start der Tests sollten auf der Datenbank eine Tabelle CUSTOMER und
 * eine Sequenz CUSTOMERSEQ angelegt werden. Dazu kann das Skript
 * sql/createdb.sql genutzt werden.
 * 
 * Es ist die Klasse CustomerDAOImpl fertig zu implementieren.
 */
public class JDBCTest {

	// Die folgenden drei Konstanten müssen jeweils angepasst werden:
	private static final String DB_URL = "jdbc:oracle:thin:@//oracle:1521/inf09";
	private static final String DB_USER = "abl400";
	private static final String DB_PASSWORD = "Nh31011991";
	// Ende

	private static final String SURNAME_1 = "Felix";
	private static final String NAME_1 = "Furchtlos";
	private static final String SURNAME_2 = "Rudi";
	private static final String NAME_2 = "Ratlos";

	private static TransactionManager transactionManager;
	private static CustomerDAO customerDAO;

	@org.junit.BeforeClass
	public static void setUpClass() throws SQLException {
		transactionManager = new TransactionManager(DB_URL);
		transactionManager.connect(DB_USER, DB_PASSWORD);
		customerDAO = new CustomerDAOImpl(transactionManager);
	}

	@org.junit.AfterClass
	public static void tearDownClass() {
		customerDAO = null;
		transactionManager.disconnect();
		transactionManager = null;
	}

	@org.junit.Before
	public void setUp() throws SQLException {
		transactionManager.executeSQLDeleteOrUpdate("delete from CUSTOMER",
				TransactionManager.EMPTY_PARAMETERS);
		transactionManager.commit();
	}

	@org.junit.Test
	public void testGetNextCustomerId() throws SQLException {
		final long id1 = customerDAO.getNextCustomerId();
		final long id2 = customerDAO.getNextCustomerId();
		Assert.assertTrue(id1 != id2);
	}

	@org.junit.Test
	public void testCreateCustomer() throws SQLException {
		final long id = createCustomer(SURNAME_1, NAME_1);
		checkIsCustomerOnDB(id, SURNAME_1, NAME_1);
	}

	@org.junit.Test
	public void testQueryCustomers() throws SQLException {
		final long id1 = createCustomer(SURNAME_1, NAME_1);
		final long id2 = createCustomer(SURNAME_2, NAME_2);
		final List<Customer> customers = customerDAO.selectAllCustomers();
		Assert.assertEquals(2, customers.size());
		for (final Customer customer : customers) {
			final long idC = customer.getId();
			if (idC == id1) {
				Assert.assertEquals(SURNAME_1, customer.getSurname());
				Assert.assertEquals(NAME_1, customer.getName());
			} else if (idC == id2) {
				Assert.assertEquals(SURNAME_2, customer.getSurname());
				Assert.assertEquals(NAME_2, customer.getName());
			} else {
				Assert.fail("Expected " + id1 + " or " + id2 + ", found " + idC);
			}
		}
	}

	@org.junit.Test
	public void testDeleteCustomer() throws SQLException {
		createCustomer(SURNAME_1, NAME_1);
		createCustomer(SURNAME_2, NAME_2);
		final List<Customer> customers = customerDAO.selectAllCustomers();
		Assert.assertEquals(2, customers.size());
		customerDAO.deleteCustomer(customers.get(0));
		final List<Customer> customers2 = customerDAO.selectAllCustomers();
		Assert.assertEquals(1, customers2.size());
	}

	@org.junit.Test
	public void testUpdateCustomer() throws SQLException {
		final long id = createCustomer(SURNAME_1, NAME_1);
		final List<Customer> customers = customerDAO.selectAllCustomers();
		Assert.assertEquals(1, customers.size());
		final Customer customer = customers.get(0);
		customer.setName(NAME_2);
		customer.setSurname(SURNAME_2);
		customerDAO.updateCustomer(customer);
		checkIsCustomerOnDB(id, SURNAME_2, NAME_2);
	}

	private long createCustomer(final String surname, final String name)
			throws SQLException {
		final long id = customerDAO.getNextCustomerId();
		final Customer customer = new Customer();
		customer.setId(id);
		customer.setSurname(surname);
		customer.setName(name);
		customerDAO.insertCustomer(customer);
		return id;
	}

	private void checkIsCustomerOnDB(final long id, final String surname,
			final String name) throws SQLException {
		final List<Object> parameters = new ArrayList<Object>();
		parameters.add(new Long(id));
		parameters.add(surname);
		parameters.add(name);
		Assert.assertEquals(
				BigDecimal.ONE,
				transactionManager
						.executeSQLQuerySingleResult(
								"select count(*) from CUSTOMER where ID = ? and SURNAME = ? and NAME = ?",
								parameters));
	}

}
