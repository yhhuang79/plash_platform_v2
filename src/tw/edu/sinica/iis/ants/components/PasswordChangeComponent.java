package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_FriendRequest;
import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.sendMail;

public class PasswordChangeComponent {

	//Password must contain a minimum of 6 to 20 characters 
	public static boolean isValidPassword (String password){
		 String expression = "^[A-Za-z0-9!@#$%^&*()_]{6,20}$";
		 CharSequence inputStr = password; 
		 Pattern pattern = Pattern.compile(expression);
		 Matcher matcher = pattern.matcher(inputStr);
		 return matcher.matches();
	}
	
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public PasswordChangeComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Password Change Action
		 * Author: Yao H. (Danny) Ho
		 * Date: 1/25/2011
		 * Version:1.01
		 */

		Session session = sessionFactory.openSession();
		
		String userid = null;
	    String username = null;
	    int sid = 0;
		
	    String oldpassword = "";
        String newpassword = "";
        String newpassword2 = "";
        String deviceType = null;
        String message = null;
        int appID = 0;
		
        if (map.containsKey("userid")){
        	userid = map.remove("userid").toString();
        	sid = Integer.parseInt(userid);
        }
        
        if (map.containsKey("sid")){
        	sid = Integer.parseInt(map.get("sid").toString());
        }
        
        if (map.containsKey("username")){
        	username = map.remove("username").toString();
        }
        if (map.containsKey("oldpassword")){
        	oldpassword = map.remove("oldpassword").toString();
        }
        if (map.containsKey("newpassword")){
        	newpassword = map.remove("newpassword").toString();
        }
        if (map.containsKey("newpassword2")){
        	newpassword2 = map.remove("newpassword2").toString();
        }
        if (map.containsKey("deviceType")){
        	deviceType = map.remove("deviceType").toString();
        }
        else{
        	deviceType = "phone";
        }
        if (map.containsKey("appID")){
        	appID = Integer.parseInt(map.get("appID").toString());
        }
        
        //Empty field(s)
        if (oldpassword.equals("")||newpassword.equals("")||newpassword2.equals("")){
        	map.put("message", "Field(s) is empty.");
        }
        
        //Entered new passwords do not match
	    else if (!newpassword.equals(newpassword2)){
        	map.put("message", "Password unmatched.");
	    }
        
        //Validate password format
	    else if (!isValidPassword(newpassword)){
	    	map.put("message", "New password is invalided.");
	    }
        
	    else {
	    	//Valid password match
	    	Criteria criteria = session.createCriteria(T_Login.class);
			//criteria.add(Restrictions.eq("username", username));
			criteria.add(Restrictions.eq("sid", sid));
			criteria.add(Restrictions.eq("password", oldpassword));
			Iterator users = criteria.list().iterator(); 
			if(users.hasNext()) {
				T_Login user = (T_Login) users.next(); 
				user.setPassword(newpassword);
				
				Transaction tx = session.beginTransaction();
				session.save(user);
				tx.commit();
				
				map.put("message", "Successful updated.");
			}
			else {
				map.put("message", "Password is invalid.");
			}
	    	
	    	
	    }

        session.close();

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Test End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
