package tw.edu.sinica.iis.ants.components;


import java.util.*;



import org.hibernate.*;
import org.hibernate.criterion.Restrictions;



import tw.edu.sinica.iis.ants.DB.T_FriendAuth;

/**
 * get a list of authorized friends
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param   a map that contains the following keys: <br>
 * 			userid - required. This indicates which user you are referring to <br>
 * 			friend_id - optional. When provided, this component returns a list of trip_ids that belongs to the user and this friend <br>
 * 			trip_id - optional. When provided, this component returns a list of friends that the user has shared this trip with <br> 
 *			If neigher friend_id nor trip_id is provided, then the component will return a list of trip ids where the key of trip id indicate friend id.
 * @return  map containing a list of IDs. empty list if no ID found
 * @example	http://localhost:1234/GetAuthFriend?userid=1&tripid=555       
 * @note	Follow the algorithm implemented in the original server
 */
public class GetAuthFriendComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public GetAuthFriendComponent() {

    }//end constructor

    public Object greet(Map map) {
    	
        System.out.println("getAuthFriendComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());


        Session session = sessionFactory.openSession(); 
        Criteria criteria = session.createCriteria(T_FriendAuth.class);
        
		try {			
			criteria.add(Restrictions.eq("userAID", Integer.parseInt(map.get("userid").toString())));
			criteria.add(Restrictions.eq("tripID", Integer.parseInt(map.get("tripid").toString())));	
   			
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
		
		Iterator fls = criteria.list().iterator();
		List resultList = new ArrayList();

		while(fls.hasNext()) {
			resultList.add(((T_FriendAuth)fls.next()).getUserBID());			
		}//end while
        map.put("getAuthFriend", resultList);
        
        System.out.println("getAuthFriendComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	} //end method greet
    
}//end class 
