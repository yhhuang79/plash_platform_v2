package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;

public class getFriendOneTripComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
    

    public getFriendOneTripComponent() {

    }

    /**
     * getFriendOneTripComponent.java
     * 
     * return last position the friend shared to a certain user of a certain trip
     * 
     * @author Yu-Hsiang Huang 
     * @version 1.1, 01/26/2010
     * @param     userid 
     * @param     friendid
     * @param     tripid
     * @return    trip information
     * @see       connpost.java
     */
    public Object greet(Map map) {
        System.out.println("getFriendOneTrip Start:\t"+ Calendar.getInstance().getTimeInMillis());
        /*
         * Please Implement Your Programming Logic From Here 
         */
        

        Session session = sessionFactory.openSession(); 
        Criteria criteria = session.createCriteria(T_FriendList.class);
        
        Integer userid = null;
        Integer friendid = null;
        Integer tripid = null;
        
        // get input params
        if (map.containsKey("userid")) {
			userid = Integer.parseInt(map.get("userid").toString());
		}
        if (map.containsKey("friendid")) {
        	friendid = Integer.parseInt(map.get("friendid").toString());
		}
        if (map.containsKey("tripid")) {
        	tripid = Integer.parseInt(map.get("tripid").toString());
		}
        
        if (userid.equals("")) {
			map.put("message", "User ID is empty");
        } else if (friendid.equals("")) {
			map.put("message", "Friend ID is empty");
        } else if (tripid.equals("")) {
			map.put("message", "Trip ID is empty");
		} else {   
			
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.eq("useraid", userid), Restrictions.eq("userbid", friendid)),
					Restrictions.and( Restrictions.eq("userbid", userid), Restrictions.eq("useraid", friendid))
					));
			
			Iterator flist = criteria.list().iterator();
			String returnResult = "<trace>";
			
			if(flist.hasNext()) {
				criteria = session.createCriteria(T_FriendAuth.class);
				criteria.add(Restrictions.eq("userAID", userid));
				criteria.add(Restrictions.eq("userBID", friendid));
				criteria.add(Restrictions.eq("tripID", tripid));				
				Iterator falist = criteria.list().iterator();
				if(falist.hasNext()) {
					criteria = session.createCriteria(T_UserPointLocationTime.class);

					criteria.add(Restrictions.eq("userid", friendid));
					criteria.add(Restrictions.eq("trip_id", tripid));
					
					Iterator rslist = criteria.list().iterator();
					
					if(rslist.hasNext()){
						T_UserPointLocationTime trips = (T_UserPointLocationTime) rslist.next();
						returnResult += "<desc>test</desc>";
						returnResult += "<locInfo>";
						
						String lng = Double.toString(trips.getGps().getCoordinate().y);
						String lat = Double.toString(trips.getGps().getCoordinate().x);
						Timestamp ts = trips.getTimestamp();
						returnResult += "<latitude>"+lat+"</latitude><longitude>" + lng + "</longitude><timestamp>" + ts.toString() + "</timestamp>";
						returnResult += "</locInfo>";
					}
					while(rslist.hasNext()){
						T_UserPointLocationTime trips = (T_UserPointLocationTime) rslist.next();
						returnResult+="<locInfo>";
						String lng = Double.toString(trips.getGps().getCoordinate().y);
						String lat = Double.toString(trips.getGps().getCoordinate().x);
						Timestamp ts = trips.getTimestamp();
						returnResult += "<latitude>"+lat+"</latitude><longitude>" + lng + "</longitude><timestamp>" + ts.toString() + "</timestamp>";
						returnResult+="</locInfo>";							
					}						
				}
			}
			
			map.put("trip_list", returnResult);
		}
        
        session.close();
       
        /*
         * End of Programming Logic Implementation
         */
        System.out.println("getFriendOneTrip End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    }
}