package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Activity;
import tw.edu.sinica.iis.ants.DB.T_ActivityUser;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @goal      set user to attend an activity
 * @author    Kenny
 * @version   1, 05/13/2011
 * @param     userid, activityid
 * @return    message 
 * 
 */

public class SetAttendActivityComponent extends PLASHComponent {

	@Override
	public Object serviceMain(Map map) {
        System.out.println("SetAttendActivityComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here

		if (!map.containsKey("userid") || !map.containsKey("activityid")) {
        	map.put("message", "Lacking of parameters");
        	return map;
        }
        Session session = sessionFactory.openSession();
        Integer userid = null;
        Integer activityid = null;

        if (map.containsKey("userid")) 
        	userid = Integer.parseInt(map.get("userid").toString());
        if (map.containsKey("activityid")) 
        	activityid = Integer.parseInt(map.get("activityid").toString());
        
        if (userid.equals("") || activityid.equals("")) {
			map.put("message", "Required information is empty");
		} else {   				
			
			Criteria c = session.createCriteria(T_ActivityUser.class);
			c.add(Restrictions.or(Restrictions.eq("userid", userid), Restrictions.eq("activityid", activityid)));
			if(c.list().size()>0){
				map.put("message", "already join to this activity"); 				
			}
			else{				
				T_ActivityUser activityUser = new T_ActivityUser();
				activityUser.setUserid(userid);
				activityUser.setActivityid(activityid);
				
				Transaction tx = session.beginTransaction();
				session.save(activityUser);
				tx.commit();
				
				map.put("message", "Success join to this activity"); 
			}
				
		}
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("SetAttendActivityComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	}
	
}
