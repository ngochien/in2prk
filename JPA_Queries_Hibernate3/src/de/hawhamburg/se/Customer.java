package de.hawhamburg.se;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

/**
 * Customer.
 * 
 * Queries: selectCustomerNameBankOfficeStreet
 * 
 */
@Entity
@NamedQuery(name = "selectCustomersWithoutCreditCards", 
query = "FROM Customer c WHERE c.creditCards IS EMPTY")
public class Customer {

	private long id;
	private String name;
	private String surname;
	private Address homeAddress;
	private Set<CreditCard> creditCards;
	private Set<Bank> banks;

	public Customer() {
		// empty
	}

	public Customer(final String surname, final String name) {
		this.name = name;
		this.surname = surname;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTOMERGEN")
	@SequenceGenerator(name = "CUSTOMERGEN", sequenceName = "CUSTOMERSEQ")
	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	@Column
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Column
	public String getSurname() {
		return surname;
	}

	public void setSurname(final String surname) {
		this.surname = surname;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "HOME_ADDRESS_ID")
	public Address getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(final Address address) {
		this.homeAddress = address;
	}

	@OneToMany(mappedBy = "holder", cascade = CascadeType.ALL)
	public Set<CreditCard> getCreditCards() {
		if (creditCards == null) {
			creditCards = new HashSet<CreditCard>();
		}
		return creditCards;
	}

	public void setCreditCards(final Set<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}

	@ManyToMany
	@JoinTable(name = "BANK_CUSTOMER", joinColumns = { @JoinColumn(name = "CUSTOMER_ID") }, inverseJoinColumns = { @JoinColumn(name = "BANK_ID") })
	public Set<Bank> getBanks() {
		if (banks == null) {
			banks = new HashSet<Bank>();
		}
		return banks;
	}

	public void setBanks(final Set<Bank> banks) {
		this.banks = banks;
	}

	public String toString() {
		return "Customer[id=" + getId() + ", name=" + getName() + ", surname="
				+ getSurname() + "]";
	}
}
