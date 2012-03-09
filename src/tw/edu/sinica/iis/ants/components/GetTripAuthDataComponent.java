package tw.edu.sinica.iis.ants.components;


import java.util.*;



import org.hibernate.*;
import org.hibernate.criterion.Restrictions;



import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.componentbase.*;

/**
 * Query various trip authorization information
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param   a map that contains the following keys: <br>
 * 			userid - required. This indicates which user you are referring to <br>
 * 			friend_id - optional. When provided, this component returns a list of trip_ids that belongs to the user and this friend <br>
 * 			trip_id - optional. When provided, this component returns a list of friends that the user has shared the trip specified with <br> 
 *			If neither friend_id nor trip_id is provided, then the component will return a list of trip ids where the key of trip id indicate friend id.
 * @return  map containing a list of IDs. empty list if no ID found
 * @example	http://localhost:1234/GetAuthFriend?userid=1&tripid=555       
 * @note	Follow the algorithm implemented in the original server
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
        int num_of_trip_ids, num_of_friend_ids; 
        StringTokenizer ids_st;        
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
			} else {
				trip_id = -1;
			}//fi

			if ((tmpFriend_id = (String)map.remove("friend_id")) != null) {				
				friend_id = Integer.parseInt(tmpFriend_id);
			} else {
				friend_id = -1;
			}//fi
						
			
			if(trip_id < 0 && friend_id < 0) { //no friend id nor trip id is provided
				
	
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
	 * @param	userid - required. This indicates which user you are referring to <br>
	 * @return  map containing lists of trip IDs. The key of each trip id list is the ID of the friend that the user shared this trip with. 
	 * 
	 */
	private Map GetTripAuthData(int userid){	
		HashMap resultMap = new HashMap();
        Criteria criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
        criteriaFriendAuth.add(Restrictions.eq("userAID", userid));
        

		Iterator fls = criteriaFriendAuth.list().iterator();
		List resultList = new ArrayList();

		while(fls.hasNext()) {
			resultList.add(((T_FriendAuth)fls.next()).getUserBID());			
		}//end while
        
        System.out.println("getAuthFriendComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        
        
		return resultMap;
	}//end method
    
}//end class 
