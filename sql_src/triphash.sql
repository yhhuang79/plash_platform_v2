-- Table: user_location.trip_hash

-- DROP TABLE user_location.trip_hash;

CREATE TABLE user_location.trip_hash
(
  id integer NOT NULL,
  userid integer NOT NULL,
  trip_id integer NOT NULL,
  hash character varying(32) NOT NULL,
  CONSTRAINT trip_hash_pkey PRIMARY KEY (hash )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_location.trip_hash
  OWNER TO postgres;
