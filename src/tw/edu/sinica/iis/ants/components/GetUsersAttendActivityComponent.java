package tw.edu.sinica.iis.ants.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Activity;
import tw.edu.sinica.iis.ants.DB.T_ActivityUser;
import tw.edu.sinica.iis.ants.DB.T_Item;

public class GetUsersAttendActivityComponent extends PLASHComponent {

	@Override
	public Object theMainLogic(Map map) {
        if(!map.containsKey("activityid") || map.get("activityid").toString().equals("")){
        	map.put("message", "Lacking of parameters or required information");
        	return map;        
        }

		System.out.println("GetUsersAttendActivityComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession(); 

		Criteria criteria = session.createCriteria(T_ActivityUser.class);
		
		criteria.add(Restrictions.eq("activityid", Integer.parseInt(map.get("activityid").toString())));
		
		List theResultList = criteria.list();
		
		List people = null;
		
		if(theResultList.size()<1){
			map.put("message", "No user in this activity");
		}
		else{
			people = new ArrayList();
			Iterator i = theResultList.iterator();
			while(i.hasNext()){
				T_ActivityUser au = (T_ActivityUser) i.next();
				people.add(au.getUserid());
			}
			map.put("people", people);
		}
		
		session.close();

		System.out.println("GetUsersAttendActivityComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
	
}
