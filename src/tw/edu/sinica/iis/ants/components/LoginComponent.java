package tw.edu.sinica.iis.ants.components;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Login;

public class LoginComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public LoginComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Login Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession(); 
		Criteria criteria = session.createCriteria(T_Login.class);
		criteria.add(Restrictions.eq("username", map.get("username").toString()));
		criteria.add(Restrictions.eq("password", map.remove("password").toString()));
		Iterator users = criteria.list().iterator(); 
		if(users.hasNext()) {
			T_Login user = (T_Login) users.next(); 
			
			if (user.isConfirmed()) {
				map.put("sid", user.getSid());
				String message = "Successful Login:" + user.getSid();
				map.put("message", message);
			}
			else {
				map.put("sid", "fail");
				map.put("message", "Inactivate");
			}

		}
		else{
			map.put("sid", "fail");
			map.put("message", "Login Fail");
		}
 
		session.close();
		
		System.out.println(sessionFactory.toString());

		System.out.println("Login End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
