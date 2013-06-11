CREATE OR REPLACE FUNCTION weekly_report() RETURNS void AS $$

DECLARE
	num_of_id integer;
	num_of_rec bigint;
	num_of_trips integer;

BEGIN
	SELECT count(*) INTO num_of_id  FROM socialcore.login WHERE confirmed = true;
	SELECT count(*) INTO num_of_rec FROM user_location.user_point_location_time where userid <> 169;
	SELECT count(distinct(userid, trip_id)) INTO num_of_trips FROM user_location.user_point_location_time where userid <> 169;
	RAISE NOTICE 'Num of id: % Num of recs: % Num of trips: %' ,num_of_id, num_of_rec, num_of_trips;
	
	
END;

$$ LANGUAGE plpgsql;

--===========================


drop function survey_trips_v1();
CREATE OR REPLACE FUNCTION survey_trips_v1(
	OUT out_user_id integer, 
	OUT out_trip_id integer, 
	OUT st_pt_lon float8,
	OUT st_pt_lat float8,
	OUT end_pt_lon float8,
	OUT end_pt_lat float8) RETURNS SETOF record AS $$

DECLARE

	--temporary records
	criteria_rec record; --record contains fields as criteria
	result_rec record;
	st_pt_id bigint;
	end_pt_id bigint;
	


	
BEGIN
	
	--outer loop, select criteria 1 record
	--Criteria include: distinct trip_id, userid 
	FOR criteria_rec IN SELECT distinct trip_id, userid FROM user_location.user_point_location_time LOOP
	
		out_user_id := criteria_rec.userid;
		out_trip_id := criteria_rec.trip_id;
		
		SELECT min(id) INTO st_pt_id FROM user_location.user_point_location_time WHERE trip_id = criteria_rec.trip_id and userid = criteria_rec.userid and latitude > -200;
		IF st_pt_id is NULL THEN
			continue;
		END IF;
		SELECT max(id) INTO end_pt_id FROM user_location.user_point_location_time WHERE trip_id = criteria_rec.trip_id and userid = criteria_rec.userid and latitude > -200;
		IF end_pt_id is NULL THEN
			continue;
		END IF;
		
		SELECT latitude, longitude INTO result_rec FROM user_location.user_point_location_time WHERE id = st_pt_id;
		st_pt_lon := result_rec.longitude;
		st_pt_lat := result_rec.latitude;

		
		SELECT latitude, longitude INTO result_rec FROM user_location.user_point_location_time WHERE id = end_pt_id;
		end_pt_lon := result_rec.longitude;
		end_pt_lat := result_rec.latitude;		
					

		return next;
	END LOOP;	

END;

$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION survey_trips_v1() IS
$$
/*
 * This function scans over user_location.user_point_location_time output:
 * userid, trip_id, start point longitude, start point latitude, end point longitude, end point latitude 
 * 
 */
$$;
 
select survey_trips_v1();

--=====================================================================

drop function survey_trips_v2();
CREATE OR REPLACE FUNCTION survey_trips_v2(
	OUT out_point_id bigint, 
	OUT pt_lon float8,
	OUT pt_lat float8,
	OUT has_picture boolean) RETURNS SETOF record AS $$

DECLARE

	--temporary records
	criteria_rec record; --record contains fields as criteria
	result_rec record;
	st_pt_id bigint;
	end_pt_id bigint;
	

	
BEGIN
	
	--outer loop, select criteria 1 record
	--Criteria include: id, and coordinate

	FOR criteria_rec IN SELECT id, latitude, longitude FROM user_location.user_point_location_time WHERE checkin = true LOOP
	

		out_point_id := criteria_rec.id;
		pt_lon := criteria_rec.longitude;
		pt_lat := criteria_rec.latitude;		
		
		
		SELECT picture_uri INTO result_rec FROM user_location.check_in_info WHERE id = criteria_rec.id;

		IF result_rec.picture_uri is NULL THEN
			has_picture := false;
		ELSE
			has_picture := true;	
		END IF;
				

		return next;
	END LOOP;	

END;

