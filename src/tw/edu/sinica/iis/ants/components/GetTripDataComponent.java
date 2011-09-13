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
 * userid : Required. This parameter indicates which user's trip to return <br>
 * trip_id: Optional. This parameter indicates which trip to return. This is optional <br>
 * 			If trip_id is absent, this component simply returns the newest trip belonging to the user with id userid. <br>
 * field_mask: Optional. This mask indicates which columns in the trip data record are included
 * sort: Not enabled yet. This parameter indicate which column will be served as the sorting key
 * latest_pt_only: Optional. This is a boolean value indicating whether the returned result should be a list of all points or a single point indicating the latest point.
 * 			If return_latest is not specified, the default value is false and the component will return a list of all trip points.
 * fields and corresponding bit positions (from left to right):
 * 	1. timestamp
 * 	2. gps
 * 	3. server_timestamp
 * 	4. trip_id
 * 	5. label
 * 	6. alt
 * 	7. accu
 * 	8. spd
 * 	9. bear
 * 	10. accex
 * 	11. accey
 * 	12. accez
 * 	13. gsminfo
 * 	14. wifiinfo
 * 	
 * Example:  
 * 
 *   
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains trip data
 */
public class GetTripDataComponent extends PLASHComponent {



	private Session tskSession; //task session


