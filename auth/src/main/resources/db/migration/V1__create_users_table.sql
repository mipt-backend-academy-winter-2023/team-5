CREATE TABLE "users"
(
    "id"         SERIAL,
    "login" VARCHAR NOT NULL,
    "password"  VARCHAR NOT NULL
);

INSERT INTO "users" (login, password)
VALUES  ('artem', '1234'),
        ('test', 'test');