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
         * get location of the latest trip
         * @author    Angus Fuming Huang
         * @version   1.0, 01/22/2011
         * @param     userid, trip_id 
         * @return    labels_LatestTrip 
         * @see       connpost.java
         */
        Session session = sessionFactory.openSession();
        Integer userid = null;
        Integer trip_id = null;
        
        if (map.containsKey("userid")) {userid = Integer.parseInt(map.get("userid").toString());}
        if (map.containsKey("trip_id")) {trip_id = Integer.parseInt(map.get("trip_id").toString());}
                
        if (userid.equals("") || trip_id.equals("")) {
			map.put("message", "userid or trip_id is empty and can not get the latest trip");
		} else {   
			    int latestTripId = 0;
			    
			    //建立Hibernate的查詢物件，並將session指定到T_UserPointLocationTime.class
			    Criteria criteria = session.createCriteria(T_UserPointLocationTime.class); 
			    
			    //取出符合使用者userid的資料紀錄
			    criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString()))); 
			
			    //將trip_id最大的那筆紀錄取出
			    criteria.setProjection(Projections.max("trip_id"));
			  
			    //將查詢結果存在latestTrip
			    Iterator latestTrip = criteria.list().iterator();
			    
			    //宣告一個HashMap型態的變數labels_LatestTrip, 用來存放trip詳細資料
			    Map labels_LatestTrip = new HashMap();
			    
			    // ?????????????????????????????????????????????????????????????????這邊的資料有待釐清
			    while(latestTrip.hasNext()) {
				      Object [] oneResult = (Object []) latestTrip.next();
				      int id = (Integer) oneResult[2];
				      String gps = oneResult[0].toString();
				      String lng = gps.substring(gps.indexOf('(')+1, gps.indexOf(' '));
				      String lat = gps.substring(gps.indexOf(' ')+1, gps.indexOf(')'));	
				      labels_LatestTrip.put(id+"", lat+";"+lng);
			     }
			 //將結果存到 map 裡面的 labels_LatestTrip 項目
			 map.put("labels_LatestTrip", labels_LatestTrip); 
		   }
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("getLatestTripComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    } //close Object greet
} //close class getLatestTripComponent