	public Object serviceMain(Map map) {
		
		System.out.println("GetTripDataComponent Start:\t"	+ Calendar.getInstance().getTimeInMillis());
		
		tskSession = sessionFactory.openSession();
		
		
	    
		try {


			int userid, trip_id, field_mask;
			boolean return_latest;
			String tmpUserid, tmpTrip_id, tmpField_mask, tmpReturn_latest;
			if ((tmpUserid = (String)map.remove("userid")) == null) {
				//user id must be specified
				map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
		        System.out.println("GetTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());				
				return map;
			} else {
				userid = Integer.parseInt(tmpUserid);
			}//fi
			
			if ((tmpField_mask = (String)map.remove("field_mask")) == null) {
				field_mask = Integer.parseInt("11111111111111",2);				
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
			
			if ((tmpReturn_latest = (String)map.remove("return_latest")) == null) {
				return_latest = false;				
			} else {
				return_latest = Boolean.parseBoolean(tmpReturn_latest);
				//field_mask = Integer.parseInt(tmpField_mask,2);
			}//fi
			
			if(return_latest){
				//return latest trip point
				map.putAll(getLatestTripPt(userid,trip_id,field_mask));
				System.out.println("GetTripDataComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
				return map;				
				
			} else {
				//return all trip
				map.put("tripDataList", getTripData(userid, trip_id,field_mask));
				System.out.println("GetTripDataComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
				return map;				

			}//fi
			
   			
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
	
	/**
	 * This method return a trip's latest point
	 * @param userid Required. This parameter indicates user id.
	 * @param trip_id Required. This parameter indicates trip id.
	 * @param field_mask Required. This parameter indicates which field to be included in the result returned.
	 * @return a map object containing specified fields and corresponding values
	 * 			If such info is not found, the map will not contain corresponding key-value pairs	   
	 */
	private Map getLatestTripPt(int userid, int trip_id, int field_mask) {
		//obtain the record
		/*
    	Criteria criteriaTripData = tskSession.createCriteria(T_TripData.class);
    	criteriaTripData.add(Restrictions.eq("userid", userid));
    	criteriaTripData.add(Restrictions.eq("trip_id", trip_id));
    	ProjectionList filterProjList = Projections.projectionList();
    	criteriaTripData.setProjection(addFilterList(filterProjList,field_mask));      	
    	criteriaTripData.setProjection(filterProjList);    	
    	criteriaTripData.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP); //*/
    	
		try {
			/*
			Map tripDataRec = (Map) criteriaTripData.uniqueResult();

			//Check whether such trip record exists or not and is updated or not
			if (tripDataRec == null) {				
				return null;
			}//fi			
			return tripDataRec; //*/
			List<Map> resultList = getTripData(userid,trip_id,field_mask);
			if (resultList.size()  == 0 ) {
				return new HashMap();
			} else {
				return resultList.get(0);
			}//fi
			
								
		} catch (HibernateException he) {
			return null;
		}//end try catch			//*/
		
	}//end method
	
	/**
	 * This method returns all point data of a trip 
	 * @param userid Required. This parameter indicates user id.
	 * @param trip_id Required. This parameter indicates trip id.
	 * @param field_mask Required. This parameter indicates which field to be included in the result returned.
	 * @return a List object containing a list of map objects where each map object contains data of a point
	 */
	private List<Map> getTripData(int userid, int trip_id, int field_mask) {

		//obtain the record
    	Criteria criteriaTripData = tskSession.createCriteria(T_TripData.class);
    	criteriaTripData.add(Restrictions.eq("userid", userid));
    	ProjectionList filterProjList = Projections.projectionList();     	
    	criteriaTripData.setProjection(addFilterList(filterProjList,field_mask));
    	criteriaTripData.addOrder(Order.desc("timestamp"));
    	criteriaTripData.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	
		try {
			List<Map> tripDataList = (List<Map>) criteriaTripData.list();		
			return tripDataList;
											
		} catch (HibernateException he) {
			return null;
		}//end try catch			//*/
	}//end method	
	
	/**
	 * Add corresponding projection property according to field_mask
	 * 
	 * @param filterProjList The projection list to contain various properties
	 * @param field_mask Indicates which field to be included in the returned map
	 * @return
	 */
	private ProjectionList addFilterList(ProjectionList filterProjList, int field_mask) {

    	if ((field_mask & 8192) != 0) { 
        	filterProjList.add(Projections.sqlProjection("timestamp", new String[] {"timestamp"}, new Type[] { new StringType() }));
    	}//fi
    	if ((field_mask & 4096) != 0) { //4096 = 1000000000000
        	filterProjList.add(Projections.sqlProjection("ST_GeomFromWKB(ST_AsBinary(?))", new String[] {"gps"}, new Type[] { new StringType() }));    	
      //  	filterProjList.add(Projections.sqlProjection("ST_GeomFromWKB(ST_AsBinary(gps))", new String[] {"lon"}, new Type[] { new StringType() }));
    	}//fi
    	if ((field_mask & 2048) != 0) { 
        	filterProjList.add(Projections.property("server_timestamp"),"server_timestamp");  
    	}//fi
    	if ((field_mask & 1024) != 0) { //1024 = 10000000000
        	filterProjList.add(Projections.property("trip_id"),"trip_id");
    	}//fi
    	if ((field_mask & 512) != 0) { 
    		filterProjList.add(Projections.property("label"),"label");
    	}//fi
    	if ((field_mask & 256) != 0) { 
    		filterProjList.add(Projections.property("alt"),"alt");
    	}//fi
    	if ((field_mask & 128) != 0) { 
    		filterProjList.add(Projections.property("accu"),"accu");
    	}//fi
    	if ((field_mask & 64) != 0) { 
    		filterProjList.add(Projections.property("spd"),"spd");
    	}//fi
    	if ((field_mask & 32) != 0) { //32 = 100000
    		filterProjList.add(Projections.property("bear"),"bear");
    	}//fi
    	if ((field_mask & 16) != 0) { 
    		filterProjList.add(Projections.property("accex"),"accex");
    	}//fi
    	if ((field_mask & 8) != 0) { 
    		filterProjList.add(Projections.property("accey"),"accey");
    	}//fi
    	if ((field_mask & 4) != 0) { 
    		filterProjList.add(Projections.property("accez"),"accez");
    	}//fi
    	if ((field_mask & 2) != 0) { //2 = 10
    		filterProjList.add(Projections.property("gsminfo"),"gsminfo");
    	}//fi
    	if ((field_mask & 1) != 0) { //1=1
    		filterProjList.add(Projections.property("wifiinfo"),"wifiinfo");
    	}//fi
		return filterProjList;
		
	}//end method
	
	

	



}//end class
