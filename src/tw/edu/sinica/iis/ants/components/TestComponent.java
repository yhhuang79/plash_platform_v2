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
 * This is test component.  <br>
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
			int tmpLat = (int) ((double)Math.random()*1150 + 25041390);
			int tmpLng = (int) ((double)Math.random()*1150 + 121614672);
			map.put("userid", userid);
			System.out.println("userid and tmp lat, lng are " + userid + ":"+ tmpLat + ":" + tmpLng);
			map.put("lat", tmpLat);
			map.put("lng", tmpLng);
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
