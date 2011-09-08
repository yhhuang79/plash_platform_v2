package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;

import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;


import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.*;
import org.hibernate.type.*;
import org.json.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.*;

import tw.edu.sinica.iis.ants.DB.*;

/**
 * This component returns the trip data.  <br>
 * 
 * This component takes a Map object that contains the following keys: <br>
 * userid : Indicates which user's trip to return <br>
 * trip_id: Indicates which trip to return. This is optional <br>
 * 			If trip_id is absent, this component simply returns the newest trip belonging to the user with id userid. <br>
 * field_mask: Indicates which columns in the trip data record are included
 * sort:
 * fields:
 * Example:  
 * 
 *   
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains trip data
 */
public class TestComponent extends PLASHComponent {



	private Session tskSession; //task session


	public Object serviceMain(Map map) {
		
		System.out.println("GetTripDataComponent Start:\t"	+ Calendar.getInstance().getTimeInMillis());
		
		tskSession = sessionFactory.openSession();
		
		
	    
		try {


			int userid, trip_id, field_mask;				
			String tmpUserid, tmpTrip_id, tmpField_mask;
			if ((tmpUserid = (String)map.remove("userid")) == null) {
				//user id must be specified
				map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
		        System.out.println("GetTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());				
				return map;
			} else {
				userid = Integer.parseInt(tmpUserid);
			}//fi
			
			if ((tmpField_mask = (String)map.remove("field_mask")) == null) {
				field_mask = Integer.parseInt("111111111111111",2);				
			} else {
				field_mask = Integer.parseInt(tmpField_mask,2);
			}//fi
			
			
			if ((tmpTrip_id = (String)map.remove("trip_id")) == null) {
				Criteria latestTripCriteria = tskSession.createCriteria(T_TripData.class);
				latestTripCriteria.add(Restrictions.eq("userid", userid));				    
		    	latestTripCriteria.setProjection(Projections.projectionList().add(Projections.max("trip_id")));      			   			 		    	  		    
				trip_id = (Integer)latestTripCriteria.uniqueResult();		   
				
			} else {

				trip_id = Integer.parseInt(tmpTrip_id);

			}//fi			
			
			//return all trip
			//map.put("tripInfoList", getAllTripInfo(userid,field_mask));
			System.out.println(" latest trip "  + trip_id);
			System.out.println("GetTripDataComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;				
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("GetTripDataComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
	        System.out.println("GetTripDataComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map; //*/
			
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("GetTripDataComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("GetTripDataComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (HibernateException he) { //bad db validity
	        System.out.println("GetTripDataComponent failure end3:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		

	}//end method
	

	



}//end class
