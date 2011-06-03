package tw.edu.sinica.iis.ants.components;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;

public class GetAllFriendLocationComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public GetAllFriendLocationComponent() {

	}

	public Object greet(Map map) {
		System.out.println("PeopleLocations Start:\t"+ Calendar.getInstance().getTimeInMillis());
		Session session = sessionFactory.openSession(); 

		
		
		String useridSet = (String) map.get("friend_list");
		
		String[] useridArray = useridSet.split(",");
		for(int i=0; i<useridArray.length; i++){
			if(!useridArray[i].trim().matches("[0-9]+")){
				return map;
			}
		}
		
		/*
		List friend_list = (List) map.get("friend_list");
		Iterator i = friend_list.iterator();
		String useridSet = "";
		while(i.hasNext()){
			useridSet += i.next().toString()+",";
		}
		useridSet = "(" + useridSet.substring(0, useridSet.length()-1) + ")";
		*/
		
		String sql = "select ST_AsText(gps) as gpsLoc, timestamp, userid from user_location.user_point_location_time uplt where id in(select   max(id) from user_location.user_point_location_time where (trip_id, userid) in (select  max(trip_id) as trip_id,userid from user_location.user_point_location_time where userid in "+useridSet+" group by userid ) group by trip_id, userid);";
		// 建立 SQLQuery
		SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("gpsLoc", Hibernate.STRING).addScalar("timestamp", Hibernate.TIMESTAMP).addScalar("userid",Hibernate.INTEGER);
		// 將別名user與實體類User關聯在一起
		//sqlQuery.addEntity("uplt", T_UserPointLocationTime.class);
		Iterator iterator = sqlQuery.list().iterator();
		Map locations = new HashMap();
		while(iterator.hasNext()) {
			Object [] oneResult = (Object []) iterator.next();
			int id = (Integer) oneResult[2];
			String gps = oneResult[0].toString();
			String lng = gps.substring(gps.indexOf('(')+1, gps.indexOf(' '));
			String lat = gps.substring(gps.indexOf(' ')+1, gps.indexOf(')'));	
			locations.put(id+"", lat+";"+lng);
		}
		map.put("locations", locations);
		session.close();
		System.out.println("PeopleLocations End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
