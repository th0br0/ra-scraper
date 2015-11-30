CREATE TABLE "venues" (
  "id"         INTEGER NOT NULL PRIMARY KEY,
  "name"       VARCHAR NOT NULL,
  "address"    VARCHAR NOT NULL,
  "country_id" INTEGER NOT NULL
);

CREATE TABLE "profiles" (
  "id"   VARCHAR NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL
);

CREATE TABLE "djs" (
  "id"   VARCHAR NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL
);

CREATE TABLE "events" (
  "id"          INTEGER NOT NULL PRIMARY KEY,
  "name"        VARCHAR NOT NULL,
  "description" VARCHAR,
  "date"        DATE    NOT NULL,
  "date_desc"   VARCHAR NOT NULL,
  "price"       REAL,
  "price_desc"  VARCHAR,
  "venue_id"    INTEGER,
  "owner_id"    VARCHAR NOT NULL
);

CREATE TABLE "event_djs" (
  "event_id" INTEGER NOT NULL,
  "dj_id"    VARCHAR NOT NULL,
  PRIMARY KEY ("event_id", "dj_id")
);

ALTER TABLE "events" ADD CONSTRAINT "OWNER_FK" FOREIGN KEY ("owner_id") REFERENCES "profiles" ("id") ON UPDATE RESTRICT ON DELETE NO ACTION;
ALTER TABLE "events" ADD CONSTRAINT "VENUE_FK" FOREIGN KEY ("venue_id") REFERENCES "venues" ("id") ON UPDATE RESTRICT ON DELETE NO ACTION;
ALTER TABLE "event_djs" ADD CONSTRAINT "EVENTDJ_DJ_FK" FOREIGN KEY ("dj_id") REFERENCES "djs" ("id") ON UPDATE RESTRICT ON DELETE NO ACTION;
ALTER TABLE "event_djs" ADD CONSTRAINT "EVENTDJ_EVENT_FK" FOREIGN KEY ("event_id") REFERENCES "events" ("id") ON UPDATE RESTRICT ON DELETE NO ACTION;