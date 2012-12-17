 CREATE SCHEMA antrip_users;
CREATE SCHEMA antrip_data;


CREATE TABLE antrip_users.user_accounts(
	email text NOT NULL,
	password text NOT NULL,	
	PRIMARY KEY (email, password),		
	user_id integer UNIQUE NOT NULL,
	status smallint NOT NULL,
	first_name text,
	last_name text,
	nick_name text NOT NULL,
	phone_number text,
	register_source smallint
);

CREATE TABLE antrip_users.friend_lists(
	user_id integer NOT NULL REFERENCES antrip_users.user_accounts(user_id),
	user_id_friend integer NOT NULL REFERENCES antrip_users.user_accounts(user_id),
	group_id smallint NOT NULL,
	PRIMARY KEY (user_id, user_id_friend, group_id)
);


CREATE TABLE antrip_data.recording_states(
	user_id integer PRIMARY KEY REFERENCES antrip_users.user_accounts(user_id),
	last_point_id bigint
); 


CREATE TABLE antrip_data.trips(
	trajectory_id integer UNIQUE PRIMARY KEY,
	trip_id integer UNIQUE NOT NULL,
	user_id integer REFERENCES antrip_users.user_accounts(user_id)
);

CREATE INDEX antrip_data_trips_idx_user_trip_trajectory_id ON antrip_data.trips(user_id, trip_id, trajectory_id);



CREATE TABLE antrip_data.trajectory_points(
	record_id bigserial NOT NULL PRIMARY KEY,
	trajectory_id integer REFERENCES antrip_data.trips(trajectory_id),	
	point_id bigserial REFERENCES common_data.trajectory_points(point_id)
);

CREATE INDEX antrip_data_trajectory_points_idx_trajectory_id ON antrip_data.trajectory_points(trajectory_id);


CREATE TABLE antrip_data.trajectory_info(
	trajectory_id integer UNIQUE PRIMARY KEY,
	name text,
	start_time timestamp WITHOUT TIME ZONE,
	end_time timestamp WITHOUT TIME ZONE,
	trip_length integer,
	num_of_pts integer,
	st_addr_prt1 text,
	st_addr_prt2 text,	
	st_addr_prt3 text,
	st_addr_prt4 text,
	st_addr_prt5 text,
	et_addr_prt1 text,
	et_addr_prt2 text,	
	et_addr_prt3 text,
	et_addr_prt4 text,
	et_addr_prt5 text,
	update_status smallint

);


CREATE TABLE antrip_data.trip_info(
	trip_id integer UNIQUE PRIMARY KEY,
	name text,
	start_time timestamp WITHOUT TIME ZONE,
	end_time timestamp WITHOUT TIME ZONE,
	trip_length integer,
	num_of_pts integer,
	st_addr_prt1 text,
	st_addr_prt2 text,	
	st_addr_prt3 text,
	st_addr_prt4 text,
	st_addr_prt5 text,
	et_addr_prt1 text,
	et_addr_prt2 text,	
	et_addr_prt3 text,
	et_addr_prt4 text,
	et_addr_prt5 text,
	update_status smallint

);


CREATE TABLE antrip_data.extras(
	record_id integer PRIMARY KEY,
	trip_id integer REFERENCES antrip_data.trips(trip_id),
	text text,
	type smallint
	
);

CREATE INDEX antrip_data_extras_idx_trip_id ON antrip_data.extras(trip_id);


CREATE TABLE antrip_data.point_checkins (
	record_id bigserial PRIMARY KEY REFERENCES antrip_data.trajectory_points(record_id),
	message text,
	URI text,
	mood smallint,
	UR_MD5 text
);

CREATE TABLE antrip_data.trip_sharing (
	user_id integer NOT NULL REFERENCES antrip_users.user_accounts(user_id),
	user_id_friend integer NOT NULL REFERENCES antrip_users.user_accounts(user_id),
	trip_id integer NOT NULL,
	PRIMARY KEY (user_id, user_id_friend, trip_id)	
);
CREATE INDEX antrip_data_trip_sharing_idx_user_trip_id ON antrip_data.trip_sharing(user_id, trip_id);