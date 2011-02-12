package tw.edu.sinica.iis.ants.components;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement;

import java.util.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;
import tw.edu.sinica.iis.ants.DB.T_FriendAuth;

/**
 * TBA
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param    userid,friendid,tripid
 * @return   operation result
 * @example	http://localhost:1234/in?userid=1&tripid=55555&friendid=55       
 * @note	Follow the algorithm implemented in the original server
 */

public class delAuthFriendComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public delAuthFriendComponent() {

    }

    public Object greet(Map map) {
    	
        System.out.println("delAuthFriendComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        
 
        
        Session session = sessionFactory.openSession(); 
        Criteria criteria = session.createCriteria(T_FriendAuth.class);
		       
        
		try {
			criteria.add(Restrictions.eq("userAID", Integer.parseInt(map.get("userid").toString())));
			criteria.add(Restrictions.eq("tripID", Integer.parseInt(map.get("tripid").toString())));	
			criteria.add(Restrictions.eq("userBID", Integer.parseInt(map.get("friendid").toString())));		

			Iterator fls = criteria.list().iterator();
			Transaction ts = session.beginTransaction();
			session.delete((T_FriendAuth)fls.next());
			ts.commit(); //*/        
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("delAuthFriend",false); //result flag, flag name to be unified
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("delAuthFriendComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NoSuchElementException e) { //Element not found
			map.put("delAuthFriend",false); //result flag, flag name to be unified
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("delAuthFriendComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;			
		}//end try catch              

		

			map.put("delAuthFriend",true); 
	        System.out.println("delAuthFriendComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
	        return map;
			
       

	} //close Object greet
    
}  //close class inputGpsLocationComponent
