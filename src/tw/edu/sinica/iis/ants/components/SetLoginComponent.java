package tw.edu.sinica.iis.ants.components;

import java.util.*;


import java.util.regex.*;


import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.sendMail;


public class SetLoginComponent extends PLASHComponent {

	public String getRandomString(int length) {

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
	    if (username==null || password==null||password2==null){
//	    	message = "Username empty";
	    	message = "Empty Field";
			return message;
	    }
		
		else if (!password.equals(password2)){
	    		message = "Password Unmatch";
			   	return message;
	    }
	    
	    else if (!email.equals(email2)){
	    	message = "E-mail Unmatch";
			return message;
	    }
	
	    //Username or password is empty
	    else if (username.equals("") || password.equalsIgnoreCase("") ||password2.equalsIgnoreCase("")){
//	    	message = "Username empty";
	    	message = "Empty Field";
			return message;
	    }
	    
	    //Username is invalid
	    else if (!isValidUsername(username)){
//	    	message = "Username invalid";
	    	message = "Invalid Username";	    	
			return message;
	    }
	    
	    //Password is invalid
	    else if (!isValidPassword(password)){
//	    	message = "Password invalid";
	    	message = "Invalid Password";
			return message;
	    }
	    
	    //Email address format is invalid
	    else if (!isValidEmailAddress(email)){
//	    	message = "E-mail invalid";
	    	message = "Invalid E-mail";
			return message;
	    }
	    
	    else {
	    	message = "Pass";
	    	return message;
	    }
		
	}
	

	private Session tskSession; //task session

	public SetLoginComponent() {
		super();	
		//enableDebugLog();
	}//end constructor

	@Override
	public Object serviceMain(Map map) {
		

        int userid, trip_id, friend_id;
        String username, password;
		String tmpUsername, tmpPassword;
		
		
		try {
			

			if ((username = (String)map.remove("username")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 002;
		        err.explaination = "User name must be specified";
				return returnUnsuccess(map,err);
			}//fi
			if ((password = (String)map.remove("password")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 002;
		        err.explaination = "Password must be specified";
				return returnUnsuccess(map,err);
			}//fi
			


	        Criteria loginCriteria = tskSession.createCriteria(T_Login.class);
	        loginCriteria.add(Restrictions.eq("username", username));
	        loginCriteria.add(Restrictions.eq("password", password));	        
	        T_Login loginEntry = (T_Login) loginCriteria.uniqueResult();
			
	        if (map.containsKey("username")){
	        	username = map.remove("username").toString();
	        }
	        if (map.containsKey("password")){
	        	password = map.remove("password").toString();
	        }

	        if (map.containsKey("email")){
	   //     	email = map.remove("email").toString();
	        }
	        if (map.containsKey("email2")){
	   //     	email2 = map.remove("email2").toString();
	        }
	        if (map.containsKey("appID")){
	    //    	appID = Integer.parseInt(map.get("appID").toString());
	        }
	      	        
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("getAuthFriend",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthFriendComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("getAuthFriend",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("getAuthFriendComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		}//end try catch
		
		
		
        
        
        

        
        
		return map;
	}//end method


}//end class
