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

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class NearestComponent {


    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public NearestComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Nearest Start:\t"+ Calendar.getInstance().getTimeInMillis());
		
		Session session = sessionFactory.openSession();
		String lng = "";
		String lat = "";	
		String sql = "select ST_AsText(gps) as gpsLoc from user_location.user_point_location_time where userid="+Integer.parseInt(map.get("sid").toString())+" order by timestamp desc limit 1";
		SQLQuery sqlQuery = session.createSQLQuery(sql).addScalar("gpsLoc", Hibernate.STRING);
		String gpspoint = "";
		Object result = sqlQuery.uniqueResult();
		gpspoint = result.toString();
		lng = gpspoint.substring(gpspoint.indexOf('(')+1, gpspoint.indexOf(' '));
		lat = gpspoint.substring(gpspoint.indexOf(' ')+1, gpspoint.indexOf(')'));	
		session.close();
		
		
		

		Map locations = (Map) map.remove("locations");
		double dis = Double.MAX_VALUE;
		int userid=-1;
		String lat_ans = "";
		String lng_ans = "";
		for(Object key : locations.keySet()){
			String[] gps = locations.get(key).toString().split(";");
			String lat2 = gps[0];
			String lng2 = gps[1];
			if(dis> (Math.pow((Double.parseDouble(lat2)-Double.parseDouble(lat)),2)+ Math.pow((Double.parseDouble(lng2)-Double.parseDouble(lng)),2))){
				dis = (Math.pow((Double.parseDouble(lat2)-Double.parseDouble(lat)),2)+ Math.pow((Double.parseDouble(lng2)-Double.parseDouble(lng)),2));
				userid = Integer.parseInt(key.toString());
				lat_ans = lat2;
				lng_ans = lng2;
			}
		}
		Map nearest = new HashMap();
		nearest.put("userid", userid);
		nearest.put("lat", lat_ans);
		nearest.put("lng", lng_ans);
		nearest.put("dis",dis);
		map.put("nearest", nearest);
		

		System.out.println("Nearest End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
		
		
		
	}
}
