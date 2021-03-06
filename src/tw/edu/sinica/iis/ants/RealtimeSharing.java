package tw.edu.sinica.iis.ants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.transform.Transformers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.db_pojo.antrip.RealtimeSharingCheckins;
import tw.edu.sinica.iis.ants.db_pojo.antrip.RealtimeSharingPoints;
import tw.edu.sinica.iis.ants.db_pojo.antrip.RealtimeSharingSessions;
import tw.edu.sinica.iis.ants.db_pojo.antrip.RealtimeSharingWatcher;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class RealtimeSharing {

	public static boolean checkToken(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingSessions.class);
    	criteria.add(Restrictions.eq("token", token));
    	criteria.add(Restrictions.ne("status", 0));
    	Iterator rsSessionItr = criteria.list().iterator();
    	if(rsSessionItr.hasNext()) {
    		RealtimeSharingSessions rsSession = (RealtimeSharingSessions) rsSessionItr.next();
    		Timestamp timestamp = rsSession.getTimestamp();
    		if (rsSession.getDuration_type() == 1){
    			return true;
    		} else if(rsSession.getDuration_type() == 2) {
    			Integer duration = timestamp.compareTo(new Timestamp(new Date().getTime()));
    			if (duration < rsSession.getDuration_value()){
    				return true;
    			}
    		}
    	}
    	return false;
	}//end method		

	public static int getTokenStatus(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingSessions.class);
    	criteria.add(Restrictions.eq("token", token));
    	int tokenStatus = 0;
    	Iterator rsSessionItr = criteria.list().iterator();
    	if(rsSessionItr.hasNext()) {
    		RealtimeSharingSessions rsSession = (RealtimeSharingSessions) rsSessionItr.next();
			tokenStatus = Integer.parseInt(rsSession.getStatus().toString());  
    	}
    	return tokenStatus;
	}//end method	

	public static Timestamp getTokenLastTimestamp(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingPoints.class);
    	criteria.add(Restrictions.eq("token", token));
    	ProjectionList projectionList = Projections.projectionList();
    	projectionList.add(Projections.property("timestamp"), "timestamp");
    	criteria.setProjection(projectionList);
    	criteria.addOrder(Order.desc("timestamp"));
    	criteria.setMaxResults(1);
    	criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	Timestamp timestamp = null;
    	Iterator rsSessionItr = criteria.list().iterator();
    	if(rsSessionItr.hasNext()) {
    		RealtimeSharingSessions rsSession = (RealtimeSharingSessions) rsSessionItr.next();
    		timestamp = rsSession.getTimestamp();  
    	}
    	return timestamp;
	}//end method	
	
	
	// 1. Input parameters : userid(Integer), timestamp
	public static Map initialSharing(Integer userid, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			String token = PlashUtils.MD5(userid + "sharing" + date.getTime());
			String url = PlashUtils.getShortUrl("http://www.plash.tw/antrack/index.html?token=" + token);
			RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
			rsSession.setUuid(userid.toString());
			rsSession.setToken(token);
			rsSession.setUrl(url);
			rsSession.setStatus(1);
			rsSession.setDuration_type(1);
			rsSession.setDuration_value(0);
			rsSession.setTimestamp(timestamp);
			Transaction tx = session.beginTransaction();
			session.save(rsSession);
			tx.commit();
			message.put("status_code", 200);
			message.put("message", "ok");
			message.put("url", url);
			message.put("token", token);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "Network Error");			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "IO Error");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 500);
			message.put("message", "Internal Server Error");
			e.printStackTrace();
		}		
    	return message;
	}//end method		

	// 2. Input parameters : userid(String), timestamp
	public static Map initialSharing(String uuid, String username, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			if (uuid.length() == 36) {
				String token = PlashUtils.MD5(uuid.substring((int)(Math.random()*10), 16) + date.getTime());
				String url = PlashUtils.getShortUrl("http://www.plash.tw/antrack/index.html?token=" + token);
				RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
				rsSession.setUuid(uuid);
				rsSession.setToken(token);
				rsSession.setUsername(username);
				rsSession.setUrl(url);
				rsSession.setStatus(100);
				rsSession.setDuration_type(1);
				rsSession.setDuration_value(0);				
				rsSession.setTimestamp(timestamp);
				Transaction tx = session.beginTransaction();
				session.save(rsSession);
				tx.commit();
				message.put("status_code", 200);
				message.put("message", "ok");
				message.put("url", url);
				message.put("token", token);
			} else {
				message.put("status_code", 400);
				message.put("message", "Parameter Error");				
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "Network Error");			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "IO Error");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 500);
			message.put("message", "Internal Server Error");
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e){
			message.put("status_code", 400);
			message.put("message", "Parameter Error");
			e.printStackTrace();			
		}
    	return message;
	}//end method		
	public static Map anthomeInitial(String uuid, String username, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			if (uuid.length() == 16) {
				String token = PlashUtils.MD5(uuid.substring((int)(Math.random()*10), 16) + date.getTime());
				String url = PlashUtils.getShortUrl("http://www.plash.tw/antrack/index.html?token=" + token);
				RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
				rsSession.setUuid(uuid);
				rsSession.setToken(token);
				rsSession.setUsername(username);
				rsSession.setUrl(url);
				rsSession.setStatus(100);
				rsSession.setDuration_type(1);
				rsSession.setDuration_value(0);				
				rsSession.setTimestamp(timestamp);
				Transaction tx = session.beginTransaction();
				session.save(rsSession);
				tx.commit();
				message.put("status_code", 200);
				message.put("message", "ok");
				message.put("url", url);
				message.put("token", token);
			} else {
				message.put("status_code", 400);
				message.put("message", "Parameter Error");				
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "Network Error");			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "IO Error");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 500);
			message.put("message", "Internal Server Error");
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e){
			message.put("status_code", 400);
			message.put("message", "Parameter Error");
			e.printStackTrace();			
		}
    	return message;
	}//end method		
	
	
	public static Map startSharing(String token, String sharing_method, Timestamp timestamp, Session session) {
		Map message = new HashMap();
    	Criteria criteria = session.createCriteria(RealtimeSharingSessions.class);
    	criteria.add(Restrictions.eq("token", token));
    	Iterator rsSessionItr = criteria.list().iterator();
    	if(rsSessionItr.hasNext()) {
    		Transaction tx = session.beginTransaction();
    		RealtimeSharingSessions rsSession = (RealtimeSharingSessions) rsSessionItr.next();
    		RealtimeSharingSessions rss = (RealtimeSharingSessions) session.get(RealtimeSharingSessions.class, rsSession.getId());
    		rss.setSharing_method(sharing_method);
    		rss.setTimestamp(timestamp);
    		rss.setStatus(1);
			session.update(rss);
			tx.commit();
			message.put("status_code", 200);
			message.put("message", "start sharing");
			return message;
    	}
		message.put("status_code", 500);
		message.put("message", "Internal Server Error");
    	return message;
	}//end method
	
	// 1. Input parameters : userid, sharing_method, duration_type, timestamp
	public static Map startSharing(Integer userid, String sharing_method, Integer duration_type, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			String token = PlashUtils.MD5(userid + "sharing" + date.getTime());
			String url = PlashUtils.getShortUrl("http://www.plash.tw/shareloc/index.html?token=" + token);
			RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
			rsSession.setUuid(userid.toString());
			rsSession.setToken(token);
			rsSession.setUrl(url);
			rsSession.setSharing_method(sharing_method);
			rsSession.setDuration_type(duration_type);
			rsSession.setTimestamp(timestamp);
			Transaction tx = session.beginTransaction();
			session.save(rsSession);
			tx.commit();
			message.put("status_code", 200);
			message.put("message", "ok");
			message.put("url", url);
			message.put("token", token);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "Network Error");			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "IO Error");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 500);
			message.put("message", "Internal Server Error");
			e.printStackTrace();
		}		
    	return message;
	}//end method		
	
	// 2. Input parameters : userid, sharing_method, duration_type, duration_value, timestamp
	public static Map startSharing(Integer userid, String sharing_method, Integer duration_type, Integer duration_value, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			String token = PlashUtils.MD5(userid + "sharing" + date.getTime());
			String url = PlashUtils.getShortUrl("http://www.plash.tw/shareloc/index.html?token=" + token);
			RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
			rsSession.setUuid(userid.toString());
			rsSession.setToken(token);
			rsSession.setUrl(url);
			rsSession.setSharing_method(sharing_method);
			rsSession.setDuration_type(duration_type);
			rsSession.setDuration_value(duration_value);
			rsSession.setTimestamp(timestamp);
			Transaction tx = session.beginTransaction();
			session.save(rsSession);
			tx.commit();
			message.put("status_code", 200);
			message.put("message", "ok");
			message.put("url", url);
			message.put("token", token);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "Network Error");			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "IO Error");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 500);
			message.put("message", "Internal Server Error");
			e.printStackTrace();
		}		
    	return message;
	}//end method	

	// 3. Input parameters : userid, sharing_method, duration_type, timestamp
	public static Map startSharing(Integer userid, String sharing_method, Integer duration_type, String friend_id, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			String token = PlashUtils.MD5(userid + "sharing" + date.getTime());
			String url = PlashUtils.getShortUrl("http://www.plash.tw/shareloc/index.html?token=" + token);
			RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
			rsSession.setUuid(userid.toString());
			rsSession.setToken(token);
			rsSession.setUrl(url);
			rsSession.setSharing_method(sharing_method);
			rsSession.setDuration_type(duration_type);
			rsSession.setFriend_id(friend_id);
			rsSession.setTimestamp(timestamp);
			Transaction tx = session.beginTransaction();
			session.save(rsSession);
			tx.commit();
			message.put("status_code", 200);
			message.put("message", "ok");
			message.put("url", url);
			message.put("token", token);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "Network Error");			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "IO Error");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 500);
			message.put("message", "Internal Server Error");
			e.printStackTrace();
		}		
    	return message;
	}//end method	

	// 4. Input parameters : userid, sharing_method, duration_type, duration_value, friend_id, timestamp
	public static Map startSharing(Integer userid, String sharing_method, Integer duration_type, Integer duration_value, String friend_id, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			String token = PlashUtils.MD5(userid + "sharing" + date.getTime());
			String url = PlashUtils.getShortUrl("http://www.plash.tw/antrack/index.html?token=" + token);
			RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
			rsSession.setUuid(userid.toString());
			rsSession.setToken(token);
			rsSession.setUrl(url);
			rsSession.setSharing_method(sharing_method);
			rsSession.setDuration_type(duration_type);
			rsSession.setDuration_value(duration_value);
			rsSession.setFriend_id(friend_id);
			rsSession.setTimestamp(timestamp);
			rsSession.setStatus(100);
			Transaction tx = session.beginTransaction();
			session.save(rsSession);
			tx.commit();
			message.put("status_code", 200);
			message.put("message", "ok");
			message.put("url", url);
			message.put("token", token);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "Network Error");			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 400);
			message.put("message", "IO Error");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			message.put("status_code", 500);
			message.put("message", "Internal Server Error");
			e.printStackTrace();
		}		
    	return message;
	}//end method
	
	// 4. Input parameters : userid, sharing_method, duration_type, duration_value, friend_id, timestamp
	public static Map uploadSharing(String token, String location, Session session) {
		System.out.println("Input String :  "+location.toString());
		Map message = new HashMap();
		
		JSONObject jsonObject;
		Object obj = JSONValue.parse(location);
		JSONParser parser = new JSONParser();
		JSONArray locations = (JSONArray)obj;
		Iterator<Object> iterator = locations.iterator();
		try{
		while (iterator.hasNext()) {
	        
	        Double latitude = 0.0, longitude = 0.0;
	                
	        Double altitude = 0.0;  	//Altitude in meter
	        Double accuracy = 0.0; 	//Accuracy in meter
	        Double speed = 0.0;	//Speed in m/s
	        Double bearing = 0.0;	//Bearing
	        String location_source = null;
	        Timestamp timestamp = null;
	        Boolean todisplay = true;
	        
			JSONObject tpoint = (JSONObject) iterator.next();
			
			if(tpoint.containsKey("latitude"))
				latitude = Double.valueOf(tpoint.get("latitude").toString()).doubleValue();
			if(tpoint.containsKey("longitude"))
				longitude = Double.valueOf(tpoint.get("longitude").toString()).doubleValue();
			if(tpoint.containsKey("altitude"))
				altitude = Double.valueOf(tpoint.get("altitude").toString()).doubleValue();
			if(tpoint.containsKey("accuracy"))
				accuracy = Double.valueOf(tpoint.get("accuracy").toString()).doubleValue();
			if(tpoint.containsKey("speed"))
				speed = Double.valueOf(tpoint.get("speed").toString()).doubleValue();
			if(tpoint.containsKey("bearing"))
				bearing = Double.valueOf(tpoint.get("bearing").toString()).doubleValue();
			if(tpoint.containsKey("location_source"))
				location_source = tpoint.get("location_source").toString();
			if(tpoint.containsKey("timestamp"))
				timestamp = Timestamp.valueOf(tpoint.get("timestamp").toString());
			if(tpoint.containsKey("todisplay")) {
				System.out.println("todisplay :  "+tpoint.get("todisplay").toString().trim());
				if(Integer.parseInt(tpoint.get("todisplay").toString().trim()) == 0)
				todisplay = false;
			}
			RealtimeSharingPoints user = new RealtimeSharingPoints();
			//Map tokenMap = new HashMap();
			//tokenMap.putAll(PlashUtils.HashToParam(token, session));
			
			//user.setUserid(Integer.parseInt(tokenMap.get("userid").toString()));
			
			//store the <trip_id> into the database
			user.setToken(token);
			
			//store the <timestamp> into the database
			user.setTimestamp(timestamp);
			
			//store the <gps> into the database
			// Add by Yu-Hsiang Huang
			WKTReader fromText = new WKTReader();
            Geometry gps = null;
		
				try {
					gps = fromText.read("POINT ("+longitude+" "+latitude+");");
				} catch (com.vividsolutions.jts.io.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gps.setSRID(4326);

			System.out.println(gps.toText());			
			user.setGps(gps);
			user.setLatitude(latitude);
			user.setLongitude(longitude);
			// end
			
			user.setAltitude(altitude);
			user.setAccuracy(accuracy);
			user.setSpeed(speed);
			user.setBearing(bearing);
			user.setLocation_source(location_source);
			user.setTodisplay(todisplay);
	    	
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				session.save(user);
				tx.commit();				
			} catch (HibernateException he) {
	    		he.printStackTrace();
	    		if(tx!=null){  
	    			tx.rollback();  
	    	    }  
	    	}
		}
		message.put("status_code", 200);
		message.put("message", "ok");			
		return message;
		} catch (GenericJDBCException e){
			
			e.printStackTrace();
		}
		message.put("status_code", 500);
		message.put("message", "Internal Server Error");
		return message;
	}//end method
	
	public static Map stopSharing(String token, Session session) {
		Map message = new HashMap();
    	Criteria criteria = session.createCriteria(RealtimeSharingSessions.class);
    	criteria.add(Restrictions.eq("token", token));
    	Iterator rsSessionItr = criteria.list().iterator();
    	if(rsSessionItr.hasNext()) {
    		Transaction tx = session.beginTransaction();
    		RealtimeSharingSessions rsSession = (RealtimeSharingSessions) rsSessionItr.next();
    		RealtimeSharingSessions rss = (RealtimeSharingSessions) session.get(RealtimeSharingSessions.class, rsSession.getId());
    		rss.setStatus(1);
			session.update(rss);
			tx.commit();
			message.put("status_code", 200);
			message.put("message", "stop sharing");
			return message;
    	}
		message.put("status_code", 500);
		message.put("message", "Internal Server Error");
    	return message;
	}//end method	
	
	public static Map startWatch(String token, String hashid, String socialid, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Criteria criteria = session.createCriteria(RealtimeSharingWatcher.class);
    	criteria.add(Restrictions.eq("token", token));
    	criteria.add(Restrictions.eq("hashid", hashid));
    	
		try {
	    	Iterator itr = criteria.list().iterator();
	    	if(itr.hasNext()) {
	    		RealtimeSharingWatcher rsWatchers = (RealtimeSharingWatcher)itr.next();
				Transaction tx = session.beginTransaction();
				RealtimeSharingWatcher rsWatcher = (RealtimeSharingWatcher) session.get(RealtimeSharingWatcher.class, rsWatchers.getId());
				rsWatcher.setHashid(hashid);
				rsWatcher.setToken(token);
				rsWatcher.setSocialid(socialid);
				rsWatcher.setTimestamp(timestamp);
				session.update(rsWatcher);
				tx.commit();
	    	} else {
				RealtimeSharingWatcher rsWatcher = new RealtimeSharingWatcher();
				rsWatcher.setHashid(hashid);
				rsWatcher.setToken(token);
				rsWatcher.setSocialid(socialid);
				rsWatcher.setTimestamp(timestamp);
				Transaction tx = session.beginTransaction();
				session.save(rsWatcher);
				tx.commit();	    		
	    	}
		} catch (NoSuchElementException nsee){
			RealtimeSharingWatcher rsWatcher = new RealtimeSharingWatcher();
			rsWatcher.setHashid(hashid);
			rsWatcher.setToken(token);
			rsWatcher.setSocialid(socialid);
			rsWatcher.setTimestamp(timestamp);
			Transaction tx = session.beginTransaction();
			session.save(rsWatcher);
			tx.commit();	
		}
		message.put("status_code", 200);
		message.put("message", "ok");
    	return message;
	}//end method	

	public static Map stopWatch(String token, String hashid, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			Criteria criteria = session.createCriteria(RealtimeSharingWatcher.class);
	    	criteria.add(Restrictions.eq("token", token));
	    	criteria.add(Restrictions.eq("hashid", hashid));
	    	
	    	RealtimeSharingWatcher rsWatchers = (RealtimeSharingWatcher) criteria.list().iterator().next();
	    	
			Transaction tx = session.beginTransaction();
			session.delete(rsWatchers);
			tx.commit();
		} catch (NullPointerException ne) {
		}
		message.put("status_code", 200);
		message.put("message", "ok");
    	return message;
	}//end method	

	
	public static Map getLocation(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingPoints.class);
    	criteria.add(Restrictions.eq("token", token));
    	criteria.add(Restrictions.eq("todisplay", true));
    	ProjectionList projectionList = Projections.projectionList();
    	projectionList.add(Projections.property("longitude"), "longitude");
    	projectionList.add(Projections.property("latitude"), "latitude");
    	projectionList.add(Projections.property("timestamp"), "timestamp");
    	criteria.setProjection(projectionList);
    	criteria.addOrder(Order.asc("timestamp"));
    	criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	boolean fristRun = true;
    	Geometry tmpGPS;
    	Timestamp stimestamp = null, etimestamp = null;
    	Double lng, lat, prv_lng = 0.0, prv_lat = 0.0, distance = 0.0;
		Map rsLocations = new HashMap();    	
		try {
			List<Map> tripDataList = (List<Map>) criteria.list();
    		for (Map tmpMap:tripDataList) {
    			//tmpGPS = (Geometry)tmpMap.remove("gps");
    			if ((prv_lng == 0.0)&&(prv_lat == 0.0)) etimestamp = Timestamp.valueOf(tmpMap.get("timestamp").toString());
    			if (prv_lng == 0.0) prv_lng = ConvertDegreeToRadians(Double.valueOf(tmpMap.get("longitude").toString()).doubleValue());
    			if (prv_lat == 0.0) prv_lat = ConvertDegreeToRadians(Double.valueOf(tmpMap.get("latitude").toString()).doubleValue());
    			lng = ConvertDegreeToRadians(Double.valueOf(tmpMap.get("longitude").toString()).doubleValue());
    			lat = ConvertDegreeToRadians(Double.valueOf(tmpMap.get("latitude").toString()).doubleValue());
    			double R = 6371;
    			double d = Math.acos(Math.sin(prv_lat)*Math.sin(lat)+
    			            Math.cos(prv_lat)*Math.cos(lat)*
    			            Math.cos(lng-prv_lng))*R;
    			if(Double.isNaN(d)) d = 0.0;
    			distance = distance + d;
    			//System.out.println("\nDistance :  " + distance);
    			prv_lng = lng;
    			prv_lat = lat;
    			if (fristRun == true){
    				stimestamp = Timestamp.valueOf(tmpMap.get("timestamp").toString());
    				fristRun = false;
    			}
				etimestamp = Timestamp.valueOf(tmpMap.remove("timestamp").toString());
	    		tmpMap.put("lng", tmpMap.remove("longitude"));
	    		tmpMap.put("lat", tmpMap.remove("latitude"));
	    		//tmpMap.put("distance", d);
    			//tmpMap.put("longitude", tmpGPS.getCoordinate().x*1000000);
    			//tmpMap.put("latitude", tmpGPS.getCoordinate().y*1000000);
    		}//rof
    		NumberFormat formatter = new DecimalFormat("#.#");
    		rsLocations.put("trip", tripDataList);
    		rsLocations.put("distance", formatter.format(Double.valueOf(distance)));
    		rsLocations.put("start", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stimestamp));
    		rsLocations.put("end", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(etimestamp));
    		rsLocations.put("eta", (etimestamp.getTime()-stimestamp.getTime())/1000/60);
    		rsLocations.put("speed", formatter.format(Double.valueOf(distance/(etimestamp.getTime()-stimestamp.getTime()))*1000*60*60));
			return rsLocations;
											
		} catch (HibernateException he) {
			System.out.println(he.toString()); 	
			return null;
		}//end try catch			//*/
	}//end method
	
	public static int getWatcherNum(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingWatcher.class);
    	criteria.add(Restrictions.eq("token", token));
    	criteria.setProjection(Projections.rowCount());
    	int watcherNum = 0;
		try {
			List watcherNums = criteria.list();
			Iterator it = watcherNums.iterator();
			if (!it.hasNext()){
				watcherNum = 0;
			} else {
				while(it.hasNext()){
					Object count = it.next();
					watcherNum = Integer.parseInt(count.toString());  
				}
			}
			return watcherNum;		
		} catch (HibernateException he) {
			return 0;
		}//end try catch			//*/
	}//end method
	
	public static Map getWatcher(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingWatcher.class);
    	criteria.add(Restrictions.eq("token", token));
    	ProjectionList projectionList = Projections.projectionList();
    	projectionList.add(Projections.property("socialid"), "socialid");     
    	criteria.setProjection(projectionList);
    	criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		Map rsWatchers = new HashMap();    	
		try {
			List<Map> watcherList = (List<Map>) criteria.list();
    		for (Map tmpMap:watcherList) {
    			if (tmpMap.get("socialid") != null) {
    				String socialid = tmpMap.remove("socialid").toString();
    				HttpClient client = new DefaultHttpClient();
    				String shortUrlAPI = "http://graph.facebook.com/"+socialid;
    				HttpGet request = new HttpGet(shortUrlAPI);
    				HttpResponse response = client.execute(request);
    				BufferedReader rd = new BufferedReader
    						  (new InputStreamReader(response.getEntity().getContent()));
    						    
    				String line = rd.readLine();
    				if(line != null) {
    					Object obj;
    					JSONObject jsonObject;
    					JSONParser parser = new JSONParser();
    					obj = parser.parse(line);
    					jsonObject = (JSONObject) obj;
    					tmpMap.put("name", jsonObject.get("first_name").toString());
    					tmpMap.put("picture", "http://graph.facebook.com/"+socialid+"/picture");
    				} else {
        	    		tmpMap.put("name", "someone");
        	    		tmpMap.put("picture", "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash4/c178.0.604.604/s160x160/252231_1002029915278_1941483569_n.jpg");    				    					
    				}
    			} else {
    	    		tmpMap.put("name", "someone");
    	    		tmpMap.put("picture", "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash4/c178.0.604.604/s160x160/252231_1002029915278_1941483569_n.jpg");    				
    			}
    		}//rof
    		rsWatchers.put("watchers", watcherList);
		} catch (HibernateException he) {
			System.out.println(he.toString()); 	
			return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //end try catch			//*/
		return rsWatchers;
	}//end method

	
	
    private static double ConvertDegreeToRadians(double degrees) {
      return (Math.PI/180)*degrees;
    }
	

	public static Map uploadPicture(String token, String picUrl, Double latitude, Double longitude, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			RealtimeSharingCheckins rsSession = new RealtimeSharingCheckins();
			rsSession.setToken(token);
			rsSession.setUrl(picUrl);
			rsSession.setLatitude(latitude);
			rsSession.setLongitude(longitude);
			rsSession.setTimestamp(timestamp);
			Transaction tx = session.beginTransaction();
			session.save(rsSession);
			tx.commit();
			message.put("status_code", 200);
			message.put("message", "ok");			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			message.put("status_code", 500);
			message.put("message", "Internal Server Error");
			e.printStackTrace();
		}		
    	return message;
	}//end method
	
	public static String getSharer(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingSessions.class);
    	criteria.add(Restrictions.eq("token", token));
    	ProjectionList filterProjList = Projections.projectionList();   
    	filterProjList.add(Projections.property("username"),"username");
    	criteria.setProjection(filterProjList);    	
    	String username = null;
    	Iterator users = criteria.list().iterator();
    	if(users.hasNext()) {
    		Object userInfo = users.next();
    		username = userInfo.toString();
    		if(username == null) username = "Someone";
    	}
    	return username;
	}//end method
	
	public static Map getCheckin(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingCheckins.class);
    	criteria.add(Restrictions.eq("token", token));
    	ProjectionList projectionList = Projections.projectionList();
    	projectionList.add(Projections.property("url"), "url");    	
    	projectionList.add(Projections.property("longitude"), "longitude");
    	projectionList.add(Projections.property("latitude"), "latitude");
    	//projectionList.add(Projections.property("timestamp"), "timestamp");
    	criteria.setProjection(projectionList);
    	criteria.addOrder(Order.asc("timestamp"));
    	criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	Double lng, lat;
		Map rsCheckins = new HashMap();    	
		try {
			List<Map> tripDataList = (List<Map>) criteria.list();
    		for (Map tmpMap:tripDataList) {
	    		tmpMap.put("lng", tmpMap.remove("longitude"));
	    		tmpMap.put("lat", tmpMap.remove("latitude"));
	    		String url = tmpMap.remove("url").toString();
	    		String thumb = url.replace("/picture/"+token, "/picture/"+token+"/thumb.s");
	    		tmpMap.put("url", url);
	    		tmpMap.put("thumb", thumb);
	    		//tmpMap.put("timestamp", tmpMap.remove("timestamp"));
    		}//rof
    		rsCheckins.put("checkin", tripDataList);
			return rsCheckins;											
		} catch (HibernateException he) {
			System.out.println(he.toString()); 	
			return null;
		}//end try catch			//*/
	}//end method
} // PlashUtils End 
