CREATE TABLE "user"
(
    "login" VARCHAR NOT NULL PRIMARY KEY,
    "password"  VARCHAR NOT NULL
);

INSERT INTO "user" (login, password)
VALUES  ('artem', '1234'),
        ('test', 'test');