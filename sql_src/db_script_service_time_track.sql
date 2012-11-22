CREATE TABLE plash.service_time_track(
	record_id bigserial NOT NULL PRIMARY KEY,
	class_name text,
	tag text, 
	elapsed_time integer,
	end_time timestamp WITHOUT TIME ZONE
);