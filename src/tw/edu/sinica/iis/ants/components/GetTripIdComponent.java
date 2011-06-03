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



public class GetTripIdComponent {

	  private SessionFactory sessionFactory;

	    public SessionFactory getSessionFactory() {
	        return sessionFactory;
	    }

	    public void setSessionFactory(SessionFactory sessionFactory) {
	        this.sessionFactory = sessionFactory;
	    }

	    public GetTripIdComponent() {

	    }

	    public Object greet(Map map) {
	    	
	        System.out.println("getCurrentTripIdComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
	        //Please Implement Your Programming Logic From Here 
	        /**
	         * @goal      return current trip id
	         * @author    Angus Fuming Huang
	         * @version   1.0, 01/21/2011
	         * @param     userid 
	         * @return    currentTripId
	         * @see       connpost.java
	         * @example   http://localhost:1234/in?userid=1
	         */

			Session session = sessionFactory.openSession();
	        
	        Integer userid = null;
	        
	        if (map.containsKey("userid")) {userid = Integer.parseInt(map.get("userid").toString());}
	        
	        if (userid.equals("")) {
				map.put("message", "userid is empty and can not get the current tripid");
			} else {   
				Integer currentTripId = null;
				    
				    //build the Hibernate query object and assign the session to the T_UserPointLocationTime.class
				    Criteria criteria = session.createCriteria(T_UserPointLocationTime.class); 
				    
	 			    //get the record matching the <userid>
				    criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString())));
				    
				    //sort the <trip_id> from big to small, to get the record having the biggest <trip_id>
				    criteria.addOrder(Order.desc("trip_id"));
				    //criteria.setProjection(Projections.max("trip_id"));
				    
					//store the query results into the <tripids>
					Iterator tripids = criteria.list().iterator();
					
					//store the value of <tripids> into the <tripid> object belonging to the T_UserPointLocationTime class
					T_UserPointLocationTime tripid = (T_UserPointLocationTime) tripids.next();
					
					//get the value of <trip_id> by using the getTrip_id() method of tripid
					currentTripId = tripid.getTrip_id();
					
					//store the result into the <newTripId> item of the map
					map.put("currentTripId", currentTripId);  
				}
	        
	        session.close();
	        //End of Programming Logic Implementation
	        System.out.println("getCurrentTripIdComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
	        return map;
	    } //close Object greet
    
} //close class getCurrentTripIdComponent

