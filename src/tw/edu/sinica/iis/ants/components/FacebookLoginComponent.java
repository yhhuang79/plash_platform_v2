package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.sendMail;
import tw.edu.sinica.iis.ants.DB.T_Login;

public class FacebookLoginComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public FacebookLoginComponent() {

	}

	public Object greet(Map map) {
		System.out.println("FacebookLogin Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession();
		String username = null;
		if(map.containsKey("username"))
			username = map.get("username").toString();
		String email = map.get("email").toString();
		String facebookid = map.get("facebookid").toString();
		
		Criteria criteria = session.createCriteria(T_Login.class);
		criteria.add(Restrictions.eq("email", email));
		criteria.add(Restrictions.eq("facebookid", facebookid));
		System.out.println("FacebookLogin Component:  " + email +" : "+ facebookid);
		Iterator users = criteria.list().iterator(); 
		if(users.hasNext()) {
			T_Login user = (T_Login) users.next(); 
			
			if (user.isConfirmed()) {
				map.put("sid", user.getSid());
				String message = "Successful Login:" + user.getSid();
				map.put("message", message);
			} else {
				map.put("sid", "0");
				map.put("message", "Inactivate");
			}

		} else {
			//map.put("sid", "0");
			//map.put("message", "Login Fail");
			T_Login user = new T_Login();
			
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword("");
			user.setPasscode("");
			user.setFacebookid(facebookid);
			user.setConfirmed(true);
			
			Transaction tx = session.beginTransaction();
			session.persist(user);
			tx.commit();
			
			map.put("sid", user.getSid());
			String message = "Successful Login:" + user.getSid();
			map.put("message", message);			
		}
 
		session.close();
		
		System.out.println(sessionFactory.toString());

		System.out.println("FacebookLogin End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
