-- Не получилось, предлагаю показать как сделать запросы на лекции
-- CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE streets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Это просто геометки на карте, может дома, может перекрестки
CREATE TABLE points (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    x FLOAT NOT NULL,
    y FLOAT NOT NULL
    -- geom GEOMETRY(Point, 4326)
    -- у меня не получилось настроить запросы в scala в лекция тоже инфы не нашел
);

-- Это ребра между двумя точками, возможно не на улице, а путь от перекрестка до дома
CREATE TABLE edges (
    id SERIAL PRIMARY KEY,
    point_from INTEGER REFERENCES points(id) NOT NULL,
    point_to INTEGER REFERENCES points(id) NOT NULL,
    street_id INTEGER REFERENCES streets(id)
);