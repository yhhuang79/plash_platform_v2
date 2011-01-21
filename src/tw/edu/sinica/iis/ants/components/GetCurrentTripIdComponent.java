package tw.edu.sinica.iis.ants.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;

public class GetCurrentTripIdComponent {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public GetCurrentTripIdComponent() {

    }

    public Object greet(Map map) {
        System.out.println("CurrentTripId Start:\t"+ Calendar.getInstance().getTimeInMillis());
        /*
         * Please Implement Your Programming Logic From Here 
         */
        
        /**
         * return current trip id
         * 
         * @author    Angus Fuming Huang
         * @version   1.0, 01/21/2011
         * @param     userid 
         * @return    tripid_current 
         * @see       connpost.java
         */

		Session session = sessionFactory.openSession();
        
        Integer userid = null;
        
        if (map.containsKey("userid")) {
			userid = Integer.parseInt(map.get("userid").toString());
		}
        
        if (userid.equals("")) {
			map.put("message", "userid is empty and can not get the current tripid");
		} else {   
			Criteria criteria = session.createCriteria(T_UserPointLocationTime.class); //建立Hibernate的查詢物件，並將session指定到T_UserPointLocationTime.class
			criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString()))); //針對特定userid
			criteria.addOrder(Order.desc("trip_id"));  //將trip_id由大到小排列
			String tripid_current = tripid.getTrip_id(); //建立tripid_current變數，並存取第一筆trip_id
			map.put("tripid_current", tripid_current);  //將tripid_current存到map中
			}
        
        session.close();
       
        /*
         * End of Programming Logic Implementation
         */
        System.out.println("CurrentTripId End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    }
}