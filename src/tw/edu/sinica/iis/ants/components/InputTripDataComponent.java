package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.hibernate.*;


import tw.edu.sinica.iis.ants.DB.T_CheckInInfo;
import tw.edu.sinica.iis.ants.DB.T_TripData;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 *
 * This service component receives information associated with a trip point and stores it on the appropriate place of database. <br>
 * 
 * This component takes a Map object that contains the following keys: <br>
 * userid : Required. An int value that indicates which user this point belongs to <br>
 * trip_id: Required. An int value that indicates the trip id <br>
 * lat: Required. A double value that indicates this point's latitude <br>
 * lng: Required. A double value indicating this point's longitude <br>
 * 				
 * Optional arguments: Available arguments are as follows: <br>
 * 	1. timestamp <br>
 * 	2. gps <br>
 * 	3. server_timestamp <br>
 * 	4. trip_id <br>
 * 	5. label <br>
 * 	6. alt <br>
 * 	7. accu <br>
 * 	8. spd <br>
 * 	9. bear <br>
 * 	10. accex <br>
 * 	11. accey <br>
 * 	12. accez <br>
 * 	13. gsminfo <br>
 * 	14. wifiinfo <br>
 *  15. app <br>
 *  16. checkin <br> 
 * 
 *  
 * @author  Angus Fuming Huang, Yi-Chun Teng, Yu-Hsiang Huang
 * @param	map A map object that contains userid, trip_id, update_status and any of the items listed above 
 *
 */
public class InputTripDataComponent extends PLASHComponent {
	


	private Session tskSession; //task session



    public InputTripDataComponent() {

    }//end constructor


