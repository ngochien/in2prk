package de.hawhamburg.se;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {

	private final TransactionManager transactionManager;
	
	private static final class CustomerObjectBuilder implements
	TransactionManager.ObjectBuilder<Customer> {

		@Override
		public Customer buildObjectFromRow(final ResultSet rs)
				throws SQLException {
			
			Customer kunde= new Customer();
				kunde.setId(rs.getLong("ID"));
				kunde.setName(rs.getString("NAME"));
				kunde.setSurname(rs.getString("SURNAME"));
		
			
	        return kunde;
			
		}

	}

	public CustomerDAOImpl(final TransactionManager transactionManager) {
		assert transactionManager != null;
		this.transactionManager = transactionManager;
	}

	@Override
	public long getNextCustomerId() throws SQLException {
		long bla = Long.parseLong(transactionManager.executeSQLQuerySingleResult("Select Customerseq.NEXTVAL From DUAL", TransactionManager.EMPTY_PARAMETERS).toString());
		
		return bla;		

	}

	@Override
	public void insertCustomer(final Customer customer) throws SQLException {
   
		
		List<Object> ls = new ArrayList<Object>();	
		ls.add(customer.getId());
		ls.add(customer.getName());
		ls.add(customer.getSurname());
	
		transactionManager.executeSQLInsert("Insert into customer (ID, NAME, SURNAME) values (?,?,?)", ls);
		transactionManager.commit();
	
		
	}

	@Override
	public List<Customer> selectAllCustomers() throws SQLException {
		
		List<Customer> ls = new ArrayList<Customer>();	
		ls = transactionManager.executeSQLQuery("Select * From Customer", new CustomerObjectBuilder());
			
		return ls;	
	}

	@Override
	public boolean deleteCustomer(final Customer customer) throws SQLException {
		
	
		List<Object> ls = new ArrayList<Object>();	
		ls.add(customer.getId());
		transactionManager.executeSQLDeleteOrUpdate("DELETE FROM Customer Where ID= ?", ls);
		transactionManager.commit();
//		return false;
		//TODO
		return BigDecimal.ZERO.equals(
				transactionManager.executeSQLQuerySingleResult(
						"select count(*) from CUSTOMER where ID= ?", ls));
	}

	@Override
	public boolean updateCustomer(final Customer customer) throws SQLException {
		
		
		List<Object> ls = new ArrayList<Object>();
		ls.add(customer.getName());
		ls.add(customer.getSurname());
		ls.add(customer.getId());
		transactionManager.executeSQLDeleteOrUpdate("UPDATE Customer SET NAME= ?, SURNAME= ? WHERE ID= ?" , ls);
		transactionManager.commit();
//		return false;
		//TODO
		return BigDecimal.ONE.equals(
				transactionManager.executeSQLQuerySingleResult(
					"select count(*) from CUSTOMER where NAME=? and SURNAME=? and ID=?", ls));
	}
}
