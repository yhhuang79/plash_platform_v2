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
 * @param   a map that contains the following keys: userid,tripid
 * @return  map containing a list of IDs. empty list if no ID found
 * @example	http://localhost:1234/in?userid=1&tripid=555       
 * @note	Follow the algorithm implemented in the original server
 */
public class getAuthFriendComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public getAuthFriendComponent() {

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
