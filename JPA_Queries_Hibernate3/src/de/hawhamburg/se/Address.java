package de.hawhamburg.se;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Address {

	private long id;
	private String postcode;
	private String city;
	private String street;

	public Address() {
		// empty
	}

	public Address(final String postcode, final String city, final String street) {
		this.postcode = postcode;
		this.city = city;
		this.street = street;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDRESSGEN")
	@SequenceGenerator(name = "ADDRESSGEN", sequenceName = "ADDRESSSEQ")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(final String postcode) {
		this.postcode = postcode;
	}

	@Column
	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	@Column
	public String getStreet() {
		return street;
	}

	public void setStreet(final String street) {
		this.street = street;
	}

	public String toString() {
		return "Address[id=" + getId() + ", postcode=" + getPostcode()
				+ ", city=" + getCity() + ", street=" + getStreet() + "]";
	}

}
