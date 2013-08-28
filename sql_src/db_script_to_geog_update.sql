

-- ====================

CREATE OR REPLACE FUNCTION to_geog_update(arg_fb_id text) RETURNS text AS $$

                                                                            
DECLARE
                                                                                
        arg_time timestamp;
        arg_latitude text;
        arg_longitude text;
        coord_geog  geography;
        
        
BEGIN
       select lat into arg_latitude from postgistemplate.linkus.user_info WHERE fb_id= arg_fb_id;
       select lng into arg_longitude from postgistemplate.linkus.user_info WHERE fb_id= arg_fb_id;
       select time into arg_time from postgistemplate.linkus.user_info WHERE fb_id= arg_fb_id;
       coord_geog :=ST_GeogFromText('POINT('||arg_longitude||' '||arg_latitude||')');
       Update postgistemplate.linkus.user_info 
       Set coord = coord_geog
       Where fb_id = arg_fb_id ;
       Insert into postgistemplate.linkus.user_location_history values (arg_fb_id,arg_longitude,arg_latitude,coord_geog,arg_time);
       return '1';
       
END;
        

$$ LANGUAGE plpgsql;
--=======================
 /* This function generates the geography data type in the user_info table
 * Parameter:
 * fb_id text
 *	   
 */
 




-- ====================

