package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Login;

public class TestComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public TestComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Please Implement Your Programming Logic From Here  
		 */
		/*
		
		Session session = sessionFactory.openSession(); 

		Criteria criteria = session.createCriteria(T_Login.class);
		criteria.add(Restrictions.eq("username", map.remove("username").toString()));
		criteria.add(Restrictions.eq("password", map.remove("password").toString()));
		Iterator users = criteria.list().iterator(); 
		if(users.hasNext()) {
			T_Login user = (T_Login) users.next(); 
			map.put("sid", user.getSid()); 
		}
		else{
			map.put("sid", "fail");	        	
		}
 
		session.close();
		*/

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Test End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
