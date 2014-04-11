package de.hawhamburg.se;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@org.hibernate.annotations.Entity(optimisticLock = org.hibernate.annotations.OptimisticLockType.ALL)
public class Customer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	private long id;
	private String name;
	private String surname;
	private Long version = 0L;
	
	

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
	public Long getVersion() {
		return version;
	}
	
	public void setVersion(Long version) {
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
