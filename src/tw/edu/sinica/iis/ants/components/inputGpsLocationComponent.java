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
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Transaction;

import com.vividsolutions.jts.geom.Geometry;

import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;


public class inputGpsLocationComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public inputGpsLocationComponent() {

    }

    public Object greet(Map map) {
    	
        System.out.println("inputGpsLocationComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here
        /**
         * @goal      input gps location
         * @author    Angus Fuming Huang
         * @version   1.0, 01/23/2011
         * @param     userid, trip_id, gps, timestamp
         * @return    message 
         * @see       connpost.java
         * @example   http://localhost:1234/in?userid=1&trip_id=500&timestamp=2011-11-11 11:11:11.111
         * 
         * @trouble   輸入參數中的gps需再確認 ; label的控制需再確認
         */
        
        Session session = sessionFactory.openSession();
        Integer userid = null;
        Integer trip_id = null;
        //Geometry gps = null;
        Timestamp timestamp = null;
        
        if (map.containsKey("userid"))    {userid = Integer.parseInt(map.get("userid").toString());}
        if (map.containsKey("trip_id"))   {trip_id = Integer.parseInt(map.get("trip_id").toString());}
        //if (map.containsKey("gps"))       {gps = map.get("gps");}
        if (map.containsKey("timestamp")) {timestamp = Timestamp.valueOf(map.get("timestamp").toString());}
        
        //if (userid.equals("")||trip_id.equals("")||timestamp.equals("")||gps.equals("")) {  //完整的判斷式
        if (userid.equals("")||trip_id.equals("")||timestamp.equals("")) {  //簡化的判斷式
			map.put("message", "required information is empty and can not input the GPS location");
		} else {   
				int label = 0; //let old version can work as well, 0 means no mentioned
				if(map.get("label") != null)
				    label = Integer.parseInt(map.get(label).toString());
				
				//id值不存入，由資料庫主動遞增                                                                                            
				
				//將userid存入資料庫
				T_UserPointLocationTime user = new T_UserPointLocationTime();
				user.setUserid(userid);
				
				//將trip_id存入資料庫
				user.setTrip_id(trip_id);
				
				//將timestamp存入資料庫
				user.setTimestamp(timestamp);
				
				//將 gps 存入資料庫
				//user.setGps(gps);
				
				//取得server_timestamp，並將其存入資料庫
				user.setServer_timestamp(new Timestamp(new Date().getTime()));
				
				//將label存入資料庫
				user.setLabel(label);
								
				Transaction tx = session.beginTransaction();
				session.save(user);
				tx.commit();
				
				map.put("message", "Success in GPS location input"); 
			}
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("inputGpsLocationComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	} //close Object greet
    
}  //close class inputGpsLocationComponent


