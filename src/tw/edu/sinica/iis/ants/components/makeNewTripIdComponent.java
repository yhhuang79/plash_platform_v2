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


public class makeNewTripIdComponent {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public makeNewTripIdComponent() {

    }

    public Object greet(Map map) {
        System.out.println("makeNewTripIdComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here
        /**
         * make trip id increase and return the value
         * @author    Angus Fuming Huang
         * @version   1.0, 01/24/2011
         * @param     userid 
         * @return    newTripId
         * @see       connpost.java
         */
        Session session = sessionFactory.openSession();
        Integer userid = null;
        
        //取得userid值
        if (map.containsKey("userid")) {userid = Integer.parseInt(map.get("userid").toString());}
        
        if (userid.equals("")) {
			map.put("message", "userid is empty and can not make new trip_id");
		} else {   
				int newTripId = 0;
				
				//建立Hibernate的查詢物件，並將session指定到T_UserPointLocationTime.class
				Criteria criteria = session.createCriteria(T_UserPointLocationTime.class); 
				
				//取出符合使用者userid的資料紀錄
				criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString()))); 
				
				//將trip_id最大的那筆紀錄取出
				criteria.setProjection(Projections.max("trip_id"));
				
				//將查詢結果存在tripids
				Iterator tripids = criteria.list().iterator();
				
				//將 tripids 資料的值存到以 T_UserPointLocationTime 為類別的 tripid 物件中
				T_UserPointLocationTime tripid = (T_UserPointLocationTime) tripids.next();
				
				//利用 tripid 的 getTrip_id()方法來取出 trip_id 值, 並且+1
				newTripId = tripid.getTrip_id() + 1;
				
				//將結果存到 map 裡面的 newTripId 項目
				map.put("newTripId", newTripId);
			}
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("makeNewTripIdComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	} //close Object greet
} //close class makeNewTripIdComponent



