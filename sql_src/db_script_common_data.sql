CREATE SCHEMA common_data;


CREATE TABLE common_data.trajectory_info(
	--trajectory_id integer UNIQUE PRIMARY KEY,
	trajectory_id serial PRIMARY KEY,
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
	update_status smallint NOT NULL

);

CREATE TABLE common_data.trajectory_points(
	trajectory_id integer NOT NULL REFERENCES common_data.trajectory_info(trajectory_id),
	point_id bigserial PRIMARY KEY,
	record_time timestamp WITHOUT TIME ZONE,
	latitude float8 NOT NULL,
	longitude float8 NOT NULL,
	receive_time timestamp WITHOUT TIME ZONE,
	altitude float4,
	accuracy float4,
	speed float4,
	bearing float4,
	accel_x float4,
	accel_y float4,
	accel_z float4,
	azimuth float4,
	pitch float4,
	roll float4
);

CREATE INDEX common_data_trajectory_points_idx_trajectory_id ON common_data.trajectory_points(trajectory_id);


CREATE TABLE common_data.point_device_data(
	point_id bigserial PRIMARY KEY REFERENCES common_data.trajectory_points(point_id),
	gsm_info text,
	wifi_info text,
	application smallint,
	device_info text,
	battery text,
	IMEI text,
	extra text
	
);

CREATE TABLE common_data.user_specified (
	point_id bigserial PRIMARY KEY REFERENCES common_data.trajectory_points(point_id),
	mood smallint,
	transportation_type smallint
);


CREATE TABLE common_data.point_binary_data(
	point_id bigserial PRIMARY KEY REFERENCES common_data.trajectory_points(point_id),
	postgis_point Geometry
);


