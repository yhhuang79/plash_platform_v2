package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.math.*;
import java.net.*;


import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.*;
import org.json.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.DB.*;

/**
 * This Component manages the trip information. This component performs task according to the task_id feed to the component. <br>
 * This component does: <br>
 * task_id = 1 : Set trip name <br>
 * task_id = 2 : Populate trip info <br>
 * Map key descriptions: <br>
 * 			Map key-value pairs are served as additional parameters. <br> 
 * 			task_id specifies which task to perform. <br>
 * 			To perform task with task_id = 1, the caller must also specify userid and trip_id to indicate which trip record to access. <br> 
 * 			If the corresponding trip is found, the trip name is set to be the name specified by trip_name.  <br> 
 * 			To perform task with task_id = 2: <br> 
 * 			The caller may optionally specify level value to indicate which level of information to generate <br>
 * 			If level is not specified, a default value(currently 1) will be used.
 * 			the caller may optionally specify max_proc_time to limit the maximum time to be spent. The unit is in seconds <br>
 * 			Notice that the program does not force calculation termination immediately when the this limit is reached. <br>
 * 			Rather, the program continues current calculations and finishes up the current trip. <br>    
 * 			Current default maximum processing time is 1 hour (3600) <br><br>
 * 
 * Example: TripInfoManagerComponent?task_id=1&trip_id=5&userid=123&trip_name="happy trip to Chun Fu's room"
 *  
 * 
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains task_id and (optionally) the following keys: userid, trip_id, trip_name, max_proc_record
 */
public class TripInfoManagerComponent {


	private SessionFactory sessionFactory;
	private Session tskSession; //task session

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public TripInfoManagerComponent() {

	}

