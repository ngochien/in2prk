package de.hawhamburg.se;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Bank {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BANKGEN")
	@SequenceGenerator(name = "BANKGEN", sequenceName = "BANKSEQ")
	private long id;
	
	@Column
	private String name;

	public Bank() {
		//
	}

	public Bank(final String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String toString() {
		return "Bank[id=" + getId() + ", name=" + getName() + "]";
	}

}
