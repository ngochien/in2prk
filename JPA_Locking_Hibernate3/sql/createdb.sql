drop sequence customerseq;
drop sequence addressseq;
drop sequence creditcardseq;
drop sequence bankseq;

drop table creditcard CASCADE CONSTRAINTS;
drop table customer CASCADE CONSTRAINTS;
drop table address CASCADE CONSTRAINTS;
drop table bank CASCADE CONSTRAINTS;

create table customer (
	id number(32) primary key,
	name varchar2(50),
	surname varchar2(50),
	version number(32)
);

create sequence customerseq;