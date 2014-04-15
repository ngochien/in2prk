package de.hawhamburg.se;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
public class Customer {

	private long id;
	private String name;
	private String surname;
	private long version;

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

	@Version
	public long getVersion() {
		return version;
	}

	// Ohne Setter läuft der Test nicht! Warum???
	public void setVersion(final long version) {
		this.version = version;
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

	public String toString() {
		return "Customer[id=" + getId() + ", name=" + getName() + ", surname="
				+ getSurname() + "]";
	}
}
