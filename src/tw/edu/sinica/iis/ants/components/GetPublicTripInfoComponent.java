package tw.edu.sinica.iis.ants.components;


import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import tw.edu.sinica.iis.ants.PlashUtils;
import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db.antrip.TripSharing;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get a list of trip info belonging to a user and a friend of him/her
 * 
 * @author	Yu-Hsiang Huang
 * @version 
 * @param   none
 * @return  a map containing a list of maps where each map contains
 * 			a trip ID, a latitude, a longitude and a timestamp. 
 * 			empty list if no matching entry found
 * @example	https://localhost:1234/in?userid=7&friendid=1       
 * @note	
 */


public class GetPublicTripInfoComponent extends PLASHComponent{


	private Session tskSession; //task session
	
	@Override
	public Object serviceMain(Map map) {
		
		tskSession = sessionFactory.openSession();    	

        System.out.println("GetPublicTripInfoComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        
        Criteria criteriaFriendAuth;
        //criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
        criteriaFriendAuth = tskSession.createCriteria(TripSharing.class);
        
		try {						
			criteriaFriendAuth.add(Restrictions.eq("id.userIdFriend", 0));
			//criteriaFriendAuth.add(Restrictions.eq("userBID", Integer.parseInt(map.get("userid").toString())));
			//criteriaFriendAuth.addOrder(Order.desc("id.tripId"));
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("code", 400);
			map.put("message", e.toString());
			map.put("GetPublicTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("GetPublicTripInfoComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("code", 400);
			map.put("message", e.toString());
			map.put("GetPublicTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("GetPublicTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		
		ArrayList<TripSharing> tripIDList = (ArrayList<TripSharing>) criteriaFriendAuth.list();
		List<Map> resultList = new ArrayList();
		Map resultEntryMap;
		System.out.println("GetPublicTripInfoComponent tripIDList.size : "+ tripIDList.size());
		
		for(int i = tripIDList.size()-1;  i >= 0 ;i--) {
			System.out.println("GetPublicTripInfoComponent UserAID : "+ tripIDList.get(i).getId().getUserId() + "    TripID : " + tripIDList.get(i).getId().getTripId());
			resultEntryMap = getSingleTripInfo(tripIDList.get(i).getId().getUserId(), tripIDList.get(i).getId().getTripId());
			if (resultEntryMap != null) {
				resultList.add(resultEntryMap);
			}//fi			
		}//rof
		map.put("code", 200);
		map.put("message", "ok");
        map.put("tripInfoList", resultList);
        map.put("tripInfoNum", tripIDList.size()-1);
        tskSession.close();
        System.out.println("GetPublicTripInfoComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
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
	private Map getSingleTripInfo(int userid, int trip_id){
		//obtain the record
    	Criteria criteriaTripInfo = tskSession.createCriteria(T_TripInfo.class);
    	criteriaTripInfo.add(Restrictions.eq("userid", userid));
    	criteriaTripInfo.add(Restrictions.eq("trip_id", trip_id));
    	ProjectionList filterProjList = Projections.projectionList();
    	criteriaTripInfo.setProjection(addFilterList(filterProjList, Integer.parseInt("01111101110011100",2)));
    	criteriaTripInfo.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	
		try {
			Map tripInfoRec = (Map) criteriaTripInfo.uniqueResult();
			if (tripInfoRec == null) {				
				return null;
			}//fi
			tripInfoRec.put("username", PlashUtils.getUsername(userid, tskSession));
			tripInfoRec.put("hash", PlashUtils.ParamToHash(userid, trip_id, tskSession));
			return tripInfoRec;
											
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
    	if ((field_mask & 65536) != 0) { 
    		filterProjList.add(Projections.property("trip_id"),"trip_id");
    	}//fi    	 
    	if ((field_mask & 32768) != 0) { //32768 = 1000000000000000
    		filterProjList.add(Projections.property("trip_name"),"trip_name");
    	}//fi
    	if ((field_mask & 16384) != 0) { //16384 = 100000000000000
        	filterProjList.add(Projections.sqlProjection("trip_st", new String[] {"trip_st"}, new Type[] { new StringType() }));
    	}//fi
    	if ((field_mask & 8192) != 0) { 
        	filterProjList.add(Projections.sqlProjection("trip_et", new String[] {"trip_et"}, new Type[] { new StringType() }));    	
    	}//fi
    	if ((field_mask & 4096) != 0) { //4096 = 1000000000000
        	filterProjList.add(Projections.property("trip_length"),"trip_length");  
    	}//fi
    	if ((field_mask & 2048) != 0) { //1024 = 10000000000
        	filterProjList.add(Projections.property("num_of_pts"),"num_of_pts");
    	}//fi
    	if ((field_mask & 1024) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt1"),"st_addr_prt1");
    	}//fi
    	if ((field_mask & 512) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt2"),"st_addr_prt2");
    	}//fi
    	if ((field_mask & 256) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt3"),"st_addr_prt3");
    	}//fi
    	if ((field_mask & 128) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt4"),"st_addr_prt4");
    	}//fi
    	if ((field_mask & 64) != 0) { 
    		filterProjList.add(Projections.property("st_addr_prt5"),"st_addr_prt5");
    	}//fi
    	if ((field_mask & 32) != 0) { //32 = 100000
    		filterProjList.add(Projections.property("et_addr_prt1"),"et_addr_prt1");
    	}//fi
    	if ((field_mask & 16) != 0) { 
    		filterProjList.add(Projections.property("et_addr_prt2"),"et_addr_prt2");
    	}//fi
    	if ((field_mask & 8) != 0) { 
    		filterProjList.add(Projections.property("et_addr_prt3"),"et_addr_prt3");
    	}//fi
    	if ((field_mask & 4) != 0) { 
    		filterProjList.add(Projections.property("et_addr_prt4"),"et_addr_prt4");
    	}//fi
    	if ((field_mask & 2) != 0) { //2 = 10
    		filterProjList.add(Projections.property("et_addr_prt5"),"et_addr_prt5");
    	}//fi
    	if ((field_mask & 1) != 0) { //1=1
    		filterProjList.add(Projections.property("is_completed"),"is_completed");
    	}//fi    	
		return filterProjList;		
	}//end method
	
}//end class
