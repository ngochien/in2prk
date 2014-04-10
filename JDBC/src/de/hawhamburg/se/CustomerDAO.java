package de.hawhamburg.se;

import java.sql.SQLException;
import java.util.List;

/**
 * Zugriff auf die Tabelle CUSTOMER.
 */
public interface CustomerDAO {

	/**
	 * Hole die n�chste ID f�r einen Customer aus der Sequenz CUSTOMERSEQ.
	 * 
	 * @return Neue ID.
	 * @throws SQLException
	 */
	long getNextCustomerId() throws SQLException;

	/**
	 * F�ge diesen Customer in die Datenbanktabelle CUSTOMER ein.
	 * 
	 * @param customer
	 *            Der einzuf�gende Customer.
	 * @throws SQLException
	 */
	void insertCustomer(Customer customer) throws SQLException;

	/**
	 * Selektiere alle Customer aus der Datenbanktabelle CUSTOMER.
	 * 
	 * @return Liste alle Customer (evtl. leer).
	 * @throws SQLException
	 */
	List<Customer> selectAllCustomers() throws SQLException;

	/**
	 * L�sche diesen Customer aus der Datenbanktabelle CUSTOMER.
	 * 
	 * @param customer
	 *            Der zu l�schende Customer.
	 * @return true, wenn der Customer gel�scht wurde; false, wenn nicht
	 * @throws SQLException
	 */
	boolean deleteCustomer(Customer customer) throws SQLException;

	/**
	 * �ndere diesen Customer auf der Datenbanktabelle CUSTOMER. Die Id wird
	 * nicht ge�ndert.
	 * 
	 * @param customer
	 *            Der zu �nderende Customer.
	 * @return true, wenn der Customer ge�ndert wurde; false, wenn nicht
	 * @throws SQLException
	 */
	boolean updateCustomer(Customer customer) throws SQLException;

}