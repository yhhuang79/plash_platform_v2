package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;

public class GetFriendLatestTripComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
    

    public GetFriendLatestTripComponent() {

    }

    public Object greet(Map map) {
        System.out.println("getFriendOneTrip Start:\t"+ Calendar.getInstance().getTimeInMillis());
        /*
         * Please Implement Your Programming Logic From Here 
         */
        
        /**
         * getFriendLatestTripComponent.java
         * 
         * return last position the friend shared to a certain user
         * 
         * @author Yu-Hsiang Huang 
         * @version 1.0, 01/13/2010
         * @param     userid, friendid
         * @return    trip information
         * @see       connpost.java
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
        
        if (userid.equals("")) {
			map.put("message", "User ID is empty");
        } else if (friendid.equals("")) {
			map.put("message", "Friend ID is empty");
		} else {   
			
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.eq("useraid", userid), Restrictions.eq("userbid", friendid)),
					Restrictions.and(Restrictions.eq("userbid", userid), Restrictions.eq("useraid", friendid))
					));
			
			Iterator flist = criteria.list().iterator();
			String returnResult = "<trace>";
			
			if(flist.hasNext()) {
				Criteria facriteria = session.createCriteria(T_FriendAuth.class);
				facriteria.add(Restrictions.eq("userAID", userid));
				facriteria.add(Restrictions.eq("userBID", friendid));
				facriteria.setProjection(Projections.max("tripID"));				
				Iterator falist = facriteria.list().iterator();
				if(falist.hasNext()) {
					Integer fatripid = Integer.parseInt(falist.next().toString());
					criteria = session.createCriteria(T_UserPointLocationTime.class);

					criteria.add(Restrictions.eq("userid", friendid));
					criteria.add(Restrictions.eq("trip_id", fatripid));
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