
--changeset eyakauleva:insert_initial_data

insert into users(first_name, last_name, email) values('Ivan', 'Ivanov', 'iivanov@gmail.com');
insert into users(first_name, last_name, email) values('Petr', 'Petrov', 'ppetrov@gmail.com');
insert into users(first_name, last_name, email) values('Olya', 'Ulianova', 'oulianova@gmail.com');

--rollback delete from users where id>0;