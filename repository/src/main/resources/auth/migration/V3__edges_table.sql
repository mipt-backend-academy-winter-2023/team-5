create table edges
(
    id int primary key,
    name varchar,
    fromId int not null,
    toId int not null
);

insert into edges (id, name, fromId, toId) values
(200, 'Dmitrovskoe', 100, 101),
(201, 'Tverskaya', 101, 102);