package tw.edu.sinica.iis.ants.components;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Transaction;

import com.vividsolutions.jts.geom.Geometry;

import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;


public class getLatestTripComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public getLatestTripComponent() {

    }

    public Object greet(Map map) {
    	
        System.out.println("getLatestTripComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here
        /**
         * @goal      get location of the latest trip
         * @author    Angus Fuming Huang
         * @version   1.0, 01/22/2011
         * @param     userid, trip_id
         * @return    latestTrip (id + label + gps)
         * @see       connpost.java
         * @example   http://localhost:1234/in?userid=1&trip_id=494
         */
        Session session = sessionFactory.openSession();
        Integer userid = null;
        Integer trip_id = null;
        
        if (map.containsKey("userid")) {userid = Integer.parseInt(map.get("userid").toString());}
        if (map.containsKey("trip_id")) {trip_id = Integer.parseInt(map.get("trip_id").toString());}
                
        if (userid.equals("") || trip_id.equals("")) {
			map.put("message", "userid or trip_id is empty and can not get the latest trip");
		} else {   
			    String latestTrip = null;
			    
			    //build the Hibernate query object and assign the session to the T_UserPointLocationTime.class
			    Criteria criteria = session.createCriteria(T_UserPointLocationTime.class); 
			    
			    //get the record matching the <userid> & <trip_id>
			    criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString()))); 
			    criteria.add(Restrictions.eq("trip_id", Integer.parseInt(map.get("trip_id").toString())));  
			    
			    //sort the <id> from big to small, to get the record having the biggest <id>
			    criteria.addOrder(Order.desc("id"));
			  
			    //store the query results into the <tripids>
			    Iterator tripids = criteria.list().iterator();
			    
			    //Danny: Incorrect result, it only get the latest GPS point not trip (list of points).  
//			    T_UserPointLocationTime latestTripId = (T_UserPointLocationTime) tripids.next();
//			    /store the <id>,<label>,<gps> of the final query result into the <latestTrip> variable
//			    latestTrip += "id=" + latestTripId.getId() + ";Label=" + latestTripId.getLabel()+ ";GPS=" + latestTripId.getGps();
//			    
//			    //store the query results into the <tripids>
//			    map.put("latestTrip", latestTrip);
			    
			    //--------------------------------------------------//
			    //Danny
			    
			    List<Map> resultList = new ArrayList();
			    Map resultEntryMap;
				while (tripids.hasNext()){
			    	
			    	
				    T_UserPointLocationTime latestTripId = (T_UserPointLocationTime) tripids.next();
				    resultEntryMap = new HashMap();
				    resultEntryMap.put("id", latestTripId.getId());
				    resultEntryMap.put("lng", ((Geometry)latestTripId.getGps()).getCoordinate().x*1000000);
				    resultEntryMap.put("lat", ((Geometry)latestTripId.getGps()).getCoordinate().y*1000000);
				    
				    if (Integer.toString(latestTripId.getLabel())!=null){
				    	resultEntryMap.put("label", latestTripId.getLabel());
				    }
				    else {
				    	resultEntryMap.put("label", -1);
				    }
				    
				    resultList.add(resultEntryMap);
				}
				
			    
//			    if (tripList.hasNext()) {			
//					resultEntry= (T_UserPointLocationTime)tripList.next();
//					resultEntryMap = new HashMap();
//					resultEntryMap.put("tripID", resultEntry.getTrip_id());				
//					resultEntryMap.put("timestamp", resultEntry.getTimestamp().toString());
//					resultEntryMap.put("lng",((Geometry)resultEntry.getGps()).getCoordinate().x*1000000);				
//					resultEntryMap.put("lat",((Geometry)resultEntry.getGps()).getCoordinate().y*1000000);
//					
//					resultList.add(resultEntryMap);
//				}//end while
			    
			    //store the result into the <latestTrip> item of the map
			    map.put("latestTrip", resultList); 
		   }
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("getLatestTripComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    } //close Object greet
    
} //close class getLatestTripComponent


