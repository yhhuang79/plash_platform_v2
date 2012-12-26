package tw.edu.sinica.iis.ants.components;


import java.util.*;



import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;



import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.componentbase.*;
import tw.edu.sinica.iis.ants.db.antrip.TripSharing;
import tw.edu.sinica.iis.ants.db.antrip.TripSharingId;

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
 * @example	https://localhost:8080/GetTripAuthDataComponent?userid=1
 * 			https://localhost:8080/GetTripAuthDataComponent?userid=1&friend_id=2
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
				map.put("tripInfo", getAuthTripInfo(userid, friend_id));
			} else {
				friend_id = -1;
			}//fi
						
			
			if(trip_id < 0 && friend_id < 0) { //no friend id nor trip id is provided				
				getTripAuthData(userid,map);
			}//fi

   			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			getElapsed();
	        tskSession.close();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 003;
	        err.explaination = "NullPointerException occured, probably due to invalid argument";
			return returnUnsuccess(map,err);					
	
		} catch (NumberFormatException e) { //invalid arguments 

			getElapsed();
	        tskSession.close();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 003;
	        err.explaination = "NumberFormatException occured, probably due to invalid argument";
			return returnUnsuccess(map,err);			
		}//end try catch
		
        return 	returnSuccess(map);
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
        
        Criteria criteriaTripSharing = tskSession.createCriteria(TripSharing.class);
        criteriaTripSharing.add(Restrictions.eq("id.userId", userid));          
        ArrayList<TripSharing> ral = (ArrayList<TripSharing>) criteriaTripSharing.list(); //result list
        int current_friend_id;
        for (TripSharing ts : ral) {
        	current_friend_id = ts.getId().getUserIdFriend();
        	if (!mapRef.containsKey(current_friend_id)) {        				
        		mapRef.put(current_friend_id, new ArrayList<Integer>());
			}//fi        	
        	((ArrayList<Integer>)mapRef.get(current_friend_id)).add(ts.getId().getTripId());		
        }//rof
        
        /*
        ArrayList<T_FriendAuth> rli = (ArrayList<T_FriendAuth>)criteriaFriendAuth.list(); //result list iterator

		for (T_FriendAuth tmpEntry :rli) {
			current_friend_id = tmpEntry.getUserBID();
        	if (!mapRef.containsKey(current_friend_id)) {        				
        		mapRef.put(current_friend_id, new ArrayList<Integer>());
			}//fi           
			((ArrayList<Integer>)mapRef.get(current_friend_id)).add(tmpEntry.getTripID());			
		}//end while //*/
        
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
			
	        Criteria criteriaTripSharing = tskSession.createCriteria(TripSharing.class);
	        criteriaTripSharing.add(Restrictions.eq("id.userId", userid));
	        criteriaTripSharing.add(Restrictions.eq("id.userIdFriend", friend_id));
	        criteriaTripSharing.setProjection(Projections.property("id.tripId"));	        
	        return (ArrayList<Integer>)criteriaTripSharing.list(); //*/
			
	        /*
		    Criteria criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
		    criteriaFriendAuth.add(Restrictions.eq("userAID", userid));
		    criteriaFriendAuth.add(Restrictions.eq("userBID", friend_id));        
		    criteriaFriendAuth.setProjection(Projections.property("tripID"));
			
		    return (ArrayList<Integer>)criteriaFriendAuth.list(); //*/
		} catch (HibernateException e) {
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
	private ArrayList<Integer> getAuthFriendID(int userid, int trip_id){	
		
		try {
	        Criteria criteriaTripSharing = tskSession.createCriteria(TripSharing.class);
	        criteriaTripSharing.add(Restrictions.eq("id.userId", userid));
	        criteriaTripSharing.add(Restrictions.eq("id.tripId", trip_id));
	        criteriaTripSharing.setProjection(Projections.property("id.userIdFriend"));
	        
	        return (ArrayList<Integer>)criteriaTripSharing.list(); //*/
	        
			/*
		    Criteria criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
		    criteriaFriendAuth.add(Restrictions.eq("userAID", userid));
		    criteriaFriendAuth.add(Restrictions.eq("tripID", trip_id));        
		    criteriaFriendAuth.setProjection(Projections.property("userBID"));
			
		    return (ArrayList<Integer>)criteriaFriendAuth.list(); //*/
		} catch (HibernateException e) {
			System.out.println("exception in getAuthFriendID: " + e.toString());
			return null;
		}//end try
	}//end method

	/**
	 * This method returns authorized trip infos
	 * 
	 * Example:	GetAuthTripInfo(123,456);
	 * 
	 * @author	Yu-Hsiang Huang 
	 * @param	userid - This indicates which user you are referring to
	 * @param	friend_id - friend's user id. 
	 * @return  A list of trip_info that belongs to the user and this friend 
	 * 
	 */
	private ArrayList<Map> getAuthTripInfo(int userid, int friend_id){	
		
		try {
			
	        Criteria criteriaTripInfo = tskSession.createCriteria(TripSharing.class);
	        criteriaTripInfo.add(Restrictions.eq("id.userId", userid));
	        criteriaTripInfo.add(Restrictions.eq("id.userIdFriend", friend_id));
	        criteriaTripInfo.setProjection(Projections.property("id.tripId"));
	        Iterator tripids = criteriaTripInfo.list().iterator();
	        ArrayList<Map> TripInfoList = new ArrayList<Map>();
	        while(tripids.hasNext()) {
	        	int tripid = Integer.parseInt(tripids.next().toString());
	        	//System.out.println("GetTripInfo end:\t"+ tripid);
	        	Map SingleTripInfo = PlashUtils.getTripInfo(userid, tripid, tskSession);
	        	//System.out.println("GetTripInfo end:\t"+ SingleTripInfo.toString());
	        	TripInfoList.add(SingleTripInfo);
	        }
	        return TripInfoList; //*/
			
	        /*
		    Criteria criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
		    criteriaFriendAuth.add(Restrictions.eq("userAID", userid));
		    criteriaFriendAuth.add(Restrictions.eq("userBID", friend_id));        
		    criteriaFriendAuth.setProjection(Projections.property("tripID"));
			
		    return (ArrayList<Integer>)criteriaFriendAuth.list(); //*/
		} catch (HibernateException e) {
			System.out.println("exception in getAuthTripID: " + e.toString());
			return null;
		}//end try
	}//end method
	
	
}//end class 
