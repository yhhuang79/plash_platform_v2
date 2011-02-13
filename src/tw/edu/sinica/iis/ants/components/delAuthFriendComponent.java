package tw.edu.sinica.iis.ants.components;

import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;

/**
 * Delete an authorized friend
 * 
 * @author	Yi-Chun Teng 
 * @param   a map that contains the following keys: userid,friendid,tripid
 * @return  operation result
 * @example	http://localhost:1234/in?userid=1&tripid=55555&friendid=55       
 * @note	Follow the algorithm implemented in the original server
 */

public class delAuthFriendComponent {

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public delAuthFriendComponent() {

    }

    public Object greet(Map map) {
    	
        System.out.println("delAuthFriendComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        
 
        
        Session session = sessionFactory.openSession(); 
        Criteria criteria = session.createCriteria(T_FriendAuth.class);
		       
        
		try {
			criteria.add(Restrictions.eq("userAID", Integer.parseInt(map.get("userid").toString())));
			criteria.add(Restrictions.eq("tripID", Integer.parseInt(map.get("tripid").toString())));	
			criteria.add(Restrictions.eq("userBID", Integer.parseInt(map.get("friendid").toString())));		

			Iterator fls = criteria.list().iterator();
			session.delete((T_FriendAuth)fls.next());
			session.beginTransaction().commit();       
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("delAuthFriend",false); //result flag, flag name to be unified
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("delAuthFriendComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("delAuthFriend",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("delAuthFriendComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;			
		} catch (NoSuchElementException e) { //Element not found
			map.put("delAuthFriend",false); //result flag, flag name to be unified
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("delAuthFriendComponent failure end3:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;			
		}//end try catch              

	
		map.put("delAuthFriend",true); 
        System.out.println("delAuthFriendComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
		
       

	} //close Object greet
    
}  //close class inputGpsLocationComponent
