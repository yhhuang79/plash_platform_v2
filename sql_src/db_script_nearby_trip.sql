

-- ====================

CREATE OR REPLACE FUNCTION nearby_trip(arg_latitude text,arg_longitude text,arg_query_radius text) RETURNS TEXT AS $$

                                                                            
DECLARE
                                                                                
      
        query_geog  geography;                                                                     
        curr_rec record;
        list TEXT;
        query_radius int;
        
        
BEGIN
      --initializations
       query_radius := CAST(arg_query_radius as int);
       query_geog :=ST_GeogFromText('POINT('||arg_longitude||' '||arg_latitude||')');
       CREATE TEMP TABLE candidate(
                dist float4,
                userid integer,
                trip_id integer
                  )
        ON COMMIT DROP;
        FOR curr_rec IN select  userid,trip_id,gps
        FROM user_location.user_point_location_time
        WHERE accu>0 and ST_DWithin(query_geog,geography(gps),query_radius)
        LOOP
            
               INSERT INTO candidate VALUES (ST_distance(query_geog,geography(curr_rec.gps)),
                                curr_rec.userid,curr_rec.trip_id);
        
        
        END LOOP;
            
        CREATE TEMP TABLE candidate1 ON COMMIT DROP AS
                select min(dist)||'~'|| userid||'~'|| trip_id as tmp From candidate 
                group by userid,trip_id order by min(dist) asc;
       
        
        select array_agg(tmp) into list From candidate1;    
       
        
        return list;  

        END;
        

$$ LANGUAGE plpgsql;
-- ========
 /* This function generates a single line in report file
 * Parameter:
 * arg_query_start_time_in_timestamp = the earliest time to include in the query
 *
 *	   
 */
 




-- ====================

