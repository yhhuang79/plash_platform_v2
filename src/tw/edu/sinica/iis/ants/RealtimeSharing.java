package tw.edu.sinica.iis.ants;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import tw.edu.sinica.iis.ants.DB.T_TripData;
import tw.edu.sinica.iis.ants.db.antrip.RealtimeSharingPoints;
import tw.edu.sinica.iis.ants.db.antrip.RealtimeSharingSessions;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class RealtimeSharing {

	public static boolean checkToken(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingSessions.class);
    	criteria.add(Restrictions.eq("token", token));
    	criteria.add(Restrictions.eq("status", 1));
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

	// 1. Input parameters : userid(Integer), timestamp
	public static Map initialSharing(Integer userid, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			String token = PlashUtils.MD5(userid + "sharing" + date.getTime());
			String url = PlashUtils.getShortUrl("http://www.plash.tw/antrack/index.html?token=" + token);
			RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
			rsSession.setUserid(userid);
			rsSession.setToken(token);
			rsSession.setUrl(url);
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
	public static Map initialSharing(String userid, Timestamp timestamp, Session session) {
		Map message = new HashMap();
		Date date= new java.util.Date();
		try {
			String token = PlashUtils.MD5(userid.substring((int)(Math.random()*10), 16) + date.getTime());
			String url = PlashUtils.getShortUrl("http://www.plash.tw/antrack/index.html?token=" + token);
			RealtimeSharingSessions rsSession = new RealtimeSharingSessions();
			rsSession.setHashid(userid);
			rsSession.setToken(token);
			rsSession.setUrl(url);
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
			rsSession.setUserid(userid);
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
			rsSession.setUserid(userid);
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
			rsSession.setUserid(userid);
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
			rsSession.setUserid(userid);
			rsSession.setToken(token);
			rsSession.setUrl(url);
			rsSession.setSharing_method(sharing_method);
			rsSession.setDuration_type(duration_type);
			rsSession.setDuration_value(duration_value);
			rsSession.setFriend_id(friend_id);
			rsSession.setTimestamp(timestamp);
			rsSession.setStatus(1);
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
		Map message = new HashMap();
		
		JSONObject jsonObject;
		Object obj = JSONValue.parse(location);
		JSONParser parser = new JSONParser();
		JSONArray locations = (JSONArray)obj;
		Iterator<Object> iterator = locations.iterator();
		
		while (iterator.hasNext()) {
	        
	        Double latitude = 0.0, longitude = 0.0;
	                
	        Double altitude = 0.0;  	//Altitude in meter
	        Double accuracy = 0.0; 	//Accuracy in meter
	        Double speed = 0.0;	//Speed in m/s
	        Double bearing = 0.0;	//Bearing
	        String location_source = null;
	        Timestamp timestamp = null;
	        
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
			
			RealtimeSharingPoints user = new RealtimeSharingPoints();
			Map tokenMap = new HashMap();
			tokenMap.putAll(PlashUtils.HashToParam(token, session));
			
			user.setUserid(Integer.parseInt(tokenMap.get("userid").toString()));
			
			//store the <trip_id> into the database
			user.setToken(token);
			
			//store the <timestamp> into the database
			user.setTimestamp(timestamp);
			
			//store the <gps> into the database
			// Add by Yu-Hsiang Huang
			WKTReader fromText = new WKTReader();
            Geometry gps = null;
		
				try {
					gps = fromText.read("POINT("+longitude+" "+latitude+")");
				} catch (com.vividsolutions.jts.io.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gps.setSRID(4326);

			user.setGps(gps);	
			// end
			
			user.setAltitude(altitude);
			user.setAccuracy(accuracy);
			user.setSpeed(speed);
			user.setBearing(bearing);

	    	
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				session.persist(user);
				tx.commit();
				message.put("status_code", 200);
				message.put("message", "ok");			
				return message;
			} catch (HibernateException he) {
	    		he.printStackTrace();
	    		if(tx!=null){  
	    			tx.rollback();  
	    	    }  
	    	}
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
    		rss.setStatus(0);
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
	
	public static Map getLocation(String token, Session session) {
    	Criteria criteria = session.createCriteria(RealtimeSharingPoints.class);
    	criteria.add(Restrictions.eq("token", token));
    	criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	Geometry tmpGPS;
		Map rsLocations = new HashMap();    	
		try {
			List<Map> tripDataList = (List<Map>) criteria.list();
    		for (Map tmpMap:tripDataList) {
    			tmpGPS = (Geometry)tmpMap.remove("gps");
	    		tmpMap.put("lng", tmpGPS.getCoordinate().x*1000000);
	    		tmpMap.put("lat", tmpGPS.getCoordinate().y*1000000);		    				
    		}//rof
    		rsLocations.put("trip", tripDataList);
			return rsLocations;
											
		} catch (HibernateException he) {
			System.out.println(he.toString()); 	
			return null;
		}//end try catch			//*/
	}//end method			
} // PlashUtils End 