	@Override
	public Object serviceMain(Map map) {
    	
        System.out.println("inputGpsLocationComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here

        
        tskSession = sessionFactory.openSession();
        Integer userid = null;
        Integer trip_id = null;

        
        Integer label = null;
        Double lat = 0.0, lng = 0.0;

                
        Double alt = 0.0;  	//Altitude in meter
        Double accu = 0.0; 	//Accuracy in meter
        Double spd = 0.0;	//Speed in m/s
        Double bear = 0.0;	//Bearing
        String accex = null;	//x-axis acceleration
        String accey = null;	//y-axis acceleration
        String accez= null; 	//z-axis acceleration
        String gsminfo = ""; //first 5 is phone information, last 3 is the nearest cell information, NULL if information is missing
        String wifiinfo = ""; // set of AP info: MAC address, frequency (MHz), signal strength (dBM)
        Integer app = 0;
    	Double azimuth = 0.0;
    	Double pitch = 0.0;
    	Double roll = 0.0;
    	String battery_info = "";        
    	Boolean checkin = false;
        
        Timestamp timestamp = null;
        
        if (map.containsKey("userid")) 
        	userid = Integer.parseInt(map.get("userid").toString());
        if (map.containsKey("trip_id"))   
        	trip_id = Integer.parseInt(map.get("trip_id").toString());
        if (map.containsKey("lat")) 
        	lat = Double.valueOf(map.get("lat").toString()).doubleValue();
        if (map.containsKey("lng"))   
        	lng = Double.valueOf(map.get("lng").toString()).doubleValue();
        // end
        
        // add by Danny (
        if (map.containsKey("alt"))
        	alt = Double.valueOf(map.get("alt").toString()).doubleValue();
        if (map.containsKey("accu"))
        	accu = Double.valueOf(map.get("accu").toString()).doubleValue();
        if (map.containsKey("spd"))
        	spd = Double.valueOf(map.get("spd").toString()).doubleValue();
        if (map.containsKey("bear"))
        	bear = Double.valueOf(map.get("bear").toString()).doubleValue();
        if (map.containsKey("accex"))
        	accex = map.get("accex").toString();
        if (map.containsKey("accey"))
        	accey = map.get("accey").toString();
        if (map.containsKey("accez"))
        	accez = map.get("accez").toString();
        if (map.containsKey("gsminfo"))
        	gsminfo = map.get("gsminfo").toString();
        if (map.containsKey("wifiinfo"))
        	wifiinfo = map.get("wifiinfo").toString();
        if (map.containsKey("app"))
        	app = Integer.parseInt(map.get("app").toString());
        if (map.containsKey("azimuth"))
        	azimuth = Double.valueOf(map.get("azimuth").toString()).doubleValue();
        if (map.containsKey("pitch"))
        	pitch = Double.valueOf(map.get("pitch").toString()).doubleValue();
        if (map.containsKey("roll"))
        	roll = Double.valueOf(map.get("roll").toString()).doubleValue();
        if (map.containsKey("battery_info"))
        	battery_info = map.get("battery_info").toString();        	
        if (map.containsKey("timestamp")) 
        	timestamp = Timestamp.valueOf(map.get("timestamp").toString());
        if (map.containsKey("checkin")) 
        	checkin = Boolean.valueOf(map.get("checkin").toString());
        
        if (userid.equals("") || trip_id.equals("") || timestamp.equals("") || lat.equals("") || lng.equals("")) {  //簡化的判斷式
			map.put("message", "required information is empty and can not input the GPS location");
		} else {   
				label = 0; //let old version can work as well, 0 means no mentioned
				if(map.get("label") != null)
				    label = Integer.parseInt(map.get("label").toString());
				
				
				//store the <userid> into the database
				T_TripData pt = new T_TripData();
				pt.setUserid(userid);
				
				//store the <trip_id> into the database
				pt.setTrip_id(trip_id);
				
				//store the <timestamp> into the database
				pt.setTimestamp(timestamp);
				
				//store the <gps> into the database
				// Add by Yu-Hsiang Huang
				WKTReader fromText = new WKTReader();
	            Geometry gps = null;
				try {
					gps = fromText.read("POINT("+lng+" "+lat+")");
					gps.setSRID(4326);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pt.setGps(gps);	
				// end
				
				//get the value of <server_timestamp>, and store it into the database
				pt.setServer_timestamp(new Timestamp(new Date().getTime()));
				
				//store the <label> into the database
				pt.setLabel(label);
				

				pt.setAlt(alt);
				pt.setAccu(accu);
				pt.setSpd(spd);
				pt.setBear(bear);
				pt.setAccex(accex);
				pt.setAccey(accey);
				pt.setAccez(accez);
				pt.setGsminfo(gsminfo);
				pt.setWifiinfo(wifiinfo);
				pt.setLatitude(lat);
				pt.setLongitude(lng);
				pt.setApp(app);		        
				pt.setAzimuth(azimuth);
				pt.setPitch(pitch);
				pt.setRoll(roll);
				pt.setBattery_info(battery_info);   
				pt.setCheckin(checkin);
				
				Transaction tx = tskSession.beginTransaction();
				//tskSession.save(pt);
				tskSession.persist(pt);
				tx.commit();
				
				
				if(checkin){
					Integer tid = pt.getId();
					T_CheckInInfo CData = new T_CheckInInfo();
					CData.setId(tid);
					if((map.containsKey("message")) && (map.get("message") != null))
						CData.setMessage(map.get("message").toString());
					if((map.containsKey("emotion")) && (map.get("emotion") != null))
						CData.setEmotion(Integer.parseInt(map.get("emotion").toString()));
					if((map.containsKey("picture_uri")) && (map.get("picture_uri") != null))
						CData.setPicture_uri(map.get("picture_uri").toString());
					tx = tskSession.beginTransaction();
					tskSession.save(CData);
					tx.commit();
					map.put("result", "Success in GPS location input with CheckIn Data"); 

				} else {				
					map.put("message", "Success in GPS location input"); 
				}
			}//fi
        
        tskSession.close();
        return map;
	} //end serviceMain
    
}  //close class
