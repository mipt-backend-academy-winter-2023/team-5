-- Enable PostGIS extension on your database
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE streets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE points (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    geom GEOMETRY(Point, 4326)
);

CREATE TABLE edges (
    id SERIAL PRIMARY KEY,
    intersection_from INTEGER REFERENCES intersections(id),
    intersection_to INTEGER REFERENCES intersections(id),
    street_id INTEGER REFERENCES streets(id),
    geom GEOMETRY(LineString, 4326)
);