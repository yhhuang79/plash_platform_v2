package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.sendMail;
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
		String username = map.get("username").toString();
		String password = map.remove("password").toString();
		
		Criteria criteria = session.createCriteria(T_Login.class);
		criteria.add(Restrictions.eq("username", username));
		criteria.add(Restrictions.eq("password", password));
		Iterator users = criteria.list().iterator(); 
		if(users.hasNext()) {
			T_Login user = (T_Login) users.next(); 
			
			if (user.isConfirmed()) {
				map.put("sid", user.getSid());
				String message = "Successful Login:" + user.getSid();
				map.put("message", message);
				
				//TODO: String aa = WebSiteCore.getUserLatestTrip(userid);
				//		req.setAttribute("map", aa);
				
			}
			//account is not activate
			else {
				//re-sent activation code to user
    			String email = user.getEmail();
    			String passcode = user.getPasscode();
    			
    			String host = "http://plash.iis.sinica.edu.tw/plash/";
				String action = "activate.action";
				String temp1 = "?username="+username;
				String temp2 = "&password="+password;
				String temp3 = "&passcode="+passcode;
				//String temp4 = "&deviceType="+deviceType;
				String parameter = temp1+temp2+temp3;//+temp4;//+temp5;
				String activateLink = host+action+parameter;
				//Or user can activate account by enter passcode.
				String action2 = "preactivate.action";
				String preactivateLink = host+action2;
				String[] to={email};
				sendMail sendAct = new sendMail();
				sendAct.sendActivationCode(to, passcode, preactivateLink, activateLink);
    			
				map.put("sid", "0");
				map.put("message", "Inactivate");
			}

		}
		else{
			map.put("sid", "0");
			map.put("message", "Login Fail");
		}
 
		session.close();
		
		System.out.println(sessionFactory.toString());

		System.out.println("Login End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
