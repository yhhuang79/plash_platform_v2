-- Table: user_location.check_in_info

-- DROP TABLE user_location.check_in_info;

CREATE TABLE user_location.check_in_info (
  id integer NOT NULL,
  message character varying(1024),
  emotion smallint,
  picture_uri character varying(100),
  CONSTRAINT check_in_info_pkey PRIMARY KEY (id )
);
