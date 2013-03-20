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
	
	--outter loop, select criteria 1 record
	--Criteria include: distinct trip_id, userid 
	FOR criteria_rec IN SELECT distinct trip_id, userid FROM user_location.user_point_location_time LOOP
	
		out_user_id := criteria_rec.userid;
		out_trip_id := criteria_rec.trip_id;
		
		SELECT min(id) INTO st_pt_id FROM user_location.user_point_location_time WHERE trip_id = criteria_rec.trip_id and userid = criteria_rec.userid and latitude > -200;
		SELECT max(id) INTO end_pt_id FROM user_location.user_point_location_time WHERE trip_id = criteria_rec.trip_id and userid = criteria_rec.userid and latitude > -200;
		SELECT latitude, longitude INTO result_rec FROM user_location.user_point_location_time WHERE id = st_pt_id;
		IF result_rec.latitude is NULL THEN
			continue;
		END IF;
		st_pt_lon := result_rec.longitude;
		st_pt_lat := result_rec.latitude;

		
		SELECT latitude, longitude INTO result_rec FROM user_location.user_point_location_time WHERE id = end_pt_id;
		IF result_rec.latitude is NULL THEN
			continue;
		END IF;
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
	
	--outter loop, select criteria 1 record
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


