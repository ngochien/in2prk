drop table customer;
create table customer (
	id number(32) primary key,
	name varchar2(50),
	surname varchar2(50)
);
drop sequence customerSeq;
create sequence customerSeq;