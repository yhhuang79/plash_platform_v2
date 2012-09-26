package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;

import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;


import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.*;
import org.json.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.*;

import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

/**
 * This component manages the trip information. This component performs task according to the task_id fed to the component. <br>
 * This component does: <br>
 * task_id = 1 : Set trip name <br>
 * task_id = 2 : Populate trip info <br>
 * Map key descriptions: <br>
 * 			Map key-value pairs are served as additional parameters. <br> 
 * 			task_id specifies which task to perform. <br>
 * 			To perform task with task_id = 1, the caller must also specify userid and trip_id to indicate which trip record to access. <br> 
 * 			If the corresponding trip is found, the trip name is set to be the name specified by trip_name.  <br> 
 * 			To perform task with task_id = 2: <br>
 * 			The caller may optionally specify userid to indicate which person's trip to update. <br> 
 * 			The caller may optionally specify level value to indicate which level of information to generate <br>
 * 			If level is not specified, a default value(currently 1) will be used. <br>
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
 * @return	map A map object that contains trip data, with field names as keys and field data as values.
 */
public class TripInfoManagerComponent extends PLASHComponent{


	private Session tskSession; //task session




	public Object serviceMain(Map map) {
		
		System.out.println("TripInfoManagerComponent Start:\t"	+ Calendar.getInstance().getTimeInMillis());
		
		tskSession = sessionFactory.openSession();
		
		int userid, trip_id;
	     
		try {

			switch (Integer.parseInt(map.remove("task_id").toString())) {

			case 1:
				userid = Integer.parseInt(map.remove("userid").toString());
				trip_id = Integer.parseInt(map.remove("trip_id").toString());				
				setTripName(userid,trip_id,map.remove("trip_name").toString());
				break;
			case 2:
				String tmpUserid, tmpLevel, tmpProcTime;
				int level, max_proc_time;
				if ((tmpUserid = (String)map.remove("userid")) == null) {
					userid = -1; //meaning no userid is supplied
				} else {
					userid = Integer.parseInt(tmpUserid);
				}//fi
				
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
				scanDB(userid, level, max_proc_time);
				
				break;
			case 3:
				break;
			default:				
				break;
			}//end switch
			
			tskSession.close();
			return map;
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			tskSession.close();
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 003;
	        err.explaination = "NullPointerException occured, probably due to invalid argument";
			return returnUnsuccess(map,err);				
		} catch (NumberFormatException e) { //invalid arguments 
			tskSession.close();
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 003;
	        err.explaination = "NumberFormatException occured, invalid arguments";
			return returnUnsuccess(map,err);	
		}//end try catch
		


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
	 * @param userid Indicates which user's trip to scan. If -1 then scan the whole database for all users' trips
	 * @param level Indicates the level the trip info should be updated to
	 * @param max_proc_time This value indicates the time allocated to process the database. 
	 * 						Notice that the program does not force calculation termination immediately when the this limit is reached.
	 * 						Rather, the program continues current calculations and finishes up the current trip.  
	 */
	private void scanDB(int userid, int level, int max_proc_time) {
		
		long startTime = Calendar.getInstance().getTimeInMillis();
		long currentTime = startTime;
				
    	
    	//First, get a list of unique userid, trip_id pairs
    	T_TripIdent currUPLTrec; //current user poiknt location time record	    	
    	Criteria criteriaUPLT = tskSession.createCriteria(T_UserPointLocationTime.class); //criteria for table user_point_location_time
    	if (userid != -1) {
 //   		criteriaUPLT.add(Restrictions.eq("userid", userid));
    	} else {        	
    	}//fi    	

    	ProjectionList uniqUIDTIDprojList = Projections.projectionList();    	
    	uniqUIDTIDprojList.add(Projections.property("userid"),"userid");
    	uniqUIDTIDprojList.add(Projections.property("trip_id"),"trip_id");
    	criteriaUPLT.setProjection(uniqUIDTIDprojList);
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
			
			if (userid != -1 && tmpUserID != userid) {
				continue;
			}//fi
			
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
				//if such trip did not exist or the update status does not meet specified level, then generate it!
				generateLevelOneTripStatus(tmpUserID, tmpTripID,currTripInfoRec);
			} else {
				//this trip_info record is up-to-date
				continue;
				
			}//fi
				
			currentTime = Calendar.getInstance().getTimeInMillis();		
	
		}while(currentTime-startTime < max_proc_time && tripListItr.hasNext() ); //*/
	
		
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
		


		
		
		if (tripInfoRec.getTrip_name() == null) { 
			//this trip doesn't have a name yet
			tripInfoRec.setTrip_name("untitled trip");
		}//fi
		tripInfoRec.setTrip_st(tripRecList.get(0).getTimestamp());
		tripInfoRec.setTrip_et(tripRecList.get(tripRecList.size()-1).getTimestamp());
		
