package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @goal      input gps location
 * @author    Angus Fuming Huang, Yi-Chun Teng
 * @version   1.2, 01/5/2012
 * @param     
 * @return    message 
 * @see       connpost.java
 * @example   http://localhost:1234/in?userid=1&trip_id=500&timestamp=2011-11-11 11:11:11.111
 * 
 */
public class InputComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public InputComponent() {

    }


    public Object greet(Map map) {
    	
        System.out.println("inputGpsLocationComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here

        
        Session session = sessionFactory.openSession();
        Integer userid = null;
        Integer trip_id = null;

        
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
        	accex = Double.valueOf(map.get("accex").toString()).doubleValue();
        if (map.containsKey("accey"))
        	accey = Double.valueOf(map.get("accey").toString()).doubleValue();
        if (map.containsKey("accez"))
        	accez = Double.valueOf(map.get("accez").toString()).doubleValue();
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
        
        if (userid.equals("") || trip_id.equals("") || timestamp.equals("") || lat.equals("") || lng.equals("")) {  //簡化的判斷式
			map.put("message", "required information is empty and can not input the GPS location");
		} else {   
				label = 0; //let old version can work as well, 0 means no mentioned
				if(map.get("label") != null)
				    label = Integer.parseInt(map.get("label").toString());
				
				
				//store the <userid> into the database
				T_UserPointLocationTime user = new T_UserPointLocationTime();
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
					gps.setSRID(4326);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
				
				Transaction tx = session.beginTransaction();
				session.save(user);
				tx.commit();
				
				map.put("message", "Success in GPS location input"); 
			}
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("inputGpsLocationComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	} //close Object greet
    
}  //close class inputGpsLocationComponent
