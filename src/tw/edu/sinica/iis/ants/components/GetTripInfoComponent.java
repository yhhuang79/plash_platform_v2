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
 * field_mask: Indicates which columns in the trip info record are included. <br>
 * There are currently 15 fields that can be selected. Caller can enable them or disable them by set corresponding mask bits: <br>
 *  1. trip_id <br>
 * 	2. trip_name <br>
 * 	3. trip_st <br>
 * 	4. trip_et <br>
 * 	5. trip_length <br>
 * 	6. num_of_pts <br>
 * 	7. st_addr_prt1 <br>
 * 	8. st_addr_prt2 <br>
 * 	9. st_addr_prt3 <br>  
 * 	10. st_addr_prt4 <br>
 * 	11. st_addr_prt5 <br>
 * 	12. et_addr_prt1 <br>
 * 	13. et_addr_prt2 <br>
 * 	14. et_addr_prt3 <br>
 *  15. et_addr_prt4 <br>
 *  16. et_addr_prt5 <br>
 * Example: GetTripInfoComponent?userid=5&trip_ic=3&field_mask="0111110000000000  
 *  
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains userid, (optionally) trip_id and (optionally) field_mask 
 */
public class GetTripInfoComponent extends PLASHComponent {


	private Session tskSession; //task session



	public Object serviceMain(Map map) {
		
		System.out.println("GetTripInfoComponent Start:\t"	+ Calendar.getInstance().getTimeInMillis());
		
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
				//return all trip
				map.put("tripInfoList", getAllTripInfo(userid,field_mask));
				System.out.println("GetTripInfoComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
				return map;				
				
			} else {

				trip_id = Integer.parseInt(tmpTrip_id);								
				map.putAll(getSingleTripInfo(userid,trip_id,field_mask));
				System.out.println("GetTripInfoComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
				return map;
				

			}//fi			
			
			
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
	        System.out.println("GetTripInfoComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map; //*/
			
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
	 * @param	field_mask Indicates which field to be included in the returned map
	 * @return	A map that contains the trip info. 
	 * 			If such info is not found, the map will not contain corresponding key-value pairs
	 * 
	 */
	private Map getSingleTripInfo(int userid, int trip_id, int field_mask){
		//obtain the record
    	Criteria criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripInfo.add(Restrictions.eq("userid", userid));
    	criteriaTripInfo.add(Restrictions.eq("trip_id", trip_id));
    	ProjectionList filterProjList = Projections.projectionList();
    	criteriaTripInfo.setProjection(addFilterList(filterProjList,field_mask));      	
   	
 
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
	 * @param	field_mask Indicates which field to be included in the returned map
	 * @return	A list of map that contains the individual trip info
	 * 
	 */
	private List<Map> getAllTripInfo(int userid, int field_mask) {
		
		//obtain the record
    	Criteria criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripInfo.add(Restrictions.eq("userid", userid));
    	ProjectionList filterProjList = Projections.projectionList();     	
    	criteriaTripInfo.setProjection(addFilterList(filterProjList,field_mask));    	
    	criteriaTripInfo.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	
		try {
			List<Map> tripInfoList = (List<Map>) criteriaTripInfo.list();

			if (tripInfoList.size() == 0) {
				return tripInfoList;
			}//fi					
			return tripInfoList;
											
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
    	if ((field_mask & 32768) != 0) { //32768 = 1000000000000000
    		filterProjList.add(Projections.property("trip_id"),"trip_id");
    	}//fi    	 
    	if ((field_mask & 16384) != 0) { //16384 = 100000000000000
    		filterProjList.add(Projections.property("trip_name"),"trip_name");
    	}//fi
    	if ((field_mask & 8192) != 0) { 
        	filterProjList.add(Projections.sqlProjection("trip_st", new String[] {"trip_st"}, new Type[] { new StringType() }));
    	}//fi
    	if ((field_mask & 4096) != 0) { //4096 = 1000000000000
        	filterProjList.add(Projections.sqlProjection("trip_et", new String[] {"trip_et"}, new Type[] { new StringType() }));    	
    	}//fi
    	if ((field_mask & 2048) != 0) { 
        	filterProjList.add(Projections.property("trip_length"),"trip_length");  
    	}//fi
    	if ((field_mask & 1024) != 0) { //1024 = 10000000000
        	filterProjList.add(Projections.property("num_of_pts"),"num_of_pts");
    	}//fi
    	if ((field_mask & 512) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt1"),"st_addr_prt1");
    	}//fi
    	if ((field_mask & 256) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt2"),"st_addr_prt2");
    	}//fi
    	if ((field_mask & 128) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt3"),"st_addr_prt3");
    	}//fi
    	if ((field_mask & 64) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt4"),"st_addr_prt4");
    	}//fi
    	if ((field_mask & 32) != 0) { //32 = 100000
    		filterProjList.add(Projections.property("st_addr_prt5"),"st_addr_prt5");
    	}//fi
    	if ((field_mask & 16) != 0) { 
    		filterProjList.add(Projections.property("et_addr_prt1"),"et_addr_prt1");
    	}//fi
    	if ((field_mask & 8) != 0) { 
    		filterProjList.add(Projections.property("et_addr_prt2"),"et_addr_prt2");
    	}//fi
    	if ((field_mask & 4) != 0) { 
    		filterProjList.add(Projections.property("et_addr_prt3"),"et_addr_prt3");
    	}//fi
    	if ((field_mask & 2) != 0) { //2 = 10
    		filterProjList.add(Projections.property("et_addr_prt4"),"et_addr_prt4");
    	}//fi
    	if ((field_mask & 1) != 0) { //1=1
    		filterProjList.add(Projections.property("et_addr_prt5"),"et_addr_prt5");
    	}//fi
		return filterProjList;
		
	}//end method
	



}//end class
