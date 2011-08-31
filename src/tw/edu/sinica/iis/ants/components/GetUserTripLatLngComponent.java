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
 * fields:
 * Example:  
 * 
 *   * 
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains userid and (optionally) trip_id
 */
public class GetUserTripLatLngComponent {


	private SessionFactory sessionFactory;
	private Session tskSession; //task session

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public GetUserTripLatLngComponent() {

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
				//return all trip
				System.out.println("GetTripInfoComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
				map.put("tripInfoList", getAllTripInfo(userid,0));
				return map;				
				
			} else {

				trip_id = Integer.parseInt(tmpTrip_id);				
				System.out.println("GetTripInfoComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
				map.putAll(getSingleTripInfo(userid,trip_id,0));
				return map;
				

			}//fi			
			
			
   			
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
	 * @return	A map that contains the trip info. 
	 * 			If such info is not found, the map will not contain corresponding key-value pairs
	 * 
	 */
	private Map getSingleTripInfo(int userid, int trip_id, int item_mask){
		//obtain the record
    	Criteria criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripInfo.add(Restrictions.eq("userid", userid));
    	criteriaTripInfo.add(Restrictions.eq("trip_id", trip_id));
    	ProjectionList filterProjList = Projections.projectionList(); 
    	filterProjList.add(Projections.property("trip_name"),"trip_name");
    	filterProjList.add(Projections.sqlProjection("trip_st", new String[] {"trip_st"}, new Type[] { new StringType() }));
    	filterProjList.add(Projections.sqlProjection("trip_et", new String[] {"trip_et"}, new Type[] { new StringType() }));    	
    	filterProjList.add(Projections.property("trip_length"),"trip_length");
    	filterProjList.add(Projections.property("num_of_pts"),"num_of_pts");    	
    	criteriaTripInfo.setProjection(filterProjList);
    	
    	criteriaTripInfo.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	
		try {
			Map tripInfoRec = (Map) criteriaTripInfo.uniqueResult();

			//Check whether such trip record exists or not and is updated or not
			if (tripInfoRec == null) {				
				return null;
			}//fi					
			return tripInfoRec;
											
		} catch (HibernateException he) {
			return null;
		}//end try catch			//*/
	}//end method
	
	/**
	 * This method get a list of trip info that belong to the specified user<br>
	 * Example:	getAllTripInfo(1,2);
	 * 
	 * @author	Yi-Chun Teng 
	 * @param	userid Indicates user's id  
	 * @return	A list of map that contains the individual trip info
	 * 
	 */
	private List<Map> getAllTripInfo(int userid, int item_mask) {
		
		//obtain the record
    	Criteria criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripInfo.add(Restrictions.eq("userid", userid));
    	ProjectionList filterProjList = Projections.projectionList(); 
    	filterProjList.add(Projections.property("trip_name"),"trip_name");
    	filterProjList.add(Projections.sqlProjection("trip_st", new String[] {"trip_st"}, new Type[] { new StringType() }));
    	filterProjList.add(Projections.sqlProjection("trip_et", new String[] {"trip_et"}, new Type[] { new StringType() }));    	
    	filterProjList.add(Projections.property("trip_length"),"trip_length");
    	filterProjList.add(Projections.property("num_of_pts"),"num_of_pts");    	
    	criteriaTripInfo.setProjection(filterProjList);
    	
    	criteriaTripInfo.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	
		try {
			//T_TripInfo tripInfoRec = (T_TripInfo) criteriaTripInfo.uniqueResult();
			List<Map> tripInfoList = (List<Map>) criteriaTripInfo.list();

			//Check whether such trip record exists or not and is updated or not
			if (tripInfoList.size() == 0) {
				return null;
			}//fi					
			return tripInfoList;
											
		} catch (HibernateException he) {
			return null;
		}//end try catch			//*/
		
	}//end method
	



}//end class
