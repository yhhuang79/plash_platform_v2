package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Map;
import java.lang.Math;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class SignUpComponent extends PLASHComponent {

	private Session session; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("Sign Up Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Sign Up (Simple Register, no active) 
		 * Author: Yu-Hsiang Huang
		 * Date: 7/23/2012
		 * Version:1.0
		 */
		session = sessionFactory.openSession(); 
		try{
			String username = map.remove("username").toString();
			System.out.println("Sign Up:\t"+ username);
			String email = map.remove("email").toString();
			String password = map.remove("password").toString();
				
			T_Login user = new T_Login();
			
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(password);
			String passcode = "";
			for (int i=0; i<10; i++)
				passcode = passcode + Integer.toString((int)(Math.random() * 10));
			user.setPasscode(passcode);
			user.setConfirmed(true);
			
			Transaction tx = session.beginTransaction();
			session.persist(user);			
			tx.commit();
	        session.close();
	        map.put("code", 200);
	        map.put("userid", user.getSid());
			map.put("message", "Login Success");
		} catch (HibernateException he) {
			map.put("code",400);
			map.put("message", "username already exists");			
		} catch (NullPointerException ne) {
			map.put("code",400);
			map.put("message", "Login Fail");
		}

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Sign Up End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
