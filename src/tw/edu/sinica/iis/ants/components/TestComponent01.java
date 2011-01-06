package tw.edu.sinica.iis.ants.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_FriendRequest;
import tw.edu.sinica.iis.ants.DB.T_Login;

public class TestComponent01 {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public TestComponent01() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Please Implement Your Programming Logic From Here  
		 */
		
		
		Session session = sessionFactory.openSession(); 

		//Login Table
		//Criteria criteria = session.createCriteria(T_Login.class);
		
		//Friendship table
		//Criteria criteria = session.createCriteria(T_FriendList.class);
		
		//Friend Request table
		Criteria criteria = session.createCriteria(T_FriendRequest.class);
		
		
		//Query to database - login table
//		criteria.add(Restrictions.eq("username", map.remove("username").toString()));
//		criteria.add(Restrictions.eq("password", map.remove("password").toString()));

		//Query to friend request table
//		Criterion lhs = Restrictions.eq("useraid", Integer.parseInt(map.remove("useraid").toString()));
//		Criterion rhs = Restrictions.eq("userbid", Integer.parseInt(map.remove("userbid").toString()));
//		criteria.add(Restrictions.and(lhs, rhs));
		
		criteria.add(Restrictions.eq("useraid", Integer.parseInt(map.get("useraid").toString())));
		
	
		
		//Query to friend table
		//criteria.add(Restrictions.or(Restrictions.eq("useraid", Integer.parseInt(map.get("sid").toString())),Restrictions.eq("userbid", Integer.parseInt(map.get("sid").toString()))));
		
//		criteria.add(Restrictions.eq("useraid", map.remove("useraid").toString()));
//		criteria.add(Restrictions.eq("userbid", map.remove("userbid").toString()));
		
		//Collection of name from the query
		//Iterator users = criteria.list().iterator(); 
		
		//Iterator fls = criteria.list().iterator();
		
		//List a = criteria.list();
		
		Iterator rls = criteria.list().iterator();
		
		
		if (rls.hasNext()) {
			T_FriendRequest user = (T_FriendRequest) rls.next(); 
			map.put("fid", user.getFid()); 
			//map.put("friendemail", user.getFriendemail());
			map.put("passcode", user.getPasscode());
		}
		else{
			map.put("fid", "fail");	        	
		}
 
		session.close();
		

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Test End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
