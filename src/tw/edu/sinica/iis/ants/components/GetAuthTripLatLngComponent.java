package tw.edu.sinica.iis.ants.components;


import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
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
 * @example	http://localhost:1234/in?userid=7&friendid=1       
 * @note	Follow the algorithm implemented in the original server
 */


public class GetAuthTripLatLngComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }//end method

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }//end methodT_FriendAuth

    public GetAuthTripLatLngComponent() {

    }//end constructor

    public Object greet(Map map) {
    	
    	
        System.out.println("getAuthTripLatLngComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());


        Session session = sessionFactory.openSession(); 
        Criteria criteria;
        criteria = session.createCriteria(T_FriendAuth.class);
        
		try {			
			criteria.add(Restrictions.eq("userAID", Integer.parseInt(map.get("friendid").toString())));
			criteria.add(Restrictions.eq("userBID", Integer.parseInt(map.get("userid").toString())));
			criteria.addOrder(Order.asc("tripID"));
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("getAuthTripLatLng",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthTripLatLngComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("getAuthTripLatLng",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthTripLatLngComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		
		Iterator tripList, tripIDList = criteria.list().iterator();
		
		List<Map> resultList = new ArrayList();
		T_UserPointLocationTime resultEntry;
		Map resultEntryMap;

		while(tripIDList.hasNext()) {			
			
		    criteria = session.createCriteria(T_UserPointLocationTime.class);
		    criteria.add(Restrictions.eq("trip_id", ((T_FriendAuth)tripIDList.next()).getTripID()));
			criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("friendid").toString())));
			criteria.addOrder(Order.asc("timestamp"));
			tripList = criteria.list().iterator();
			if (tripList.hasNext()) {			
				resultEntry= (T_UserPointLocationTime)tripList.next();
				resultEntryMap = new HashMap();
				resultEntryMap.put("tripID", resultEntry.getTrip_id());				
				resultEntryMap.put("timestamp", resultEntry.getTimestamp().toString());
				resultEntryMap.put("lng",((Geometry)resultEntry.getGps()).getCoordinate().x*1000000);				
				resultEntryMap.put("lat",((Geometry)resultEntry.getGps()).getCoordinate().y*1000000);
				
				resultList.add(resultEntryMap);
			}//end while
		}//end while
        map.put("getAuthTripLatLng", resultList);
        session.close();
        System.out.println("getAuthTripLatLngComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    }//end method
}//end class
