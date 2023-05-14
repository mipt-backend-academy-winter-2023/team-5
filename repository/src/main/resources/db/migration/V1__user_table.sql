create table users
(
    id serial,
    username varchar,
    password_hash varchar
);

insert into users (username, password_hash) values ('timk', 'verystrongpassword');
