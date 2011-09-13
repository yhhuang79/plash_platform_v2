package tw.edu.sinica.iis.ants.components;


import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;

import com.vividsolutions.jts.geom.Geometry;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Get a list of trip info of a user
 * 
 * @author	
 * @version 
 * @param   
 * @return  
 * @example	 
 * @note	
 */


public class CopyOfGetUserTripLatLngComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }//end method

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }//end methodT_FriendAuth

    public CopyOfGetUserTripLatLngComponent() {

    }//end constructor

    public Object greet(Map map) {
    	
    	
        System.out.println("GetUserTripLatLngComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());


		Session session = sessionFactory.openSession();
        
        Integer userid = null;
        Integer tripCount = -1 ;
        
        if (map.containsKey("userid")) {
			userid = Integer.parseInt(map.get("userid").toString());
		}
        if (map.containsKey("tripCount")){
        	tripCount = Integer.parseInt(map.get("tripCount").toString());
        }
        
        if (userid.equals("")) {
			map.put("message", "userid is empty");
		} else {   
			System.out.println("!!!!!!!!!!!!!"+userid);
			Criteria criteria = session.createCriteria(T_UserPointLocationTime.class);
			criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString())));
			criteria.addOrder(Order.desc("trip_id"));

			criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("trip_id"))));

			Iterator tripList, tripIDList = criteria.list().iterator();
			
			List<Map> resultList = new ArrayList();
			T_UserPointLocationTime resultEntry;
			Map resultEntryMap;
			int count = 0;
			while(tripIDList.hasNext()) {			
				
			    criteria = session.createCriteria(T_UserPointLocationTime.class);
			    criteria.add(Restrictions.eq("trip_id", (Integer)tripIDList.next()));
				criteria.add(Restrictions.eq("userid", userid));
				criteria.addOrder(Order.asc("timestamp"));
				tripList = criteria.list().iterator();
				if (tripList.hasNext()) {
					
					if ((count >= tripCount-10 && count < tripCount)||tripCount==-1){
						resultEntry= (T_UserPointLocationTime)tripList.next();
						resultEntryMap = new HashMap();
						resultEntryMap.put("tripID", resultEntry.getTrip_id());				
						resultEntryMap.put("timestamp", resultEntry.getTimestamp().toString());
						resultEntryMap.put("lng",((Geometry)resultEntry.getGps()).getCoordinate().x*1000000);				
						resultEntryMap.put("lat",((Geometry)resultEntry.getGps()).getCoordinate().y*1000000);
						resultList.add(resultEntryMap);
						
					}
					count++;
					if (count == tripCount)
						break;
					

					
//					if(count==10)
//						break;
				}//end if
			}//end while
	        map.put("GetUserTripLatLng", resultList);
	        
		}

        session.close();
       
		
        
        
        System.out.println("GetUserTripLatLngComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    }//end method
}//end class