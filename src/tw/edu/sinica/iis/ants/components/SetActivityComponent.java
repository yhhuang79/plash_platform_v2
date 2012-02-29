package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import tw.edu.sinica.iis.ants.DB.T_Activity;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
/**
 * @goal      create activity
 * @author    Kenny
 * @version   1, 05/13/2011
 * @param     userid, name, timestamp, lat, lng, image
 * @return    message 
 * 
 */
public class SetActivityComponent extends PLASHComponent {

	@Override
	public Object serviceMain(Map map) {    	
        System.out.println("SetActivityComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here

        if(!map.containsKey("userid") || !map.containsKey("name") || !map.containsKey("timestamp") || !map.containsKey("lat") || !map.containsKey("lng")){
        	map.put("message", "Lacking of parameters");
        	return map;
        }
        Session session = sessionFactory.openSession();
        Integer userid = null;
        String name = null;
        Timestamp timestamp = null;
        Double lat = null, lng = null;
        String image = null;

        if (map.containsKey("userid")) 
        	userid = Integer.parseInt(map.get("userid").toString());
        if (map.containsKey("name")) 
        	name = map.get("name").toString();
        if (map.containsKey("timestamp")) 
        	timestamp = Timestamp.valueOf(map.get("timestamp").toString());
        if (map.containsKey("lat")) 
        	lat = Double.valueOf(map.get("lat").toString()).doubleValue();
        if (map.containsKey("lng"))   
        	lng = Double.valueOf(map.get("lng").toString()).doubleValue();
        if (map.containsKey("image")) 
        	image = map.get("image").toString();
        else
        	image = "";
        
        if (userid.equals("") || name.equals("") || timestamp.equals("") || lat.equals("") || lng.equals("")) {
			map.put("message", "Required information is empty");
		} else {   				
				
				//store the <userid> into the database
				T_Activity activity = new T_Activity();
				activity.setUserid(userid);
				activity.setName(name);
				activity.setTimestamp(timestamp);
				
				WKTReader fromText = new WKTReader();
	            Geometry gps = null;
				try {
					gps = fromText.read("POINT("+lng+" "+lat+")");
					gps.setSRID(4326);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				activity.setGps(gps);
				activity.setImage(image);
				
				Transaction tx = session.beginTransaction();
				session.save(activity);
				tx.commit();
				
				map.put("message", "Success creating an activity"); 
			}
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("SetActivityComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	}
	
}