$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION survey_trips_v2() IS
$$
/*
 * This function scans over user_location.user_point_location_time output:
 * point id, point longitude, point latitude, has picture or not
 * 
 */
$$;
 
select survey_trips_v2();

--==================================

CREATE OR REPLACE FUNCTION output_result_v3_1(arg_start_time_in_sec bigint, mode integer ) RETURNS text AS $$

DECLARE
	
	
	result text;
	rec_0 record;
	rec_1 record;
	rec_2 record;
	rec_3 record;
	rec_4 record;
	speed float4;
	start_time_in_timestamp timestamp;
	
BEGIN
	

	CASE 
	
		WHEN mode >= 900 and mode <= 999 THEN 
		
		
			SELECT elapsed_time INTO rec_0 FROM two_speed.experiment_results_v3 
					WHERE route_id = mode%900 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 0 ;
			SELECT elapsed_time INTO rec_1 FROM two_speed.experiment_results_v3 
					WHERE route_id = mode%900 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 1 ;
			SELECT elapsed_time INTO rec_2 FROM two_speed.experiment_results_v3 
					WHERE route_id = mode%900 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 2 ;
			SELECT elapsed_time INTO rec_3 FROM two_speed.experiment_results_v3 
					WHERE route_id = mode%900 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 3 ;	
			SELECT elapsed_time INTO rec_4 FROM two_speed.experiment_results_v3 
					WHERE route_id = mode%900 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 4 ;					

			start_time_in_timestamp :=	date '1970-1-1' + cast ( ('''' || arg_start_time_in_sec  || ' seconds''') as interval);
			
			IF rec_0.elapsed_time is null or rec_1.elapsed_time is null or rec_2.elapsed_time is null or rec_3.elapsed_time is null or rec_4.elapsed_time is null THEN
				return null;
			ELSE
						
				result := start_time_in_timestamp || ',' || rec_0.elapsed_time || ',' || rec_1.elapsed_time || ',' || rec_2.elapsed_time || ',' || rec_3.elapsed_time || ',' || rec_4.elapsed_time;
				return result;
			END IF;
					
			
					
		WHEN mode = 3100 THEN
		
			SELECT elapsed_time INTO rec_1 FROM two_speed.experiment_results_v3 
					WHERE route_id = 31 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 0 ;
			SELECT elapsed_time INTO rec_2 FROM two_speed.experiment_results_v3 
					WHERE route_id = 32 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 0 ;
			SELECT elapsed_time INTO rec_3 FROM two_speed.experiment_results_v3 
					WHERE route_id = 33 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 0 ;
	
			
			IF rec_1.elapsed_time is null or rec_2.elapsed_time is null or rec_3.elapsed_time is null THEN				
				return null;
			ELSEIF rec_1.elapsed_time < rec_2.elapsed_time and rec_1.elapsed_time < rec_3.elapsed_time THEN			
				result := '31 ';				
			ELSEIF rec_2.elapsed_time < rec_1.elapsed_time and rec_2.elapsed_time < rec_3.elapsed_time THEN				
				result := '32 ';			
			ELSEIF rec_3.elapsed_time < rec_2.elapsed_time and rec_3.elapsed_time < rec_1.elapsed_time THEN			
				result := '33 ';					
			END IF;
			
			result := result || '1:' || arg_start_time_in_sec;
			
		WHEN mode = 3101 THEN
		
			SELECT elapsed_time INTO rec_1 FROM two_speed.experiment_results_v3 
					WHERE route_id = 31 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 1 ;
			SELECT elapsed_time INTO rec_2 FROM two_speed.experiment_results_v3 
					WHERE route_id = 32 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 1 ;
			SELECT elapsed_time INTO rec_3 FROM two_speed.experiment_results_v3 
					WHERE route_id = 33 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 1 ;
	
			IF rec_1.elapsed_time is null or rec_2.elapsed_time is null or rec_3.elapsed_time is null THEN				
				return null;
			ELSEIF rec_1.elapsed_time < rec_2.elapsed_time and rec_1.elapsed_time < rec_3.elapsed_time THEN			
				result := '31 ';				
			ELSEIF rec_2.elapsed_time < rec_1.elapsed_time and rec_2.elapsed_time < rec_3.elapsed_time THEN				
				result := '32 ';			
			ELSEIF rec_3.elapsed_time < rec_2.elapsed_time and rec_3.elapsed_time < rec_1.elapsed_time THEN			
				result := '33 ';					
			END IF;
			
			result := result || '1:' || arg_start_time_in_sec;
			
		WHEN mode = 3102 THEN
		
			SELECT elapsed_time INTO rec_1 FROM two_speed.experiment_results_v3 
					WHERE route_id = 31 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 2 ;
			SELECT elapsed_time INTO rec_2 FROM two_speed.experiment_results_v3 
					WHERE route_id = 32 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 2 ;
			SELECT elapsed_time INTO rec_3 FROM two_speed.experiment_results_v3 
					WHERE route_id = 33 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 2 ;
	
			
			IF rec_1.elapsed_time is null or rec_2.elapsed_time is null or rec_3.elapsed_time is null THEN				
				return null;
			ELSEIF rec_1.elapsed_time < rec_2.elapsed_time and rec_1.elapsed_time < rec_3.elapsed_time THEN			
				result := '31 ';				
			ELSEIF rec_2.elapsed_time < rec_1.elapsed_time and rec_2.elapsed_time < rec_3.elapsed_time THEN				
				result := '32 ';			
			ELSEIF rec_3.elapsed_time < rec_2.elapsed_time and rec_3.elapsed_time < rec_1.elapsed_time THEN			
				result := '33 ';					
			END IF;
			
			result := result || '1:' || arg_start_time_in_sec;
			
		WHEN mode = 3103 THEN
		
			SELECT elapsed_time INTO rec_1 FROM two_speed.experiment_results_v3 
					WHERE route_id = 31 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 3 ;
			SELECT elapsed_time INTO rec_2 FROM two_speed.experiment_results_v3 
					WHERE route_id = 32 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 3 ;
			SELECT elapsed_time INTO rec_3 FROM two_speed.experiment_results_v3 
					WHERE route_id = 33 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 3 ;
	
			
			IF rec_1.elapsed_time is null or rec_2.elapsed_time is null or rec_3.elapsed_time is null THEN				
				return null;
			ELSEIF rec_1.elapsed_time < rec_2.elapsed_time and rec_1.elapsed_time < rec_3.elapsed_time THEN			
				result := '31 ';				
			ELSEIF rec_2.elapsed_time < rec_1.elapsed_time and rec_2.elapsed_time < rec_3.elapsed_time THEN				
				result := '32 ';			
			ELSEIF rec_3.elapsed_time < rec_2.elapsed_time and rec_3.elapsed_time < rec_1.elapsed_time THEN			
				result := '33 ';					
			END IF;
			
			result := result || '1:' || arg_start_time_in_sec;			

		WHEN mode = 3104 THEN
		
			SELECT elapsed_time INTO rec_1 FROM two_speed.experiment_results_v3 
					WHERE route_id = 31 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 4 ;
			SELECT elapsed_time INTO rec_2 FROM two_speed.experiment_results_v3 
					WHERE route_id = 32 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 4 ;
			SELECT elapsed_time INTO rec_3 FROM two_speed.experiment_results_v3 
					WHERE route_id = 33 and start_time_in_sec = arg_start_time_in_sec and experiment_id = 31 and speed_mode = 4 ;
	
			
			IF rec_1.elapsed_time is null or rec_2.elapsed_time is null or rec_3.elapsed_time is null THEN				
				return null;
			ELSEIF rec_1.elapsed_time < rec_2.elapsed_time and rec_1.elapsed_time < rec_3.elapsed_time THEN			
				result := '31 ';				
			ELSEIF rec_2.elapsed_time < rec_1.elapsed_time and rec_2.elapsed_time < rec_3.elapsed_time THEN				
				result := '32 ';			
			ELSEIF rec_3.elapsed_time < rec_2.elapsed_time and rec_3.elapsed_time < rec_1.elapsed_time THEN			
				result := '33 ';					
			END IF;
			
			result := result || '1:' || arg_start_time_in_sec;
			
		ELSE
			
			return 'bad mode, input mode is ' || mode;
			
	END CASE;
		

	

	return result;
	
