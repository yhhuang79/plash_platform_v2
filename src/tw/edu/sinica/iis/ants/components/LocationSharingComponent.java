package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import tw.edu.sinica.iis.ants.RealtimeSharing;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class LocationSharingComponent extends PLASHComponent{

	private Session session; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("LocationSharingComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
		session = sessionFactory.openSession(); 
		Object obj;
		JSONObject jsonObject;
		
		if(map.containsKey("action")){
			String action = map.remove("action").toString();
			if (action.contains("initialize")){
				//Integer userid = 0;
				String userid = null;
				Integer duration_type = 0;
				Integer duration_value = 0;
				String sharing_method = null;
				String friend_id = null;
				Timestamp timestamp = null;

				if(map.containsKey("userid")){
					userid = map.get("userid").toString();
					/*
					 *  Plash account userid
					 * 
					try {
						userid = Integer.valueOf(map_userid);
					} catch (NumberFormatException e) {
						userid = PlashUtils.getUserid(map_userid, session);
					}
					*/
				}
				if(map.containsKey("sharing_method")){
					sharing_method = map.remove("sharing_method").toString();
				}
				if(map.containsKey("friend_id"))
					friend_id = map.remove("friend_id").toString();
				if(map.containsKey("duration_type")){
					duration_type = Integer.valueOf(map.remove("duration_type").toString());
				}
				if(map.containsKey("duration_value"))
					duration_value = Integer.valueOf(map.remove("duration_value").toString());
		        if (map.containsKey("timestamp")){
		        	try {
		        		timestamp = Timestamp.valueOf(map.remove("timestamp").toString());
		        	} catch (IllegalArgumentException e) {
			        	Date date= new java.util.Date();
			        	timestamp = new Timestamp(date.getTime());		        		
		        	}
		        }else{
		        	Date date= new java.util.Date();
		        	timestamp = new Timestamp(date.getTime());
		        }
		        if (userid != null) {
		        	map.putAll(RealtimeSharing.initialSharing(userid, timestamp, session));
		        } else {
					map.put("status_code", 400);
					map.put("message", "Parameter Error");
					//map.put("userid", userid);
		        }		        	
			}			
			if (action.contains("start")){
				String token = null;
				String sharing_method = null;
				Timestamp timestamp = null;

				if(map.containsKey("token")){
					token = map.remove("token").toString();
				}
				if(map.containsKey("sharing_method")){
					sharing_method = map.remove("sharing_method").toString();
				}
		        if (map.containsKey("timestamp")){ 
		        	timestamp = Timestamp.valueOf(map.remove("timestamp").toString());
		        }else{
		        	Date date= new java.util.Date();
		        	timestamp = new Timestamp(date.getTime());
		        }
		        
		       map.putAll(RealtimeSharing.startSharing(token, sharing_method, timestamp, session));
		        

			}
			if (action.contains("stop")){
				String token;
				if(map.containsKey("token")){
					token = map.remove("token").toString();
					boolean isTokenAlive = RealtimeSharing.checkToken(token, session);
					if(isTokenAlive){
						map.putAll(RealtimeSharing.stopSharing(token, session));
					}else{
						map.put("status_code", 400);
						map.put("message", "Invalid Token");						
					}					
				}else{
					map.clear();
					map.put("status_code", 400);
					map.put("message", "Empty token");
				}
			}
			if (action.contains("sWatch")){
				System.out.println("startWatch : " + map.get("hashid").toString());
				String token;
				String hashid = null;
				String socialid = null;
				Timestamp timestamp = null;
				if(map.containsKey("token")){
					token = map.remove("token").toString();
					if(map.containsKey("hashid")) hashid = map.remove("hashid").toString();
					if(map.containsKey("socialid")) hashid = map.remove("socialid").toString();
					if(map.containsKey("timestamp")) hashid = map.remove("timestamp").toString();
					
					boolean isTokenAlive = RealtimeSharing.checkToken(token, session);
					if(isTokenAlive){
						map.putAll(RealtimeSharing.startWatch(token, hashid, socialid, timestamp, session));
					}else{
						map.put("status_code", 400);
						map.put("message", "Invalid Token");						
					}					
				}else{
					map.clear();
					map.put("status_code", 400);
					map.put("message", "Empty token");
				}
			}
			if (action.contains("eWatch")){
				String token = null;
				String hashid = null;
				if(map.containsKey("token")){
					token = map.remove("token").toString();
					if(map.containsKey("hashid")) hashid = map.remove("hashid").toString();
						map.putAll(RealtimeSharing.stopWatch(token, hashid, session));
				}else{
					map.clear();
					map.put("status_code", 400);
					map.put("message", "Empty token");
				}
			}
			
			if (action.toLowerCase() == "update"){
	
			}
			if (action.contains("get")){
				String token;
				if(map.containsKey("token")){
					token = map.remove("token").toString();
					boolean isTokenAlive = RealtimeSharing.checkToken(token, session);
					if(isTokenAlive){
						map.putAll(RealtimeSharing.getLocation(token, session));
						map.put("status_code", 200);
						map.put("message", "Ok");
					}else{
						map.put("status_code", 400);
						map.put("message", "Invalid Token");						
					}					
				}else{
					map.clear();
					map.put("status_code", 400);
					map.put("message", "Empty token");
				}
			}
			if (action.contains("upload")){
				String token, location;
				if(map.containsKey("token")){
					token = map.remove("token").toString();
					boolean isTokenAlive = RealtimeSharing.checkToken(token, session);
					if(isTokenAlive){
						if(map.containsKey("location")){
							location = map.remove("location").toString();
							map.putAll(RealtimeSharing.uploadSharing(token, location, session));
							map.put("number_of_watcher", RealtimeSharing.getWatcherNum(token, session));
						} else {
							map.put("status_code", 400);
							map.put("message", "parameter error");						
						}
					}else{
						map.put("status_code", 400);
						map.put("message", "Invalid Token");						
					}					
				}else{
					map.clear();
					map.put("status_code", 400);
					map.put("message", "Empty token");
				}				
			}			
		}
		
		session.close();
	    System.out.println("LocationSharingComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
	    	    
	    return map;
	}
}