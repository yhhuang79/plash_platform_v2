package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_ActivityUser;
import tw.edu.sinica.iis.ants.DB.T_FriendAuth;

public class DelAttendActivityComponent extends PLASHComponent {

	@Override
	public Object serviceMain(Map map) {
        if(!map.containsKey("activityid") || !map.containsKey("userid") || map.get("activityid").toString().equals("") || map.get("userid").toString().equals("")){
        	map.put("message", "Lacking of parameters or required information");
        	return map;        
        }

    	
        System.out.println("DelAttendActivityComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        
 
        
        Session session = sessionFactory.openSession(); 
        Criteria criteria = session.createCriteria(T_ActivityUser.class);
		       
        
		try {
			criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString())));
			criteria.add(Restrictions.eq("activityid", Integer.parseInt(map.get("activityid").toString())));	

			Iterator fls = criteria.list().iterator();
			session.delete((T_ActivityUser)fls.next());
			session.beginTransaction().commit();       
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("DelAttendActivityComponent",false); //result flag, flag name to be unified
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("DelAttendActivityComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("DelAttendActivityComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("DelAttendActivityComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;			
		} catch (NoSuchElementException e) { //Element not found
			map.put("DelAttendActivityComponent",false); //result flag, flag name to be unified
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("DelAttendActivityComponent failure end3:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;			
		}//end try catch              

	
		map.put("DelAttendActivityComponent",true); 
        System.out.println("DelAttendActivityComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	}
	
}
