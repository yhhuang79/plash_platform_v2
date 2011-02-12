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
 * @param   userid,friendid
 * @return  map containing a list of trip IDs. empty list if no ID found
 * @example	http://localhost:1234/in?userid=7&friendid=1       
 * @note	Follow the algorithm implemented in the original server
 */
public class getAuthTripComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public getAuthTripComponent() {

    }

    public Object greet(Map map) {
    	
    	
        System.out.println("getAuthTripComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());




        Session session = sessionFactory.openSession(); 
        Criteria criteria = session.createCriteria(T_FriendAuth.class);
        
		try {			
			criteria.add(Restrictions.eq("userAID", Integer.parseInt(map.get("friendid").toString())));
			criteria.add(Restrictions.eq("userBID", Integer.parseInt(map.get("userid").toString())));	
   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("getAuthTrip",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthTripComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		
		Iterator fls = criteria.list().iterator();
		List resultList = new ArrayList();

		while(fls.hasNext()) {
			resultList.add(((T_FriendAuth)fls.next()).getTripID());			
		}//end while
        map.put("getAuthTrip", resultList);
        
        System.out.println("getAuthTripComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;

	} //close Object greet
    
}  //close class inputGpsLocationComponent
