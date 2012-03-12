package tw.edu.sinica.iis.ants.components;


import java.util.*;



import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;



import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.componentbase.*;

/**
 * Get trip authorization data component <br>
 * This service enables users to query various trip authorization information
 * 
 * @author	Yi-Chun Teng 
 * @version 1.3
 * @param   a map that contains the following keys: <br>
 * 			userid - required. This indicates which user you are referring to <br>
 * 			friend_id - optional. When provided, this component returns a list of trip_ids that belongs to the user and this friend <br>
 * 			trip_id - optional. When provided, this component returns a list of friends that the user has shared the trip specified with <br> 
 *			If neither friend_id nor trip_id is provided, then the component will return a list of trip ids where the key of trip id indicate friend id.
 * @return  map containing a list of IDs. empty list if no ID found
 * @example	https://localhost:8080/GetTripAuthDataComponent?userid=1&friend_id=2
 * 			https://localhost:8080/GetTripAuthDataComponent?userid=1&trip_id=172
 */
public class GetTripAuthDataComponent extends PLASHComponent {

	private Session tskSession; //task session

	
    public GetTripAuthDataComponent() {
		super();	
		//enableDebugLog();
    }//end constructor

	@Override
	public Object serviceMain(Map map) {

		tskSession = sessionFactory.openSession(); 


        int userid, trip_id, friend_id;	
		String tmpUserid, tmpTrip_id, tmpFriend_id;
		
		try {
			

			if ((tmpUserid = (String)map.remove("userid")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "User id must be specified";
				return returnUnsuccess(map,err);
			} else {
				userid = Integer.parseInt(tmpUserid);
			}//fi

			
			if ((tmpTrip_id = (String)map.remove("trip_id")) != null) {					
				trip_id = Integer.parseInt(tmpTrip_id);
				map.put("friendList", getAuthFriendID(userid, trip_id));
			} else {
				trip_id = -1;
			}//fi

			if ((tmpFriend_id = (String)map.remove("friend_id")) != null) {				
				friend_id = Integer.parseInt(tmpFriend_id);
				map.put("tripList", getAuthTripID(userid, friend_id));
			} else {
				friend_id = -1;
			}//fi
						
			
			if(trip_id < 0 && friend_id < 0) { //no friend id nor trip id is provided				
				getTripAuthData(userid,map);
			}//fi

   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("getAuthFriend",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthFriendComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("getAuthFriend",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthFriendComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		
        return map;
	} //end method greet

	/**
	 * This method returns trip authorization data of a user<br>
	 * Example:	GetTripAuthData(123);
	 * 
	 * @author	Yi-Chun Teng 
	 * @param	userid - This indicates which user you are referring to <br>
	 * @param	mapRef - reference to the hash map object that will be used to store processed results <br> 
	 * @return  void. The map object passed will contain lists of trip IDs. The key of each trip id list is the ID of the friend that the user shared this trip with. 
	 * 
	 */
	private void getTripAuthData(int userid, Map mapRef){	
        Criteria criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
        criteriaFriendAuth.add(Restrictions.eq("userAID", userid));
        

		Iterator<T_FriendAuth> rli = criteriaFriendAuth.list().iterator(); //result list iterator
		int current_friend_id = -1;
		T_FriendAuth tmpEntry;
		while(rli.hasNext()) {
			tmpEntry = rli.next();
			if (tmpEntry.getUserBID() != current_friend_id) {
				current_friend_id = tmpEntry.getUserBID();
				mapRef.put(current_friend_id, new ArrayList<Integer>());
			}//fi
			((ArrayList<Integer>)mapRef.get(current_friend_id)).add(tmpEntry.getTripID());			
		}//end while
        
	}//end method

	/**
	 * This method returns authorized trip ids
	 * 
	 * Example:	GetAuthTripID(123,456);
	 * 
	 * @author	Yi-Chun Teng 
	 * @param	userid - This indicates which user you are referring to
	 * @param	friend_id - friend's user id. 
	 * @return  A list of trip_ids that belongs to the user and this friend 
	 * 
	 */
	private ArrayList<Integer> getAuthTripID(int userid, int friend_id){	
		
		try {
		    Criteria criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
		    criteriaFriendAuth.add(Restrictions.eq("userAID", userid));
		    criteriaFriendAuth.add(Restrictions.eq("userBID", friend_id));        
		    criteriaFriendAuth.setProjection(Projections.property("tripID"));
			
		    return (ArrayList<Integer>)criteriaFriendAuth.list();
		} catch (Exception e) {
			System.out.println("exception in getAuthTripID: " + e.toString());
			return null;
		}//end try
	}//end method

	/**
	 * This method returns a list of friends that the user has shared the trip with
	 * 
	 * Example:	GetTripAuthData(123,456);
	 * 
	 * @author	Yi-Chun Teng 
	 * @param	userid - This indicates which user you are referring to
	 * @param	trip_id - The trip that the user has shared 
	 * @return  A list of user ids belong to the people the user has shared trip with 
	 * 
	 */
	private ArrayList<Integer> getAuthFriendID(int userid, int friend_id){	
		
		try {
		    Criteria criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
		    criteriaFriendAuth.add(Restrictions.eq("userAID", userid));
		    criteriaFriendAuth.add(Restrictions.eq("tripID", friend_id));        
		    criteriaFriendAuth.setProjection(Projections.property("userBID"));
			
		    return (ArrayList<Integer>)criteriaFriendAuth.list();
		} catch (Exception e) {
			System.out.println("exception in getAuthFriendID: " + e.toString());
			return null;
		}//end try
	}//end method
	
}//end class 