END;

$$ LANGUAGE plpgsql;


COMMENT ON FUNCTION output_result_v3_1(bigint, integer) IS
$$
/*
 * This function generates a single line in report file
 * The format of the output depends on it's purpose. It can be a excel csv file or svn input file.
 * Precondition:
 * 		-database two_speed.experiment_results_v3 is available
 * 		-Arguments are correct, arguments are:
 * 			1. start_time_in_sec, a bigint value indicating the start_time_in_sec column
 * 			2. mode  
 * There are following modes:
 * 931: All time report, this report lists elapsed times for all speed modes for route 31
 * 932: All time report, this report lists elapsed times for all speed modes for route 32
 * 933: All time report, this report lists elapsed times for all speed modes for route 33
 * 		For mode 931~333, the line generated adheres csv format. Each line has the following fields:
 *  start time in sec, elapsed time in speed mode 0, in mode 1, in mode 2, in mode 3, in mode 4
 * 
 * 3100: Generate a single line for svm input file, the line format is: [route #][space][1:][starting time]
 *	   
 */
 
$$;

select output_result_v3_1(1212848520, 3100);

-- ====================


DROP FUNCTION get_total_distance(integer, integer);
CREATE OR REPLACE FUNCTION get_total_distance(arg_user_id integer, arg_trip_id integer ) RETURNS float4 AS $$

