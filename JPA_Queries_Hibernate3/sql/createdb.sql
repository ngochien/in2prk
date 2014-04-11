drop sequence customerseq;
drop sequence addressseq;
drop sequence creditcardseq;
drop sequence bankseq;
drop sequence cardissuerseq;

drop table bank_customer;
drop table bank_office;
drop table creditcard;
drop table customer;
drop table address;
drop table bank;
drop table cardissuer;

create table address (
	id number(32) primary key,
	postcode varchar2(10),
	city varchar2(100),
	street varchar2(100)
);

create sequence addressseq;

create table customer (
	id number(32) primary key,
	name varchar2(50),
	surname varchar2(50),
	home_address_id number(32) references address(id)
);

create sequence customerseq;

create table cardissuer (
	id number(32) primary key,
	name varchar2(200) unique
);

create sequence cardissuerseq;

create table creditcard (
	id number(32) primary key,
	ccnumber varchar2(16) unique,
	cctype varchar2(6) not null,
	customer_id number(32) references customer(id) not null,
	issuer_id number(32) references cardissuer(id) not null,
	constraint creditcard_chk_cctype check (cctype in ('CREDIT', 'DEBIT'))
);

create sequence creditcardseq;

create table bank (
	id number(32) primary key,
	name varchar2(200) unique
);

create sequence bankseq;

create table bank_customer (
	bank_id number(32) references bank(id),
	customer_id number(32) references customer(id),
	constraint BANK_CUSTOMER_PK primary key (bank_id, customer_id) 
);

create table bank_office (
	bank_id number(32) references bank(id),
	address_id number(32) references address(id),
	constraint BANK_OFFICE_PK primary key (bank_id, address_id) 
);