package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;

import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;


import javax.net.ssl.HttpsURLConnection;


import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.*;
import org.hibernate.type.*;
import org.json.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.*;

import tw.edu.sinica.iis.ants.PlashUtils;
import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

/**
 * This component returns the trip information.  <br>
 * 
 * This component takes a Map object that contains the following keys: <br>
 * userid : Required. This parameter indicates which user's trip to return <br>
 * trip_id: Optional. This parameter indicates which trip to return. This is optional <br>
 * 				If trip_id is absent, this component simply returns all trips belonging to that user. <br>
 * field_mask: Optional. This parameter indicates which columns in the trip info record are included. <br>
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
 * Example: GetTripInfoComponent?userid=5&trip_id=3&field_mask=0111110000000000  
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
			String tmpUserid, tmpTrip_id, tmpField_mask, tmpName = "*";
			if ((tmpUserid = (String)map.remove("userid")) == null) {
				//user id must be specified
				map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
		        System.out.println("GetTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
		        tskSession.close();
				return map;
			} else {
				userid = Integer.parseInt(tmpUserid);
			}//fi
			
			if ((tmpField_mask = (String)map.remove("field_mask")) == null) {
				field_mask = Integer.parseInt("1111111111111111",2);				
			} else {
				field_mask = Integer.parseInt(tmpField_mask,2);
				if (field_mask == 0) { //return nothing
			        tskSession.close();
					return map;
				}//fi 
			}//fi
			
			
			if ((tmpTrip_id = (String)map.remove("trip_id")) == null) {
				//return all trip
				int sort = 1, FirstResult = 0, MaxResult = 0;
				if(map.containsKey("sort")){
					String SortOrder = map.remove("sort").toString();
					if(SortOrder.equals("trip_id"))
						sort = 1;
					if(SortOrder.equals("trip_name"))
						sort = 2;
					if(SortOrder.equals("trip_st"))
						sort = 3;
					if(SortOrder.equals("trip_et"))
						sort = 4;
					if(SortOrder.equals("trip_length"))
						sort = 5;
					if(SortOrder.equals("num_of_pts"))
						sort = 6;
				}
				if(map.containsKey("FirstResult")){
				    FirstResult = Integer.parseInt(map.remove("FirstResult").toString());
				}
				if(map.containsKey("MaxResult")){
					MaxResult = Integer.parseInt(map.remove("MaxResult").toString());
				}
				
				if(map.containsKey("name")){
					tmpName = (String)map.remove("name");
				}

				List<Map> tmpList = getAllTripInfo(userid,field_mask,sort,FirstResult,MaxResult,tmpName);
				if (tmpList == null) {//error here
					tskSession.close();
					return map;
				} else {
					System.out.println("list: " + tmpList.toString() );
					map.put("tripInfoList", tmpList);
					map.put("tripInfoNum", getAllTripNum(userid));
				}
				
				System.out.println("GetTripInfoComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
		        tskSession.close();
				return map;				
				
			} else {

				trip_id = Integer.parseInt(tmpTrip_id);								
				map.putAll(getSingleTripInfo(userid,trip_id,field_mask));
				System.out.println("GetTripInfoComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
				tskSession.close();
				return map;
				

			}//fi			
			
			
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
	        System.out.println("GetTripInfoComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
	        tskSession.close();
			return map; //*/
			
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("GetTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
	        tskSession.close();
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
    	criteriaTripInfo.setProjection(addFilterList(filterProjList,field_mask & 32767));      	
   	
 
    	criteriaTripInfo.setProjection(filterProjList);    	
    	criteriaTripInfo.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	
		try {
			Map tripInfoRec = (Map) criteriaTripInfo.uniqueResult();

			//Check whether such trip record exists or not and is updated or not
			if (tripInfoRec == null) {	
				
				return null;
			}//fi	
			tripInfoRec.put("hash", PlashUtils.ParamToHash(userid, trip_id, tskSession));
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
	private List<Map> getAllTripInfo(int userid, int field_mask, int sort_order, int firstResult, int maxResult, String name) {
		
		//obtain the record
    	Criteria criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripInfo.add(Restrictions.eq("userid", userid));
    	if(firstResult != 0)
    		criteriaTripInfo.setFirstResult(firstResult);
    	if(maxResult != 0)
    		criteriaTripInfo.setMaxResults(maxResult);
    	ProjectionList filterProjList = Projections.projectionList();     	
    	criteriaTripInfo.setProjection(addFilterList(filterProjList,field_mask));
    	switch(sort_order) {
    		case 1:
    			criteriaTripInfo.addOrder(Order.desc("trip_id"));
    			break;
    		case 2:
    			criteriaTripInfo.addOrder(Order.desc("trip_name"));
    			break;
    		case 3:
    			criteriaTripInfo.addOrder(Order.desc("trip_st"));
    			break;
    		case 4:
    			criteriaTripInfo.addOrder(Order.desc("trip_et"));
    			break;
    		case 5:
    			criteriaTripInfo.addOrder(Order.desc("trip_length"));
    			break;
    		case 6:
    			criteriaTripInfo.addOrder(Order.desc("num_of_pts"));
    			break;    			
    		default:
    			criteriaTripInfo.addOrder(Order.desc("trip_id"));
    	}
    	//criteriaTripInfo.addOrder(Order.desc("trip_id"));
    	criteriaTripInfo.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

		try {
			List<Map> tripInfoList = (List<Map>) criteriaTripInfo.list();
			List<Map> resultList = new ArrayList<Map>();
			
			if(name != "*"){
				Iterator tls = tripInfoList.iterator();
				//Map oneTrip;
				while(tls.hasNext()) {
					Map tl = (Map) tls.next();
					if (tl.get("trip_name").toString().toLowerCase().startsWith(name.toLowerCase())) {
						tl.put("hash", PlashUtils.ParamToHash(userid, Integer.parseInt(tl.get("trip_id").toString()), tskSession));
						resultList.add(tl);
					}//fi */
				}//rof
			} else {
				Iterator tls = tripInfoList.iterator();
				//Map oneTrip;
				while(tls.hasNext()) {
					Map tl = (Map) tls.next();
					tl.put("hash", PlashUtils.ParamToHash(userid, Integer.parseInt(tl.get("trip_id").toString()), tskSession));
					resultList.add(tl);
				}//rof
			}
									
			return resultList;
											
		} catch (HibernateException he) {
			
			return null;
		}//end try catch			//*/
		
	}//end method
	
	private Integer getAllTripNum(int userid) {
		
		//obtain the record
    	Criteria criteriaTripNum = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripNum.add(Restrictions.eq("userid", userid));
    	ProjectionList filterProjList = Projections.projectionList();     	
    	criteriaTripNum.setProjection(addFilterList(filterProjList,32768));
    	criteriaTripNum.setProjection(Projections.rowCount());
    	int tripInfoNum = 0;
		try {
			List tripInfoNums = criteriaTripNum.list();
			Iterator it = tripInfoNums.iterator();
			  if (!it.hasNext()){
				  tripInfoNum = 0;
			  } else {
				  while(it.hasNext()){
					  Object count = it.next();
					  tripInfoNum = Integer.parseInt(count.toString());  
				  }
			  }
			return tripInfoNum;
											
		} catch (HibernateException he) {
			
			return 0;
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
