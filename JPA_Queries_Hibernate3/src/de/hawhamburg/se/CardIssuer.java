package de.hawhamburg.se;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class CardIssuer {

	private long id;
	private String name;

	public CardIssuer() {
		//
	}

	public CardIssuer(final String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CARDISSUERGEN")
	@SequenceGenerator(name = "CARDISSUERGEN", sequenceName = "CARDISSUERSEQ")
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

	public String toString() {
		return "CardIssuer[id=" + getId() + ", name=" + getName() + "]";
	}

}
