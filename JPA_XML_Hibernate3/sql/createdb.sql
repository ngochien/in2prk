drop sequence customerseq;

drop table customer;

create sequence customerseq CASCADE CONSTRAINT;

create table customer (
	id number(32) primary key,
	name varchar2(50),
	surname varchar2(50)
);

