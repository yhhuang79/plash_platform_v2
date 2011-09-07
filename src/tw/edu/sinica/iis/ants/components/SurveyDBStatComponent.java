package tw.edu.sinica.iis.ants.components;

import java.util.*;



import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.*;




/**
 * get a list of authorized friends
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param   a map that contains the following keys: userid,tripid
 * @return  map containing a list of IDs. empty list if no ID found
 * @example	http://localhost:1234/in?userid=1&tripid=555       
 * @note	Follow the algorithm implemented in the original server
 */
public class SurveyDBStatComponent extends PLASHComponent {
	
	Session session;



    public SurveyDBStatComponent() {

    }//end constructor

    public Object serviceMethod(Map map) {
        session = sessionFactory.openSession(); 
        System.out.println("getAuthFriendComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());

        System.out.println("# of trips " + ProbeNumOfGPSPts());

        map.put("SurveyDBStatComponent", true);
        
        System.out.println("getAuthFriendComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	} //end method greet
    
    //obtain number of users based on # of accounts
    private int ProbeNumOfUsersByLoginID() {

        Criteria criteria = session.createCriteria(T_Login.class);    
		criteria.setProjection(Projections.rowCount());
			
		return (Integer)criteria.list().get(0);
    }//end method
    
    
    //obtain number of users based on # of unique e-mails
    private int ProbeNumOfUsersByEMail() {

        Criteria criteria = session.createCriteria(T_Login.class);  
		criteria.setProjection(Projections.distinct(Projections.countDistinct("email")));
			
		return (Integer)criteria.list().get(0);
    }//end method
    
    //Obtain number of trips
    private int ProbeNumOfTrips() {

        Criteria criteria = session.createCriteria(T_UserPointLocationTime.class);        
		
        criteria.setProjection(Projections.distinct(Projections.property("userid")));
		
		Iterator userIDs = criteria.list().iterator();
		int tmpSum = 0;
		while (userIDs.hasNext()) {
			criteria = session.createCriteria(T_UserPointLocationTime.class);   
			criteria.add(Restrictions.eq("userid", userIDs.next()));
			criteria.setProjection(Projections.distinct(Projections.property("trip_id")));
			tmpSum += (Integer)criteria.list().get(0);						
		}//end while
			
		return tmpSum;
    }//end method    
    
    //Obtain number of GPS points recorded
    private int ProbeNumOfGPSPts() {
        Criteria criteria = session.createCriteria(T_UserPointLocationTime.class);    
		criteria.setProjection(Projections.rowCount());
			
		return (Integer)criteria.list().get(0);    	
    }//end method
   
    
}//end class
