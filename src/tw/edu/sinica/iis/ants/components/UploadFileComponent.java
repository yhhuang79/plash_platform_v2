package tw.edu.sinica.iis.ants.components;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import edu.emory.mathcs.backport.java.util.Collections;

import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;

public class UploadFileComponent {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public UploadFileComponent() {

    }

    public Object greet(InputStream is) {
        //System.out.println("UploadFileComponent !!!!!!!!!!!!!!!!!!!!:\t"+ str);
    	return is;
    	
    }
    
    public Object greet(String str) {
        String result = "";
        try {
			System.out.println("UploadFileComponent !!!!!!!!!!!!!!!!!!!!:\t"+ URLDecoder.decode(str, "UTF-8"));
	        System.out.println("UploadFileComponent !!!!!!!!!!!!!!!!!!!!??");
	        String[] tmp = URLDecoder.decode(str, "UTF-8").replace("note=", "").replace("\\n","!!").split("!!");
	        for(int i=0; i<tmp.length; i++){
	            System.out.println(tmp[i]);
	            //result += tmp[i]+"<br/>";
	            result += tmp[i]+"";
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//result = "<html><head></head><body>"+result+"</body></html>";
		
		
		
		
        //"Content-Disposition: form-data; name=\"note\""
/*
        Session session = sessionFactory.openSession();
        Integer userid = null;
        Integer trip_id = null;
        Integer label = null;
        Double lat = null, lng = null;
        Timestamp timestamp = null;
        
						
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
								
				Transaction tx = session.beginTransaction();
				session.save(user);
				tx.commit();
				
        session.close();
        //End of Programming Logic Implementation
        System.out.println("inputGpsLocationComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
*/   
		return result;
    	
    }

    public Object greet(Map map) {
        System.out.println("UploadFileComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        /*
         * Please Implement Your Programming Logic From Here 
         */
        
        /**
         * Upload file
         * 
         * @author Kenny
         * @version 1.0, 08/04/2011
         * @param     ? 
         * @return    ? 
         */
        
		Iterator i = map.keySet().iterator();
		while(i.hasNext()){
			String a = i.next().toString();
	        System.out.println("UploadFileComponent !!!!!!!!!!!!!!!!!!!!:\t"+ a);
		}
			
		if(true)
			return map;
		


		Session session = sessionFactory.openSession();
        Integer userid = null;
        
        if (map.containsKey("userid")) {
			userid = Integer.parseInt(map.get("userid").toString());
		}
        
        if (userid.equals("")) {
			map.put("message", "userid is empty");
		} else {   
			Criteria criteria = session.createCriteria(T_UserPointLocationTime.class);
			criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString())));
			//List tripid_list = new ArrayList();
			Set tripid_list = new TreeSet(Collections.reverseOrder());
			Iterator tripids = criteria.list().iterator();
			while(tripids.hasNext()) {
				T_UserPointLocationTime tripid = (T_UserPointLocationTime) tripids.next();
				tripid_list.add(tripid.getTrip_id());
			}
			
			//transform into List (cause MULE doesn't allow Set)
			List tmpList = new ArrayList(tripid_list);
 
			map.put("tripid_list", tmpList);
		}
        
        session.close();
       
        /*
         * End of Programming Logic Implementation
         */
        System.out.println("UploadFileComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    }
}