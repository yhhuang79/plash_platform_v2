package tw.edu.sinica.iis.ants.components;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tw.edu.sinica.iis.ants.PlashUtils;
import tw.edu.sinica.iis.ants.DB.T_CheckInInfo;
import tw.edu.sinica.iis.ants.DB.T_TripData;
import tw.edu.sinica.iis.ants.DB.T_TripHash;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class UploadTripComponent extends PLASHComponent{

	private Session session; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("UploadTripComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
		session = sessionFactory.openSession(); 
		Integer userid = 0;
		Integer trip_id = 0;
		Object obj;
		JSONObject jsonObject;
		
		JSONParser parser = new JSONParser();		
		try {
			if(map.containsKey("tripinfo")){
				System.out.println(map.get("tripinfo").toString());
				obj = parser.parse(map.remove("tripinfo").toString());
				jsonObject = (JSONObject) obj;
				String trip_name = "Untitled";
			    String st_addr_prt1 = "";
			    String st_addr_prt2 = ""; 
			    String st_addr_prt3 = ""; 
			    String st_addr_prt4 = ""; 
			    String st_addr_prt5 = "";  
			    String et_addr_prt1 = "";
			    String et_addr_prt2 = ""; 
			    String et_addr_prt3 = ""; 
			    String et_addr_prt4 = ""; 
			    String et_addr_prt5 = "";        
				
		        if (jsonObject.containsKey("trip_name")) {
		        	trip_name = jsonObject.get("trip_name").toString();
		        }//fi     
				userid = Integer.parseInt(jsonObject.get("userid").toString());
				if(jsonObject.containsKey("trip_id")) {
					trip_id = Integer.parseInt(jsonObject.get("trip_id").toString());
				} else {
					trip_id = PlashUtils.getNewTripId(userid, session);				
				}
				Timestamp trip_st = Timestamp.valueOf(jsonObject.get("trip_st").toString());
				Timestamp trip_et = Timestamp.valueOf(jsonObject.get("trip_et").toString());
				Integer trip_length = Integer.parseInt(jsonObject.get("trip_length").toString());
				Integer num_of_pts = Integer.parseInt(jsonObject.get("num_of_pts").toString());
				
		        if (jsonObject.containsKey("st_addr_prt1")){
		        	st_addr_prt1 = jsonObject.get("st_addr_prt1").toString();
		        }//fi        	
		        if (jsonObject.containsKey("st_addr_prt2")){
		        	st_addr_prt2 = jsonObject.get("st_addr_prt2").toString();
		        }//fi  
		        if (jsonObject.containsKey("st_addr_prt3")){
		        	st_addr_prt3 = jsonObject.get("st_addr_prt3").toString();
		        }//fi  
		        if (jsonObject.containsKey("st_addr_prt4")){
		        	st_addr_prt4 = jsonObject.get("st_addr_prt4").toString();
		        }//fi          
		        if (jsonObject.containsKey("st_addr_prt5")){
		        	st_addr_prt5 = jsonObject.get("st_addr_prt5").toString();
		        }//fi     
	
		        if (jsonObject.containsKey("et_addr_prt1")){
		        	et_addr_prt1 = jsonObject.get("et_addr_prt1").toString();
		        }//fi        	
		        if (jsonObject.containsKey("et_addr_prt2")){
		        	et_addr_prt2 = jsonObject.get("et_addr_prt2").toString();
		        }//fi  
		        if (jsonObject.containsKey("et_addr_prt3")){
		        	et_addr_prt3 = jsonObject.get("et_addr_prt3").toString();
		        }//fi  
		        if (jsonObject.containsKey("et_addr_prt4")){
		        	et_addr_prt4 = jsonObject.get("et_addr_prt4").toString();
		        }//fi          
		        if (jsonObject.containsKey("et_addr_prt5")){
		        	et_addr_prt5 = jsonObject.get("et_addr_prt5").toString();
		        }//fi
		        
		        Short update_status = Short.parseShort(map.get("update_status").toString());
		        T_TripInfo tmpTripInfo = new T_TripInfo();
				tmpTripInfo.setUserid(userid);			
				tmpTripInfo.setTrip_id(trip_id);
				tmpTripInfo.setTrip_name(trip_name);
				tmpTripInfo.setTrip_st(trip_st);
				tmpTripInfo.setTrip_et(trip_et);
				tmpTripInfo.setTrip_length(trip_length);
				tmpTripInfo.setNum_of_pts(num_of_pts);
				tmpTripInfo.setSt_addr_prt1(st_addr_prt1);
				tmpTripInfo.setSt_addr_prt2(st_addr_prt2);
				tmpTripInfo.setSt_addr_prt3(st_addr_prt3);
				tmpTripInfo.setSt_addr_prt4(st_addr_prt4);
				tmpTripInfo.setSt_addr_prt5(st_addr_prt5);
				tmpTripInfo.setEt_addr_prt1(et_addr_prt1);				
				tmpTripInfo.setEt_addr_prt2(et_addr_prt2);
				tmpTripInfo.setEt_addr_prt3(et_addr_prt3);
				tmpTripInfo.setEt_addr_prt4(et_addr_prt4);
				tmpTripInfo.setEt_addr_prt5(et_addr_prt5);				
				tmpTripInfo.setUpdate_status(update_status);
		        
				Transaction tx = session.beginTransaction();
				session.persist(tmpTripInfo);
				tx.commit();
				
				T_TripHash tth = new T_TripHash();
				tth.setId(tmpTripInfo.getId());
				tth.setTrip_id(tmpTripInfo.getTrip_id());
				tth.setUserid(tmpTripInfo.getUserid());
				try {
					String tst = "";
					if (tmpTripInfo.getTrip_st() != null)
						tst = tmpTripInfo.getTrip_st().toString();
					tth.setHash(PlashUtils.MD5(tmpTripInfo.getId().toString() + "#" + tst));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				tx = session.beginTransaction();
				session.save(tth);
				tx.commit();
			}
			
			
			obj = parser.parse(map.remove("trip").toString());
			jsonObject = (JSONObject) obj;	
			if (jsonObject.containsKey("userid"))
				userid = Integer.parseInt(jsonObject.get("userid").toString());
			if (jsonObject.containsKey("trip_id"))
				trip_id = Integer.parseInt(jsonObject.get("trip_id").toString());
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
		        String gsminfo = null; //first 5 is phone information, last 3 is the nearest cell information, NULL if information is missing
		        String wifiinfo = null; // set of AP info: MAC address, frequency (MHz), signal strength (dBM)
		        Integer app = 0;
		    	Double azimuth = 0.0;
		    	Double pitch = 0.0;
		    	Double roll = 0.0;
		    	String battery_info = null;        
		        
		        Timestamp timestamp = null;
		        
				JSONObject tpoint = (JSONObject) iterator.next();
				
				if(tpoint.containsKey("label"))
					label = Integer.parseInt(tpoint.get("label").toString());
				if(tpoint.containsKey("lat"))
					lat = Double.valueOf(tpoint.get("lat").toString()).doubleValue();
				if(tpoint.containsKey("lng"))
					lng = Double.valueOf(tpoint.get("lng").toString()).doubleValue();
				if(tpoint.containsKey("alt"))
					alt = Double.valueOf(tpoint.get("alt").toString()).doubleValue();
				if(tpoint.containsKey("accu"))
					accu = Double.valueOf(tpoint.get("accu").toString()).doubleValue();
				if(tpoint.containsKey("spd"))
					spd = Double.valueOf(tpoint.get("spd").toString()).doubleValue();
				if(tpoint.containsKey("bear"))
					bear = Double.valueOf(tpoint.get("bear").toString()).doubleValue();
				if(tpoint.containsKey("accex"))
					accex = Double.valueOf(tpoint.get("accex").toString()).doubleValue();
				if(tpoint.containsKey("accey"))
					accey = Double.valueOf(tpoint.get("accey").toString()).doubleValue();
				if(tpoint.containsKey("accez"))
					accez = Double.valueOf(tpoint.get("accez").toString()).doubleValue();
				if(tpoint.containsKey("gsminfo"))
					gsminfo = tpoint.get("gsminfo").toString();
				if(tpoint.containsKey("wifiinfo"))
					wifiinfo = tpoint.get("wifiinfo").toString();
				if(tpoint.containsKey("app"))
					app = Integer.parseInt(tpoint.get("app").toString());
				if(tpoint.containsKey("azimuth"))
					azimuth = Double.valueOf(tpoint.get("azimuth").toString()).doubleValue();				
				if(tpoint.containsKey("pitch"))
					pitch = Double.valueOf(tpoint.get("pitch").toString()).doubleValue();
				if(tpoint.containsKey("roll"))
					roll = Double.valueOf(tpoint.get("roll").toString()).doubleValue();				
				if(tpoint.containsKey("battery_info"))
					battery_info = tpoint.get("battery_info").toString();				
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
		    	}else{
		    		user.setCheckin(false);
		    	}
		    	
				Transaction tx = null;
				try {
					tx = session.beginTransaction();
					session.persist(user);
					tx.commit();
				} catch (HibernateException he) {
		    		he.printStackTrace();
		    		if(tx!=null){  
		    			tx.rollback();  
		    	    }  
		    	}
				tid = user.getId();					
			
				if(tpoint.containsKey("CheckIn")) {
					JSONObject checkindata = (JSONObject) tpoint.get("CheckIn");
					T_CheckInInfo CData = new T_CheckInInfo();
					CData.setId(tid);
					if((checkindata.containsKey("message")) && (checkindata.get("message") != null))
						CData.setMessage(checkindata.get("message").toString());
					if((checkindata.containsKey("emotion")) && (checkindata.get("emotion") != null))
						CData.setEmotion(Integer.parseInt(checkindata.get("emotion").toString()));
					if((checkindata.containsKey("picture_uri")) && (checkindata.get("picture_uri") != null))
						CData.setPicture_uri(checkindata.get("picture_uri").toString());
					
					try {
						tx = session.beginTransaction();
						session.save(CData);
						tx.commit();
					} catch (HibernateException he) {
		    			he.printStackTrace();
		    			if(tx!=null){  
		    				tx.rollback();  
		    	        }  
		    		}
				}
			}
			
			map.put("message", "Ok");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		session.close();
	    System.out.println("UploadTripComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
	    	    
	    return map;
	}
}