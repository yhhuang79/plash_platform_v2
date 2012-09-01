package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;


/**
 *
 * This component move location point data into depreciated point table and delete the ones in the original table. <br>
 * 
 * The following parameters are required: <br>
 * userid : Required. This parameter indicates which user the input trip belongs to<br>
 * trip_id: Required. This parameter indicates which trip <br>
 * 				
 *  *  
 * @author  Yi-Chun Teng
 * @param	map A map object that contains userid, trip_id, update_status and any of the items listed above 
 *

 * @version   1.2, 09/03/2012
 * @param     
 * @return    return status 
 * @example   https://localhost:8080/DelTripComponent?userid=1&trip_id=2
 * 
 */
public class DelTripComponent extends PLASHComponent {
		
	/**
	 * Fields
	 */
	private int userid;
	private int trip_id;
	private Session tskSession; //task session



	public DelTripComponent() {

	}//end constructor

	
	public Object serviceMain(Map map) {
		

		try {
	        
	        if (map.containsKey("userid")) {
	        	userid = Integer.parseInt(map.get("userid").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "User ID must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi
	        if (map.containsKey("trip_id"))  {
	        	trip_id = Integer.parseInt(map.get("trip_id").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "Trip id must be specified";
				return returnUnsuccess(map,err);     
	        }//fi

		

			
			//select the rows using userid and tripid from user_point_location_time
	        tskSession = sessionFactory.openSession();

			
		    Criteria criteria = tskSession.createCriteria(T_TripData.class); 
		    criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString())));
		    criteria.add(Restrictions.eq("trip_id", Integer.parseInt(map.get("trip_id").toString())));
		    //insert the data to depreciated_location_data table
		    for(T_TripData obj : (List<T_TripData>)criteria.list()) {
		    	T_TripData u = obj;
		    	T_DepreciatedLocationData t = new T_DepreciatedLocationData();
		    	t.setId(u.getId());
		    	t.setGps(u.getGps());
		    	t.setLabel(u.getLabel());
		    	t.setTimestamp(u.getTimestamp());
		    	t.setServer_timestamp(u.getServer_timestamp());
		    	t.setTrip_id(u.getTrip_id());
		    	t.setUserid(u.getUserid());
		    	tskSession.save(t);
		    	tskSession.delete(obj); // delete data from original table
		    }//rof
		    //delete the entry in trip_info table with the same userid and trip_id
		    criteria = tskSession.createCriteria(T_TripInfo.class);
		    criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString())));
		    criteria.add(Restrictions.eq("trip_id", Integer.parseInt(map.get("trip_id").toString())));
		    tskSession.delete((T_TripInfo)criteria.uniqueResult());
		    /*
		    for(Object obj : criteria.list()) {
		    	tskSession.delete(obj);
		    }//rof */
			
			Transaction tx = tskSession.beginTransaction();	
			tx.commit();
	        tskSession.close();
	      
	        return 	returnSuccess(map);
	        
		} catch (ConstraintViolationException e) {
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 004;
	        err.explaination = "Insert or update on table violates foreign key constraint";
			return returnUnsuccess(map,err);			
		} catch (Exception e){
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 005;
	        err.explaination = e.toString();
	        e.printStackTrace();
			return returnUnsuccess(map,err);			
		}//end try
	
	}//end service main
}//end class
