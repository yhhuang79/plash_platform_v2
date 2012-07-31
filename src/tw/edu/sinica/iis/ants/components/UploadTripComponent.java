package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.DB.T_CheckInInfo;
import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.DB.T_TripData;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class UploadTripComponent extends PLASHComponent{

	private Session session; //task session

	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("UploadTripComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
		session = sessionFactory.openSession(); 

		JSONParser parser = new JSONParser();		
		try {
			//System.out.println(map.get("trip").toString());
			Object obj = parser.parse(map.remove("trip").toString());
			JSONObject jsonObject = (JSONObject) obj;			
			Integer userid = Integer.parseInt(jsonObject.get("userid").toString());
			Integer trip_id = Integer.parseInt(jsonObject.get("trip_id").toString());
			//System.out.println(userid);
			JSONArray checkin = (JSONArray) jsonObject.get("CheckInDataList");
			System.out.println(checkin.toString());
			Iterator<Object> iterator = checkin.iterator();
			while (iterator.hasNext()) {
		        		        
		        Integer label = null;
		        Double lat = 0.0, lng = 0.0;

		                
		        Double alt = 0.0;  	//Altitude in meter
		        Double accu = 0.0; 	//Accuracy in meter
		        Double spd = 0.0;	//Speed in m/s
		        Double bear = 0.0;	//Bearing
		        Double accex = 0.0;	//x-axis acceleration
		        Double accey = 0.0;	//y-axis acceleration
		        Double accez= 0.0; 	//z-axis acceleration
		        String gsminfo = ""; //first 5 is phone information, last 3 is the nearest cell information, NULL if information is missing
		        String wifiinfo = ""; // set of AP info: MAC address, frequency (MHz), signal strength (dBM)
		        Integer app = 0;
		    	Double azimuth = 0.0;
		    	Double pitch = 0.0;
		    	Double roll = 0.0;
		    	String battery_info = "";        
		        
		        Timestamp timestamp = null;
		        
				JSONObject tpoint = (JSONObject) iterator.next();
				
				if(tpoint.containsKey("label"))
					label = Integer.parseInt(tpoint.get("label").toString());
				if(tpoint.containsKey("lat"))
					lat = Double.valueOf(tpoint.get("lat").toString()).doubleValue();
				if(tpoint.containsKey("lng"))
					lat = Double.valueOf(tpoint.get("lng").toString()).doubleValue();
				if(tpoint.containsKey("alt"))
					lat = Double.valueOf(tpoint.get("alt").toString()).doubleValue();
				if(tpoint.containsKey("accu"))
					lat = Double.valueOf(tpoint.get("accu").toString()).doubleValue();
				if(tpoint.containsKey("spd"))
					lat = Double.valueOf(tpoint.get("spd").toString()).doubleValue();
				if(tpoint.containsKey("bear"))
					lat = Double.valueOf(tpoint.get("bear").toString()).doubleValue();
				if(tpoint.containsKey("accex"))
					lat = Double.valueOf(tpoint.get("accex").toString()).doubleValue();
				if(tpoint.containsKey("accey"))
					lat = Double.valueOf(tpoint.get("accey").toString()).doubleValue();
				if(tpoint.containsKey("accez"))
					lat = Double.valueOf(tpoint.get("accez").toString()).doubleValue();
				if(tpoint.containsKey("gsminfo"))
					gsminfo = tpoint.get("gsminfo").toString();
				if(tpoint.containsKey("wifiinfo"))
					gsminfo = tpoint.get("wifiinfo").toString();
				if(tpoint.containsKey("app"))
					label = Integer.parseInt(tpoint.get("app").toString());
				if(tpoint.containsKey("azimuth"))
					lat = Double.valueOf(tpoint.get("azimuth").toString()).doubleValue();				
				if(tpoint.containsKey("pitch"))
					lat = Double.valueOf(tpoint.get("pitch").toString()).doubleValue();
				if(tpoint.containsKey("roll"))
					lat = Double.valueOf(tpoint.get("roll").toString()).doubleValue();				
				if(tpoint.containsKey("battery_info"))
					gsminfo = tpoint.get("battery_info").toString();				
				if(tpoint.containsKey("timestamp"))
					timestamp = Timestamp.valueOf(tpoint.get("timestamp").toString());
				
				T_TripData user = new T_TripData();
				user.setUserid(userid);
				
				//store the <trip_id> into the database
				user.setTrip_id(trip_id);
				
				//store the <timestamp> into the database
				user.setTimestamp(timestamp);
				
				//store the <gps> into the database
				// Add by Yu-Hsiang Huang
				WKTReader fromText = new WKTReader();
	            Geometry gps = null;
			
					try {
						gps = fromText.read("POINT("+lng+" "+lat+")");
					} catch (com.vividsolutions.jts.io.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gps.setSRID(4326);

				user.setGps(gps);	
				// end
				
				//get the value of <server_timestamp>, and store it into the database
				user.setServer_timestamp(new Timestamp(new Date().getTime()));
				
				//store the <label> into the database
				user.setLabel(label);
				

				user.setAlt(alt);
				user.setAccu(accu);
				user.setSpd(spd);
				user.setBear(bear);
				user.setAccex(accex);
				user.setAccey(accey);
				user.setAccez(accez);
				user.setGsminfo(gsminfo);
				user.setWifiinfo(wifiinfo);
				user.setLatitude(lat);
				user.setLongitude(lng);
				user.setApp(app);		        
		    	user.setAzimuth(azimuth);
		    	user.setPitch(pitch);
		    	user.setRoll(roll);
		    	user.setBattery_info(battery_info);
		    	
		    	Integer tid = 0;
		    	if(tpoint.containsKey("CheckIn")){
		    		user.setCheckin(true);
		    		
					Transaction tx = session.beginTransaction();
					session.save(user);
					tx.commit();
					
					Criteria criteria = session.createCriteria(T_TripData.class);
					criteria.add(Restrictions.eq("userid", userid));
					criteria.add(Restrictions.eq("trip_id", trip_id));
					criteria.addOrder(Order.desc("id"));
					Iterator trip_point = criteria.list().iterator(); 
					if(trip_point.hasNext()) {
						T_TripData tp = (T_TripData) trip_point.next();
						tid=tp.getId();
					}	
					
		    	}else{
		    		user.setCheckin(false);
		    		
					Transaction tx = session.beginTransaction();
					session.save(user);
					tx.commit();
		    	}
			
				if(tpoint.containsKey("CheckIn")) {
					JSONObject checkindata = (JSONObject) tpoint.get("CheckIn");
					T_CheckInInfo CData = new T_CheckInInfo();
					CData.setId(tid);
					if(checkindata.containsKey("message"))
						CData.setMessage(checkindata.get("message").toString());
					if(checkindata.containsKey("emotion"))
						CData.setEmotion(Integer.parseInt(checkindata.get("emotion").toString()));
					if(checkindata.containsKey("picture_uri"))
						CData.setPicture_uri(checkindata.get("picture_uri").toString());
					
					Transaction tx2 = session.beginTransaction();
					session.save(CData);
					tx2.commit();										
				}
			}
			
			map.put("message", "Ok");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
		
	    System.out.println("UploadTripComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
	    	    
	    return map;
	}
}