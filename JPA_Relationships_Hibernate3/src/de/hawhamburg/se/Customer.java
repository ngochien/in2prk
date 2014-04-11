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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTOMERGEN")
	@SequenceGenerator(name = "CUSTOMERGEN", sequenceName = "CUSTOMERSEQ")
	private long id;

	@Column
	private String name;
	@Column
	private String surname;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "home_address_id", unique = true)
	private Address homeAddress;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "holder")
	private Set<CreditCard> creditCards;

	@ManyToMany
	@JoinTable(name = "bank_customer",
				joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "bank_id", referencedColumnName = "id"))
	private Set<Bank> banks;

	public Customer() {
		// empty
	}

	public Customer(final String surname, final String name) {
		this.name = name;
		this.surname = surname;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(final String surname) {
		this.surname = surname;
	}

	public Address getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(final Address address) {
		this.homeAddress = address;
	}

	public Set<CreditCard> getCreditCards() {
		if (creditCards == null) {
			creditCards = new HashSet<CreditCard>();
		}
		return creditCards;
	}

	public void setCreditCards(final Set<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}

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
