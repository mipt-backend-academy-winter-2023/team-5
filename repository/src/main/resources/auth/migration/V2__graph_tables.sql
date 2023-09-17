create table nodes
(
    id int primary key,
    name varchar,
    latitude float not null,
    longitude float not null,
    nodeType int not null
);

insert into nodes (id, name, latitude, longitude, nodeType) values
(100, 'Mipt', 50.9, 100.5, 0),
(101, 'Okruzhnaya', 51.9, 101.5, 1),
(102, 'Kremlin', 52.9, 102.5, 0);

