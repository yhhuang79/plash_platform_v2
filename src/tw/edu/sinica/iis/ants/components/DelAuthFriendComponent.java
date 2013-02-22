package tw.edu.sinica.iis.ants.components;

import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db.antrip.TripSharing;

/**
 * Delete an authorized friend. <br>
 * This service component receives a user id, friend id, trip id tuple and searches  antrip.TripSharing database. <br>
 * If such entry is found, then remove that entry as requested. <br>
 * If such entry is absent, then issue an abnormal result and then set error level to be warning. <br>
 * 
 * @author	Yi-Chun Teng 
 * @param   a map that contains the following keys: userid,friendid,tripid
 * @return  operation result
 * @example	https://localhost:8080/DelAuthFriendComponent?userid=1&tripid=55555&friendid=55       
 * @note	Follow the algorithm implemented in the original server
 */

public class DelAuthFriendComponent extends PLASHComponent{

    public DelAuthFriendComponent() {

    }

	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
 
        
		tskSession = sessionFactory.openSession(); 
        Criteria criteria = tskSession.createCriteria(TripSharing.class);
		       
        
		try {
			criteria.add(Restrictions.eq("id.userId", Integer.parseInt(map.get("userid").toString())));
			criteria.add(Restrictions.eq("id.tripId", Integer.parseInt(map.get("tripid").toString())));	
			if (map.containsKey("friendid")) {
				criteria.add(Restrictions.eq("id.userIdFriend", Integer.parseInt(map.get("friendid").toString())));
				TripSharing rec = (TripSharing) criteria.uniqueResult();
				tskSession.delete(rec);
				tskSession.beginTransaction().commit();       

			} else {
				
				ArrayList<TripSharing> tsl = (ArrayList<TripSharing>) criteria.list();
				for (TripSharing rec:tsl) {
					tskSession.delete(rec);
				}//rof			
     
				/*
				Query query = tskSession.createQuery("delete TripSharing where " +
						"id.userId=" + Integer.parseInt(map.get("userid").toString()) +
						" and id.tripId=" + Integer.parseInt(map.get("tripid").toString())
						);
				query.executeUpdate(); //*/
				tskSession.beginTransaction().commit();
			}//fi
					
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
	        tskSession.close();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "NullPointerException occured, probably due to invalid argument";
			return returnUnsuccess(map,err);
		} catch (NumberFormatException e) { //invalid arguments 
	        tskSession.close();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "NumberFormatException occured, probably due to invalid argument";
			return returnUnsuccess(map,err);		
		} catch (NoSuchElementException e) { //Element not found
	        tskSession.close();
	        AbnormalResult war = new AbnormalResult(this,'W');
	        war.refCode = 001;
	        war.explaination = "Warning: NoSuchElementException occured, such element is not found.";
			return returnUnsuccess(map,war);		
		}//end try catch              

		map.clear();
		map.put("code", 200);
		map.put("message", "ok");
        return returnSuccess(map);
		//return map;
       


	}//end method
    
}  //close class inputGpsLocationComponent
