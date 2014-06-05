package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import tw.edu.sinica.iis.ants.RealtimeSharing;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class AntHomeComponent extends PLASHComponent{

	private Session session; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("AntHomeComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
		session = sessionFactory.openSession(); 
		Object obj;
		JSONObject jsonObject;
		
		if(map.containsKey("action")){
			String action = map.remove("action").toString();
			System.out.println("LocationSharingComponent Action:\t"+ action +"\t"+ Calendar.getInstance().getTimeInMillis());
			if (action.contains("initialize")){
				String uuid = null, sharer = null;
				Timestamp timestamp = null;

				if(map.containsKey("uuid")){
					uuid = map.get("uuid").toString();
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
				if(map.containsKey("sharer")){
					sharer = map.remove("sharer").toString();
				}
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
		        if (uuid != null && sharer != null) {
		        	map.putAll(RealtimeSharing.anthomeInitial(uuid, sharer, timestamp, session));
		        } else {
					map.put("status_code", 400);
					map.put("message", "Parameter Error");
					//map.put("userid", userid);
		        }		        	
			}			
			if (action.contains("subscribe")){ 

			}
			if (action.contains("auth")){

			}			

		}
		
		session.close();
	    System.out.println("LocationSharingComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
	    	    
	    return map;
	}
}