package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Login;

public class FacebookRegisterComponent {
	
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public FacebookRegisterComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Facebook Register 
		 * Author: Yu-Hsiang Huang
		 * Date: 6/20/2012
		 * Version:1.0
		 */
		
		Session session = sessionFactory.openSession();
		
		String username = map.get("username").toString();
		String email = map.get("email").toString();
		Integer facebookid = Integer.parseInt(map.get("facebookid").toString());
		
		T_Login user = new T_Login();
		//T_Login user = (T_Login) users.next();
		
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword("");
		user.setPasscode("");
		user.setFacebookid(facebookid);
		user.setConfirmed(true);
		
		Transaction tx = session.beginTransaction();
		session.save(user);
		tx.commit();

		Criteria criteria = session.createCriteria(T_Login.class);
		criteria.add(Restrictions.eq("email", email));
		criteria.add(Restrictions.eq("facebookid", facebookid));
		criteria.add(Restrictions.eq("username", username));
		System.out.println("FacebookRegister Component:  " + username +" : "+ email +" : "+ facebookid);
		Iterator users = criteria.list().iterator(); 
		if(users.hasNext()) {
			user = (T_Login) users.next(); 			
			if (user.isConfirmed()) {
				map.put("sid", user.getSid());
				String message = "Successful FacebookRegister:" + user.getSid();
				map.put("message", message);
			}
		}
		else{
			map.put("sid", "0");
			map.put("message", "Login Fail");
		}
 
        
        session.close();

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Facebook Register End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