		//calculate trip length
		Double tmpDist = new Double(0);
		Geometry firstGPS = null;
		Geometry secondGPS = null;
		boolean getFirst = true;
		GetAddrThread stAddr = null;
		GetAddrThread etAddr = null;

		for (int i = 0; i < tripRecList.size(); i++) {
			
			//check if the gps is out of bound
			if (Math.abs(tripRecList.get(i).getLatitude()) <= 180 && Math.abs(tripRecList.get(i).getLongitude()) <= 180  ) {
				if (getFirst) {
					firstGPS = tripRecList.get(i).getGps();					
					getFirst = false;
					//use thread to get address
					stAddr = new GetAddrThread(firstGPS.getCoordinate().y, firstGPS.getCoordinate().x,tripInfoRec,(short) 0);
					stAddr.run();					
					continue;
				}//fi
					secondGPS = tripRecList.get(i).getGps();											
			} else {
				continue;
			}//fi
			
			double tmpSegDist = ((BigDecimal)tskSession.createSQLQuery(
		
			
					"SELECT round(CAST(ST_Distance_Sphere(ST_GeomFromText('" + 
					firstGPS +
					"',4326),ST_GeomFromText('" + 
					secondGPS +
					"',4326)) As numeric),2);"

					).uniqueResult()).doubleValue(); 
			tmpDist += tmpSegDist;
			firstGPS = secondGPS;
			System.out.println("Segment distance is " + tmpSegDist + ", GPS: " + firstGPS.toString() + " , " + secondGPS.toString());
		}//end for

		if (secondGPS != null) {
			etAddr = new GetAddrThread(secondGPS.getCoordinate().y, secondGPS.getCoordinate().x,tripInfoRec,(short) 1);
			etAddr.run();
		}//fi
		
		tripInfoRec.setTrip_length(tmpDist.intValue());
			
		try {
			stAddr.join();
			etAddr.join();
			tripInfoRec.setUpdate_status((short) 1);
		} catch (NullPointerException e) {
			tripInfoRec.setUpdate_status((short) 0);
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
		private short addrType; //start address or end address
		
		/**
		 * Constructor
		 * @param lat Double value indicates the latitude
		 * @param lon Double value that indicates the long  
		 * @param tripInfoRec Reference to T_TripInfo instance currently working on
		 * @param addrType Indicates the type of address. 0 = starting point's address, 1 = end point's address	
		 */
		private GetAddrThread(double lat, double lon,T_TripInfo tripInfoRec, short addrType) {
			this.lat = lat;
			this.lon = lon;
			this.tripInfoRec = tripInfoRec;
			this.addrType = addrType;
		}//end method

		/**
		 * Run the threaded task!
		 */

		public void run() {
		       String addr = new String("" );
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
					if (addrType == 0) {
						tripInfoRec.setSt_addr_prt5((String)((JSONObject)jArray.get(0)).get("long_name"));
						tripInfoRec.setSt_addr_prt4((String)((JSONObject)jArray.get(1)).get("long_name"));
						tripInfoRec.setSt_addr_prt3((String)((JSONObject)jArray.get(2)).get("long_name"));
						tripInfoRec.setSt_addr_prt2((String)((JSONObject)jArray.get(3)).get("long_name"));
						tripInfoRec.setSt_addr_prt1((String)((JSONObject)jArray.get(4)).get("long_name"));						
					} else if (addrType == 1) {
						tripInfoRec.setEt_addr_prt5((String)((JSONObject)jArray.get(0)).get("long_name"));
						tripInfoRec.setEt_addr_prt4((String)((JSONObject)jArray.get(1)).get("long_name"));
						tripInfoRec.setEt_addr_prt3((String)((JSONObject)jArray.get(2)).get("long_name"));
						tripInfoRec.setEt_addr_prt2((String)((JSONObject)jArray.get(3)).get("long_name"));
						tripInfoRec.setEt_addr_prt1((String)((JSONObject)jArray.get(4)).get("long_name"));						
					}//fi		
			 			
				} catch (MalformedURLException e) {
					if (addrType == 0) {
						tripInfoRec.setSt_addr_prt5("Invalid location data");
					} else if (addrType == 1) {
						tripInfoRec.setEt_addr_prt5("Invalid location data");
					}//fi
					
				} catch (IOException e) {
					if (addrType == 0) {
						tripInfoRec.setSt_addr_prt5("Address not available");
					} else if (addrType == 1) {
						tripInfoRec.setEt_addr_prt5("Address not available");
					}//fi					
				} catch (JSONException e) {
					if (addrType == 0) {
						tripInfoRec.setSt_addr_prt5("Invalid data");
					} else if (addrType == 1) {
						tripInfoRec.setEt_addr_prt5("Invalid data");
					}//fi		
				}//try catch
				
	
							 
		}//end method
		
		
	}//end class	
}//end class
