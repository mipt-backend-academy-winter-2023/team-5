CREATE TABLE "user"
(
    "id"         SERIAL,
    "login" VARCHAR NOT NULL,
    "password"  VARCHAR NOT NULL
);

INSERT INTO "user" (login, password)
VALUES  ('artem', '1234'),
        ('test', 'test');