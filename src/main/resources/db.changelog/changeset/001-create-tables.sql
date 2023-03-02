--liquibase formatted sql

--changeset eyakauleva:create_tables

create table users
(
    id bigserial,
	first_name varchar(45) not null,
	last_name varchar(45) not null,
	email varchar(100) not null,
	primary key (id)
);

--rollback drop table users;