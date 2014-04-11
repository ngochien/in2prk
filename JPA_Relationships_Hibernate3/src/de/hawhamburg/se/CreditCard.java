package de.hawhamburg.se;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class CreditCard {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CREDITCARDGEN")
	@SequenceGenerator(name = "CREDITCARDGEN", sequenceName = "CREDITCARDSEQ")
	private long id;
	
	@Column(name = "ccnumber")
	private String number;
	
	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer holder;

	public CreditCard() {
		// empty
	}

	public CreditCard(final String number) {
		this.number = number;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(final String number) {
		this.number = number;
	}

	public Customer getHolder() {
		return holder;
	}

	public void setHolder(final Customer holder) {
		this.holder = holder;
	}

	public String toString() {
		return "CreditCard[id=" + getId() + ", number=" + getNumber() + "]";
	}

}
