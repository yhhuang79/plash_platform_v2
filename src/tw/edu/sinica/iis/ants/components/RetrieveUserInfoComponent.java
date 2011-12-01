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

public class RetrieveUserInfoComponent {

	public static String getRandomString(int length) {

		// final String charset =
		// "!0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String charset = "0123456789";
		Random rand = new Random(System.currentTimeMillis());

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int pos = rand.nextInt(charset.length());
			sb.append(charset.charAt(pos));
		}
		try {
			// if you generate more than 1 time, you must
			// put the process to sleep for awhile
			// otherwise it will return the same random string
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static boolean isValidEmailAddress(String emailAddress) {
		String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	public static boolean isValidUsername(String username) {
		String expression = "^[A-Za-z0-9_]{6,20}$";
		CharSequence inputStr = username;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	
	//Password must contain a minimum of 6 to 20 characters 
	public static boolean isValidPassword (String password){
		 String expression = "^[A-Za-z0-9!@#$%^&*()_]{6,20}$";
		 CharSequence inputStr = password; 
		 Pattern pattern = Pattern.compile(expression);
		 Matcher matcher = pattern.matcher(inputStr);
		 return matcher.matches();
	}
	
	public static String inputFormatChecker (String username, String password, String password2, String email, String email2){
		
		String message;
	 
		//Entered passwords do not match
	    if (!password.equals(password2)){
	    		message = "Password unmatch";
			   	return message;
	    }
	    
	    else if (!email.equals(email2)){
	    	message = "E-mail unmatch";
			return message;
	    }
	
	    //Username or password is empty
	    else if (username.equals("") || password.equalsIgnoreCase("") ||password2.equalsIgnoreCase("")){
	    	message = "Username empty";
			return message;
	    }
	    
	    //Username is invalid
	    else if (!isValidUsername(username)){
	    	message = "Username invalid";
			return message;
	    }
	    
	    //Password is invalid
	    else if (!isValidPassword(password)){
	    	message = "Password invalid";
			return message;
	    }
	    
	    //Email address format is invalid
	    else if (!isValidEmailAddress(email)){
	    	message = "E-mail invalid";
			return message;
	    }
	    
	    else {
	    	message = "Pass";
	    	return message;
	    }
		
	}
	
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public RetrieveUserInfoComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: RetrieveUserInfo Action
		 * Author: Yao H. (Danny) Ho
		 * Date: 1/25/2011
		 * Version:1.01
		 */

		Session session = sessionFactory.openSession();
		
        String username = "";
        String email = "";
        String password = "";
        //String deviceType = null;
		String deviceType = "phone";
        int appID = 0;
        
        int fid = 0;
        String Req_passcode = "";
        String friendname = "";
		
        if (map.containsKey("username")){
        	username = map.remove("username").toString();
        }
//        if (map.containsKey("password")){
//        	password = map.remove("password").toString();
//        }
        if (map.containsKey("email")){
        	email = map.remove("email").toString();
        }
        if (map.containsKey("appID")){
        	appID = Integer.parseInt(map.get("appID").toString());
        }
        
        //Empty field(s): User did not provide any input
        if (username.equals("") && email.equals("")){
        	map.put("message", "Empty Field");
        }
        //if username format is invalid
        else if (email.equals("")&&!isValidUsername(username)){
        	map.put("message", "Invalid Username");
        }
        //if email format is invalid.
        else if (username.equals("")&&!isValidEmailAddress(email)){
        	map.put("message", "Invalid Email");
        }
        //retrieve user password
        else {
        	
        	//Query login table for input username and email
        	Criteria criteria = session.createCriteria(T_Login.class);
    		criteria.add(Restrictions.eq("username", username));
//    		criteria.add(Restrictions.eq("email", email));
    		Iterator users = criteria.list().iterator(); 
    		if(users.hasNext()) {
    			T_Login user = (T_Login) users.next();
    			password = user.getPassword();
    			String srvEmail = user.getEmail();
    			
    			//check if the email address saved on server match the one inputed
    			if(srvEmail.equals(email)){
    				String[] to={email};
        			sendMail sendAct = new sendMail();
        			sendAct.sendPassword(to, username, password);
        			map.put("retrivedPW", password);
        			map.put("message", "Password Sent To Email");
    			}else{
    				map.put("message", "No Match");
    			}
    		}else{
    			map.put("message", "Account Not Found");
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
