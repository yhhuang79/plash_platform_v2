package tw.edu.sinica.iis.ants.components;

import java.sql.*;
import java.util.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.sendMail;
import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

/**
 * set authorized friend
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param   a map that contains the following keys: userid,friendid, tripID
 * @return  map containing result status
 * @example	http://localhost:1234/in?userid=1&friendid=7&tripid=555       
 * @note	Follow the algorithm implemented in the original server
 */
public class SetAuthFriendComponent extends PLASHComponent {



	private Session tskSession; //task session



	public Object serviceMain(Map map) {
    	
    	
        System.out.println("setAuthFriendComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
 

        tskSession = sessionFactory.openSession(); 
        Criteria criteria = tskSession.createCriteria(T_FriendAuth.class);
        
		try {			
			criteria.add(Restrictions.eq("userAID", Integer.parseInt(map.get("userid").toString())));
			criteria.add(Restrictions.eq("userBID", Integer.parseInt(map.get("friendid").toString())));	
			criteria.add(Restrictions.eq("tripID", Integer.parseInt(map.get("tripid").toString())));			
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("setAuthTrip",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        map.put("message", "Authenticate failed!");
			System.out.println("setAuthFriendComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("setAuthTrip",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
			map.put("message", "Authenticate failed!");
			System.out.println("setAuthFriendComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		
		if (criteria.list().iterator().hasNext()) { //element exists
			map.put("setAuthTrip",false); //result flag, flag name to be unified
	        //need to pass an information
			map.put("message", "duplicate");
			//Danny: 4/26 work until here
			System.out.println("setAuthFriendComponent failure end3:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;			
		} else {
			T_FriendAuth entry = new T_FriendAuth();
			
			entry.setUserAID(Integer.parseInt(map.get("userid").toString()));
			entry.setUserBID(Integer.parseInt(map.get("friendid").toString()));
			entry.setTripID(Integer.parseInt(map.get("tripid").toString()));						
			tskSession.save(entry);
			tskSession.beginTransaction().commit();

			//put code for sending e-mail here
			//Suggestion: the process of sending e-mail should be initiated by router rather than this component
			
			map.put("setAuthFriend", true);
			map.put("message", "success");
	        System.out.println("setAuthFriendComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
	        return map;			
		}//end if



	} //close Object greet

    
}  //close class inputGpsLocationComponent
