create table users
(
    id serial,
    username varchar,
    password_hash varchar
);

insert into users (username, password_hash) values ('timk', 'cebf2cb7f8d7c263837cf63e10cecb98a0560c181d34e6c4bdab3f28e619cabc'); -- password: verystrongpassword
