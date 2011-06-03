package tw.edu.sinica.iis.ants.components;


import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;

/**
 * Get a list of trips belonging to a user and a friend of him/her
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param   a map that contains the following keys userid,friendid
 * @return  a map containing a list of trip IDs. empty list if no ID found
 * @example	http://localhost:1234/in?userid=7&friendid=1       
 * @note	Follow the algorithm implemented in the original server
 */
public class GetAuthTripComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public GetAuthTripComponent() {

    }

    public Object greet(Map map) {
    	
    	
        System.out.println("getAuthTripComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());


        Session session = sessionFactory.openSession(); 
        Criteria criteria = session.createCriteria(T_FriendAuth.class);

        
		try {			
			criteria.add(Restrictions.eq("userAID", Integer.parseInt(map.get("friendid").toString())));
			criteria.add(Restrictions.eq("userBID", Integer.parseInt(map.get("userid").toString())));
			criteria.setProjection(Projections.property("tripID"));
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("getAuthTrip",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthTripComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("getAuthTrip",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthTripComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch

		map.put("getAuthTrip", criteria.list());
		       
        System.out.println("getAuthTripComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;

	} //close Object greet
    
}  //close class inputGpsLocationComponent
