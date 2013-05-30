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
			if (action.contains("initial")){
				Integer userid = 0;
				Integer duration_type = 0;
				Integer duration_value = 0;
				String sharing_method = null;
				String friend_id = null;
				Timestamp timestamp = null;

				if(map.containsKey("userid")){
					userid = Integer.valueOf(map.remove("userid").toString());
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
		        	timestamp = Timestamp.valueOf(map.remove("timestamp").toString());
		        }else{
		        	Date date= new java.util.Date();
		        	timestamp = new Timestamp(date.getTime());
		        }
		        
		       map.putAll(RealtimeSharing.initialSharing(userid, timestamp, session));
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
			if (action.toLowerCase() == "update"){
				
			}
			if (action.toLowerCase() == "get"){
				String token;
				if(map.containsKey("token")){
					token = map.remove("token").toString();
					boolean isTokenAlive = RealtimeSharing.checkToken(token, session);
					if(isTokenAlive){
						map.putAll(RealtimeSharing.getLocation(token, session));
						map.put("status_code", 200);
						map.put("message", "stop sharing");
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
			if (action.toLowerCase() == "upload"){
				
			}			
		}
		
		session.close();
	    System.out.println("LocationSharingComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
	    	    
	    return map;
	}
}