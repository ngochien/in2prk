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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class Bank {

	private long id;
	private String name;
	private Set<Address> offices;

	public Bank() {
		//
	}

	public Bank(final String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BANKGEN")
	@SequenceGenerator(name = "BANKGEN", sequenceName = "BANKSEQ")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "BANK_OFFICE", joinColumns = { @JoinColumn(name = "ADDRESS_ID") }, inverseJoinColumns = { @JoinColumn(name = "BANK_ID") })
	public Set<Address> getOffices() {
		if (offices == null) {
			offices = new HashSet<Address>();
		}
		return offices;
	}

	public void setOffices(final Set<Address> offices) {
		this.offices = offices;
	}

	public String toString() {
		return "Bank[id=" + getId() + ", name=" + getName() + "]";
	}

}
