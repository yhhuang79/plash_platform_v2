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

public class getUserTripIdComponent {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public getUserTripIdComponent() {

    }

    public Object greet(Map map) {
        System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
        /*
         * Please Implement Your Programming Logic From Here 
         */
        
        /**
         * return whole trip ids for a user
         * 
         * @author Yu-Hsiang Huang 
         * @version 1.0, 01/10/2010
         * @param     userid 
         * @return    tripid_list 
         * @see       connpost.java
         */

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
			List tripid_list = new ArrayList();
			Iterator tripids = criteria.list().iterator();
			while(tripids.hasNext()) {
				T_UserPointLocationTime tripid = (T_UserPointLocationTime) tripids.next();
				tripid_list.add(tripid.getTrip_id());
			}
 
			map.put("tripid_list", tripid_list);
		}
        
        session.close();
       
        /*
         * End of Programming Logic Implementation
         */
        System.out.println("Test End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    }
}