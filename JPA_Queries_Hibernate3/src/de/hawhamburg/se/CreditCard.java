package de.hawhamburg.se;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class CreditCard {

	private long id;
	private String number;
	private CreditCardType type;
	private Customer holder;
	private CardIssuer issuer;

	public CreditCard() {
		// empty
	}

	public CreditCard(final String number, final CreditCardType type) {
		this.number = number;
		this.type = type;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CREDITCARDGEN")
	@SequenceGenerator(name = "CREDITCARDGEN", sequenceName = "CREDITCARDSEQ")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "CCNUMBER")
	public String getNumber() {
		return number;
	}

	public void setNumber(final String number) {
		this.number = number;
	}

	@Column(name = "CCTYPE")
	@Enumerated(EnumType.STRING)
	public CreditCardType getType() {
		return type;
	}

	public void setType(final CreditCardType type) {
		this.type = type;
	}

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID")
	public Customer getHolder() {
		return holder;
	}

	public void setHolder(final Customer holder) {
		this.holder = holder;
	}

	@ManyToOne
	@JoinColumn(name = "ISSUER_ID")
	public CardIssuer getIssuer() {
		return issuer;
	}

	public void setIssuer(final CardIssuer issuer) {
		this.issuer = issuer;
	}

	public String toString() {
		return "CreditCard[id=" + getId() + ", number=" + getNumber()
				+ ", type=" + getType() + "]";
	}

}
