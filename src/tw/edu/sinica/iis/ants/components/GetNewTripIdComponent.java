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

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;
import tw.edu.sinica.iis.ants.DB.T_UserTrip;


public class GetNewTripIdComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public GetNewTripIdComponent() {

    }

    public Object greet(Map map) {
    	
        System.out.println("makeNewTripIdComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here
        /**
         * @goal      make trip id increase and return the value
         * @author    Danny - Yao Hua Ho
         * @version   1.1, 06/21/2011
         * @param     userid 
         * @return    newTripId
         * @see       connpost.java
         * @example   http://localhost:1234/in?userid=1
         */
        Session session = sessionFactory.openSession();
        Integer userid = null;
        
        //get the value of <userid>
        if (map.containsKey("userid")) {
        	userid = Integer.parseInt(map.get("userid").toString());
        	System.out.println("Start - userid = "+userid);
        }
        
        if (userid.equals("")) {
			map.put("message", "userid is empty and can not make new trip_id");
		} else {   
				int newTripId = 0;
				
				/*
				//----------------------------------------------------------------------------------------//
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
				
				//get the value of <trip_id> by using the getTrip_id() method of tripid, and add 1
				newTripId = tripid.getTrip_id() + 1;
				
				//store the result into the <newTripId> item of the map
				map.put("newTripId", newTripId);
				
				//----------------------------------------------------------------------------------------//
				*/
				
				/*
				
				//Sync all the trip ids (6/21/2011)
				Criteria criteria = session.createCriteria(T_UserTrip.class);
				//int current_id = 1;
				//criteria.add(Restrictions.eq("id", current_id));
				Iterator ids = criteria.list().iterator();
				while (ids.hasNext()){
					int current_userid;
					T_UserTrip id = (T_UserTrip) ids.next();
					current_userid = id.getUserid();
					int temp_TripId = id.getTrip_count();
					//System.out.println("current_userid = "+current_userid);
					
					Criteria criteria2 = session.createCriteria(T_UserPointLocationTime.class);
					criteria2.add(Restrictions.eq("userid", current_userid)); 
					criteria2.addOrder(Order.desc("trip_id"));
					Iterator tripids = criteria2.list().iterator();
					if (tripids.hasNext()){
						T_UserPointLocationTime tripid = (T_UserPointLocationTime) tripids.next();
						newTripId = tripid.getTrip_id() + 1;
						if (newTripId < temp_TripId){
							newTripId = temp_TripId;
						}
						id.setTrip_count(newTripId);
						//System.out.println("newTripId = "+newTripId);
						
						System.out.println("Danny - Sync ids:\t current_userid = "+current_userid+"\t newTripId = "+newTripId);
					}
			        Transaction tx = session.beginTransaction();
			        session.save(id);
			        tx.commit();
				}
				*/
				//----------------------------------------------------------------------------------------//
				
				//build the Hibernate query object and assign the session to the T_UserTrip.class
				Criteria criteria = session.createCriteria(T_UserTrip.class); 
				
				//get the record matching the <userid>
				criteria.add(Restrictions.eq("userid", userid));
				
				//store the query results into the <tripids>
				Iterator tripids = criteria.list().iterator();
				
				if (tripids.hasNext()){
					//store the value of <tripids> into the <tripid> object belonging to the T_UserTrip class
					T_UserTrip tripid = (T_UserTrip) tripids.next();
				
					//get the value of <trip_id> by using the getTrip_id() method of tripid, and add 1
					newTripId = tripid.getTrip_count() + 1;
					
					//update new <tripid> into T_UserTrip class
					tripid.setTrip_count(newTripId);
					
					//begin Transaction and commit to DB
			        Transaction tx = session.beginTransaction();
			        session.save(tripid);
			        tx.commit();
					
				}
				else {
					//for new user without any recorded trip
					newTripId = 1;
					criteria = session.createCriteria(T_FriendList.class);
	        		T_UserTrip tripid1 = new T_UserTrip();
					//tripid.setId(id)
					tripid1.setUserid(userid);
					tripid1.setTrip_count(newTripId);
					
					//begin Transaction and commit to DB
			        Transaction tx = session.beginTransaction();
			        session.save(tripid1);
			        tx.commit();
				}
			
				
				

				
				//store the result into the <newTripId> item of the map
				map.put("newTripId", newTripId);
				
			}

        session.close();
        //End of Programming Logic Implementation
        System.out.println("makeNewTripIdComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	} //close Object greet
    
} //close class makeNewTripIdComponent




