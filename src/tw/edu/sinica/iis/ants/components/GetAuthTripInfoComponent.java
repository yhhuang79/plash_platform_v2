package tw.edu.sinica.iis.ants.components;


import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get a list of trip info belonging to a user and a friend of him/her
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param   a map that contains the following keys userid,friendid
 * @return  a map containing a list of maps where each map contains
 * 			a trip ID, a latitude, a longitude and a timestamp. 
 * 			empty list if no matching entry found
 * @example	https://localhost:1234/in?userid=7&friendid=1       
 * @note	
 */


public class GetAuthTripInfoComponent extends PLASHComponent{


	private Session tskSession; //task session
	
	@Override
	public Object serviceMain(Map map) {
		
		tskSession = sessionFactory.openSession();    	

    	int friendid;
        System.out.println("getAuthTripLatLngComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        Session session = sessionFactory.openSession(); 
        
        
        Criteria criteriaFriendAuth;
        criteriaFriendAuth = session.createCriteria(T_FriendAuth.class);
        
		try {			
			friendid = Integer.parseInt(map.get("friendid").toString());
			
			criteriaFriendAuth.add(Restrictions.eq("userAID", Integer.parseInt(map.get("friendid").toString())));
			criteriaFriendAuth.add(Restrictions.eq("userBID", Integer.parseInt(map.get("userid").toString())));
			criteriaFriendAuth.addOrder(Order.desc("tripID"));
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("GetAuthTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthTripLatLngComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("GetAuthTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("GetAuthTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		
		//Iterator<T_FriendAuth> tripIDList = criteriaFriendAuth.list().iterator();
		
		
		ArrayList<T_FriendAuth> tripIDList = (ArrayList<T_FriendAuth>) criteriaFriendAuth.list();
		//Iterator<T_FriendAuth> tripIDListItr = tripIDList.iterator();		
		List<Map> resultList = new ArrayList();
		Map resultEntryMap;


		for(int i = tripIDList.size()-1;  i >0 ;i--) {

			
			resultEntryMap = getSingleTripInfo(friendid,tripIDList.get(i).getTripID());
			if (resultEntryMap != null) {
				resultList.add(resultEntryMap);
			}//fi			
			
		}//rof
		/*
		while(tripIDListItr.hasNext()) {
			tmp = tripIDListItr.next();
			System.out.println(" er... " + (tmp == null) +  " : " + tmp.getClass());
			int tmptid = tmp.getTripID();

			
			resultEntryMap = getSingleTripInfo(friendid,tmptid);
			if (resultEntryMap != null) {
				resultList.add(resultEntryMap);
			}//fi
							
		}//end while /*/
		
        map.put("authTripInfoList", resultList);
        session.close();
        System.out.println("GetAuthTripInfoComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
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
    	//ProjectionList filterProjList = Projections.projectionList();      	
    	//criteriaTripInfo.setProjection(filterProjList);    	
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

	
}//end class
