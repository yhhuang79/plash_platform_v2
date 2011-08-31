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
 * This component returns the trip information.  <br>
 * 
 * This component takes a Map object that contains the following keys: <br>
 * userid : Indicates which user's trip to return <br>
 * trip_id: Indicates which trip to return. This is optional <br>
 * 				If trip_id is absent, this component simply returns all trips belonging to that user. <br>
 * field_mask: Indicates which columns in the trip info record are included
 * Default fields:
 *  trip_name
trip_st
trip_et
trip_length
num_of_pts 
 * Example:  
 * 
 *   * 
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains userid and (optionally) trip_id
 */
public class GetUserTripIdComponent {


	private SessionFactory sessionFactory;
	private Session tskSession; //task session

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public GetUserTripIdComponent() {

	}

	public Object greet(Map map) {
		
		System.out.println("GetTripInfoComponent Start:\t"	+ Calendar.getInstance().getTimeInMillis());
		
		tskSession = sessionFactory.openSession();
		
		
	     
		try {


			int userid, trip_id;				
			String tmpUserid, tmpTrip_id;
			if ((tmpUserid = (String)map.remove("userid")) == null) {
				//user id must be specified
				map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
		        System.out.println("GetTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());				
				return map;
			} else {
				userid = Integer.parseInt(tmpUserid);
			}//fi
			if ((tmpTrip_id = (String)map.remove("trip_id")) == null) {
				trip_id = -1; //-1 means return all trip
			} else {
				trip_id = Integer.parseInt(tmpTrip_id);
			}//fi			
			
			
			System.out.println("GetTripInfoComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
			map.putAll(getSingleTripInfo(userid,trip_id,0));
			return map;
			
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
	        System.out.println("GetTripInfoComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("GetTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		

		

	}//end method
	
	/**
	 * This method get a single trip info<br>
	 * Example:	setTripName(1,2,"my trip");
	 * 
	 * @author	Yi-Chun Teng 
	 * @param	userid Indicates user's id  
	 * @param	trip_id Indicates trip id
	 * @return	A map that contains the trip info
	 * 
	 */
	private Map getSingleTripInfo(int userid, int trip_id, int item_mask){
		//obtaint the record
    	Criteria criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripInfo.add(Restrictions.eq("userid", userid));
    	criteriaTripInfo.add(Restrictions.eq("trip_id", trip_id));
    	ProjectionList filterProjList = Projections.projectionList(); 
    	filterProjList.add(Projections.property("trip_name"),"trip_name");
    	//filterProjList.add(Projections.property("trip_st"),"trip_st");
    	//filterProjList.add(Projections.property("trip_et"),"trip_et");
    	filterProjList.add(Projections.sqlProjection("trip_st as trip_st", new String[] {"trip_st"}, new Type[] { new StringType() }));
    	filterProjList.add(Projections.sqlProjection("trip_et as trip_et", new String[] {"trip_et"}, new Type[] { new StringType() }));    	
    	filterProjList.add(Projections.property("trip_length"),"trip_length");
    	filterProjList.add(Projections.property("num_of_pts"),"num_of_pts");    	
    	criteriaTripInfo.setProjection(filterProjList);
    	
    	criteriaTripInfo.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	
		try {
			//T_TripInfo tripInfoRec = (T_TripInfo) criteriaTripInfo.uniqueResult();
			Map tripInfoRec = (Map) criteriaTripInfo.uniqueResult();

			//Check whether such trip record exists or not and is updated or not
			if (tripInfoRec == null) {
				//Create a new tripInfo record but set the update status value to 0
				return null;
			}//fi			
			System.out.println("^.^y " + tripInfoRec.keySet().toString());
			System.out.println("^.^;; " + tripInfoRec.get("strip_st"));
			return tripInfoRec;
											
		} catch (HibernateException he) {
			return null;
		}//end try catch			//*/
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

	
	}//end method	
	


}//end class
