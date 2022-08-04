CREATE TABLE IF NOT EXISTS "users"
(
    "user_id"  BIGINT PRIMARY KEY AUTO_INCREMENT,
    "email"    varchar,
    "login"    varchar,
    "name"     varchar,
    "birthday" timestamp
)
;


CREATE TABLE IF NOT EXISTS  "friendships"
(
    "user_id"   bigint,
    "friend_id" bigint,
    "confirmed" boolean,
    PRIMARY KEY ("user_id", "friend_id")
);

CREATE TABLE IF NOT EXISTS  "films"
(
    "film_id"      BIGINT PRIMARY KEY AUTO_INCREMENT,
    "name"         varchar,
    "description"  varchar,
    "release_date" date,
    "duration_min" int,
    "rating_id"    int,
    "likes"        bigint
);

CREATE TABLE IF NOT EXISTS  "films_ganres"
(
    "film_id"  bigint,
    "genre_id" int
);

CREATE TABLE IF NOT EXISTS  "ratings"
(
    "rating_id"   int,
    "rating_name" varchar(100)
);

CREATE TABLE IF NOT EXISTS "genres"
(
    "genre_id"   int primary key auto_increment,
    "genre_name" varchar(100)
);

CREATE TABLE IF NOT EXISTS  "likes"
(
    "film_id" bigint,
    "user_id" bigint,
    PRIMARY KEY ("film_id", "user_id")
);

ALTER TABLE "friendships"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "friendships"
    ADD FOREIGN KEY ("friend_id") REFERENCES "users" ("user_id");

ALTER TABLE "likes"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "likes"
    ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

-- ALTER TABLE "films_ganres"
--     ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");
--
-- ALTER TABLE "films_ganres"
--     ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("genre_id");

ALTER TABLE "ratings"
    ADD FOREIGN KEY ("rating_id") REFERENCES "films" ("rating_id");