package de.hawhamburg.se;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDRESSGEN")
	@SequenceGenerator(name = "ADDRESSGEN", sequenceName = "ADDRESSSEQ")
	private long id;
	@Column
	private String street;

	public Address() {
		// empty
	}

	public Address(final String street) {
		this.street = street;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(final String street) {
		this.street = street;
	}

	public String toString() {
		return "Address[id=" + getId() + ", street=" + getStreet() + "]";
	}

}