DECLARE
	
	--tmp variables
	
	curr_dist float4;

	curr_rec record;
	prev_coord_geog geography;
	curr_coord_geog geography;
	proc_count bigint;
	
	
	
BEGIN
	
	--check if such trip exists or the size of good pts in the trip specified is less than 2 

	
	--initializations
	proc_count := 0;
	curr_dist := 0;
	
	FOR curr_rec IN SELECT latitude, longitude 
	FROM user_location.user_point_location_time 
	WHERE userid = arg_user_id and trip_id = arg_trip_id and latitude > -200 LOOP
		
		proc_count := proc_count + 1;		
		curr_coord_geog := ST_GeographyFromText('POINT(' || curr_rec.longitude || ' ' ||  curr_rec.latitude || ')');
		
		IF proc_count = 1 THEN
			prev_coord_geog := curr_coord_geog;
			continue;		
		END IF;
		
		curr_dist := curr_dist + ST_Distance(prev_coord_geog, curr_coord_geog);
		prev_coord_geog := curr_coord_geog;
		--raise notice 'hahaha Mike is god % ', curr_dist;
	
	END LOOP;
	
	IF proc_count < 2  THEN		
		return null;
	ELSE
		return curr_dist;
	END IF;
				

	
END;

$$ LANGUAGE plpgsql;

select get_total_distance(143,270);


--=============================

drop function correction_patch_v1();
CREATE OR REPLACE FUNCTION correction_patch_v1() RETURNS void AS $$

DECLARE

	--temporary records
	criteria_rec record; --record contains fields as criteria
	result_rec record;
	st_pt_id bigint;
	end_pt_id bigint;
	tmp_lon float8;
	tmp_lat float8;
	iteration_count integer;

	
BEGIN
	
	--outer loop, select criteria 1 record
	--Criteria include: distinct trip_id, userid 
	iteration_count := 0;
	FOR criteria_rec IN SELECT id FROM user_location.user_point_location_time
	WHERE (latitude > 90 or latitude < -90) and latitude  > -990
	LOOP
		iteration_count := iteration_count + 1;
		tmp_lon := latitude FROM user_location.user_point_location_time WHERE id = criteria_rec.id;
		tmp_lat := longitude FROM user_location.user_point_location_time WHERE id = criteria_rec.id;
		UPDATE user_location.user_point_location_time SET latitude := tmp_lat  WHERE id = criteria_rec.id;
		UPDATE user_location.user_point_location_time SET longitude := tmp_lon  WHERE id = criteria_rec.id;
		
		IF iteration_count > 10 THEN
			return;
		END IF;

	END LOOP;	

END;

$$ LANGUAGE plpgsql;

 
select correction_patch_v1();