	public Object greet(Map map) {
		
		System.out.println("Test Start:\t"	+ Calendar.getInstance().getTimeInMillis());
		
		tskSession = sessionFactory.openSession();
		
		
	     
		try {

			switch (Integer.parseInt(map.remove("task_id").toString())) {

			case 1:
				int userid = Integer.parseInt(map.remove("userid").toString());
				int trip_id = Integer.parseInt(map.remove("trip_id").toString());				
				setTripName(userid,trip_id,map.remove("trip_name").toString());
				break;
			case 2:
				String tmpLevel, tmpProcTime;
				int level, max_proc_time;
				if ((tmpLevel = (String)map.remove("level")) == null) {
					level = 1; //modify the default level here
				} else {
					level = Integer.parseInt(tmpLevel);
				}//fi
				if ((tmpProcTime = (String)map.remove("max_proc_time")) == null) {
					max_proc_time = 3600000; //modify the default max_proc_time here
				} else {
					max_proc_time = Integer.parseInt(tmpProcTime)*1000;
				}//fi			
				scanDB(level, max_proc_time);
				
				break;
			case 3:
				break;
			default:				
				break;
			}//end switch
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("TripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
	        System.out.println("TripInfoManagerComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("TripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("TripInfoManagerComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		

		
		System.out.println("Test End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}//end method
	
	/**
	 * This method sets trip name<br>
	 * If the specified trip info record is not found, then the operation simply stops without notifying the caller <br><br>
	 * Example:	setTripName(1,2,"my trip");
	 * 
	 * @author	Yi-Chun Teng 
	 * @param	userid Indicates user's id  
	 * @param	trip_id Indicates trip id
	 * @param	trip_name The trip name to be set 	
	 * 
	 */
	private void setTripName(int userid, int trip_id, String trip_name){
		//obtaint the record
    	Criteria criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripInfo.add(Restrictions.eq("userid", userid));
    	criteriaTripInfo.add(Restrictions.eq("trip_id", trip_id));
		try {
			T_TripInfo tripInfoRec = (T_TripInfo) criteriaTripInfo.uniqueResult();
			//Check whether such trip record exists or not and is updated or not
			if (tripInfoRec == null) {
				//Create a new tripInfo record but set the update status value to 0
				tripInfoRec = new T_TripInfo();
				tripInfoRec.setUserid(userid);
				tripInfoRec.setTrip_id(trip_id);					
			}//fi				
				tripInfoRec.setTrip_name(trip_name);							
				tskSession.save(tripInfoRec);
				tskSession.beginTransaction().commit();			

											
		} catch (HibernateException he) {
			return;
			//most likely due to duplicate matching result
			//handle hibernation exception here, to be implemented
			
			//DBValidityScan(postgistemplate.user_location_point_time, 3600);
			
			//Map argMap = new Map();			
			//argMap.put("DB","postgistemplate");
			//argMap.put("table","user_location_point_time");
			//argMap.put("max_proc_time","3600");
			//new DBValidityScanComponent().greet(argMap);
			//DBValidityScanComponent pDVScanComponent = ComponentPool.get(DBValidityScanComponent.class);
			//pDVScanComponent.greet(argMapd);
		}//end try catch			
	
	}//end method
	
	/**
	 * This method tries to scan the DB <br>
	 * 
	 * @author Yi-Chun Teng
	 * @param level Indicates the level the trip info should be updated to
	 * @param max_proc_time This value indicates the time allocated to process the database. 
	 * 						Notice that the program does not force calculation termination immediately when the this limit is reached.
	 * 						Rather, the program continues current calculations and finishes up the current trip.    
	 */
	private void scanDB(int level, int max_proc_time) {
		
		long startTime = Calendar.getInstance().getTimeInMillis();
		long currentTime = startTime;
				
    	
    	//First, get a list of unique userid, trip_id pairs
    	T_TripIdent currUPLTrec; //current user poiknt location time record	    	
    	Criteria criteriaUPLT = tskSession.createCriteria(T_UserPointLocationTime.class); //criteria for table user_point_location_time
    	ProjectionList uniqUIDTIDprojList = Projections.projectionList(); 
    	uniqUIDTIDprojList.add(Projections.property("userid"),"userid");
    	uniqUIDTIDprojList.add(Projections.property("trip_id"),"trip_id");
    	criteriaUPLT.setProjection(Projections.distinct(uniqUIDTIDprojList));
    	//criteriaUPLT.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP );
    	criteriaUPLT.setResultTransformer(Transformers.aliasToBean(T_TripIdent.class) );
    	@SuppressWarnings("unchecked")    	
    	//Iterator<Map> tripListItr = (Iterator<Map>)criteriaUPLT.list().iterator();
    	Iterator<T_TripIdent> tripListItr = criteriaUPLT.list().iterator();
    	if (!tripListItr.hasNext()) { //empty list
    		return;
    	}//fi
    	
    	//A list of unique user id and trip id has been extracted, now scan through each entry and check for info status
    	Criteria criteriaTripInfo;//criteria for table Trip_Info
    	int tmpUserID, tmpTripID;
    	T_TripInfo currTripInfoRec; //current trip info record
    	System.out.println("Size of trip: " + criteriaUPLT.list().size() );
		do {
			//get the ids
			currUPLTrec = tripListItr.next();			
			tmpUserID = currUPLTrec.getUserid();
			tmpTripID = currUPLTrec.getTrip_id();
			
		
			//Map currRecMap = tripListItr.next();
			//System.out.println(currRecMap.remove("userid") + " : " + currRecMap.remove("trip_id") + " : ");
			
			criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
			criteriaTripInfo.add(Restrictions.eq("userid", tmpUserID));
			criteriaTripInfo.add(Restrictions.eq("trip_id", tmpTripID));	
			try {
				currTripInfoRec = (T_TripInfo) criteriaTripInfo.uniqueResult();
			} catch (HibernateException he) {
				continue;
				//most likely due to duplicate matching result
				//handle hibernate exception here, to be implemented 
			}//end try catch			
			//Check whether such trip record exists or not and is updated or not
			if (currTripInfoRec == null || currTripInfoRec.getUpdate_status() < level) {
				//if such trip did not exist or the update status does not meet specified level
				//then generate it!
				generateLevelOneTripStatus(tmpUserID, tmpTripID,currTripInfoRec);
			} else {
				//this trip_info record is up-to-date
				continue;
				
			}//fi
				
			currentTime = Calendar.getInstance().getTimeInMillis();			
		}while(currentTime-startTime < max_proc_time && tripListItr.hasNext() );
	
		
	}//end method
	
	/**
	 * This method populate trip information, level one means the status info adheres level one definition <br>
	 * If the specified trip info record is not found, then the operation simply stops without notifying the caller <br><br>
	 * setTripName(1,2,"my trip");	  
	 * 
	 * @author	Yi-Chun Teng 
	 * @param	userid Indicates user's id  
	 * @param	trip_id Indicates trip id
	 * @param	tripInfoRec Indicates the POJO object to be manipulated. <br>
	 * 			If null, then it means no such entry exists in the TripInfo table. 
	 * 
	 */
	private void generateLevelOneTripStatus(int userid, int trip_id, T_TripInfo tripInfoRec){
		if (tripInfoRec == null) { //no such entry in TripInfo table
			tripInfoRec = new T_TripInfo();
			tripInfoRec.setUserid(userid);
			tripInfoRec.setTrip_id(trip_id);			
		}//fi
		
		//obtain a list of trip point 
		Criteria criteriaUPLT = tskSession.createCriteria(T_UserPointLocationTime.class); //criteria for table user_point_location_time
		criteriaUPLT.add(Restrictions.eq("userid", userid));
		criteriaUPLT.add(Restrictions.eq("trip_id", trip_id));
		List<T_UserPointLocationTime> tripRecList = (List<T_UserPointLocationTime>)criteriaUPLT.list();		
		if(tripRecList.size()<2) {
			//only 1 trip point
			//what to do? delete this trip or merge this point to nearest trip or ?		
		}//fi
		tripInfoRec.setNum_of_pts(tripRecList.size());
		//tripInfoRec.setTrip_st_address(getAddress(tripRecList.get(0).getGps().getCoordinate().y, tripRecList.get(0).getGps().getCoordinate().x));
		//use thread to get address
		GetAddrThread at = new GetAddrThread(tripRecList.get(0).getGps().getCoordinate().y, tripRecList.get(0).getGps().getCoordinate().x,tripInfoRec);
		at.run();
		
		if (tripInfoRec.getTrip_name() == null) { 
			//this trip doesn't have a name yet
			tripInfoRec.setTrip_name("untitled trip");
		}//fi
		tripInfoRec.setTrip_st(tripRecList.get(0).getTimestamp());
		tripInfoRec.setTrip_et(tripRecList.get(tripRecList.size()-1).getTimestamp());


		//calculate trip length
		Double tmpDist = new Double(0);
		for (int i = 1; i < tripRecList.size(); i++) {
			//do something here
			
			tmpDist += ((BigDecimal)tskSession.createSQLQuery(
			//java.math.BigDecimal test = (BigDecimal)tskSession.createSQLQuery(
			//Iterator itr = tskSession.createSQLQuery(
			
			
					"SELECT round(CAST(ST_Distance_Sphere(ST_GeomFromText('" + 
					tripRecList.get(i-1).getGps() +
					"',4326),ST_GeomFromText('" + 
					tripRecList.get(i).getGps() +
					"',4326)) As numeric),2);"

					).uniqueResult()).doubleValue(); //*/
								
			
		}//end for

		
		tripInfoRec.setTrip_length(tmpDist.intValue());
			
		try {
			at.join();
			tripInfoRec.setUpdate_status((short) 1);
		} catch (InterruptedException e) {
			// trip address failed to be set
			
			tripInfoRec.setUpdate_status((short) 0);			
		} //waits for thread at to finish;
		
		//Write the update to the trip info table						
		tskSession.save(tripInfoRec);
		tskSession.beginTransaction().commit();		
	}//end method	
	

	
	/**
	 * This class handles address retrieval in a separate thread	 * 
	 * 
	 */
	private class GetAddrThread extends Thread {
		private double lat;
		private double lon;
		private T_TripInfo tripInfoRec; //reference to trip info record
		
		/**
		 * Constructor
		 * @param lat Double value indicates the latitude
		 * @param lon Double value that indicates the long  
		 * @param tripInfoRec Reference to T_TripInfo instance currently working on
		 * 
		 */
		private GetAddrThread(double lat, double lon,T_TripInfo tripInfoRec) {
			this.lat = lat;
			this.lon = lon;
			this.tripInfoRec = tripInfoRec;
		}//end method

		/**
		 * Run the threaded task!
		 */

		public void run() {
		       String addr = new String("");
				try {
					URL addrRequestURL = new URL(
							"http://maps.googleapis.com/maps/api/geocode/json?latlng=" +
							lat +
							"," +
							lon +
							"&sensor=true"
							);

					URLConnection addrConnect = addrRequestURL.openConnection(); 
			        BufferedReader buffInReader = new BufferedReader( new InputStreamReader(addrConnect.getInputStream()));
	      
			        
		      
					//Make the reader be the JSONObject	      
			        
			        JSONObject jObj = new JSONObject(new JSONTokener(buffInReader));	        
			        buffInReader.close();					     
			        JSONArray jArray = ((JSONObject) (jObj.getJSONArray("results").get(0))).getJSONArray("address_components");
			        for (int i = 0; i < jArray.length(); i++ ) {
			        	addr = addr + ((JSONObject)jArray.get(i)).get("long_name") + " ";
			        }//rof */
			 			
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					tripInfoRec.setTrip_st_address("Error generating address");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					tripInfoRec.setTrip_st_address("Error generating address");
				} catch (JSONException e) {
					// TODO Auto-generated catch block			
					tripInfoRec.setTrip_st_address("Address not available");			
				}//try catch
				
				tripInfoRec.setTrip_st_address(addr);
							 
		}//end method
		
		
	}//end class	
}//end class
