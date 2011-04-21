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

public class RegisterComponent {

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
	    if (username==null || password==null||password2==null){
	    	message = "Username empty";
			return message;
	    }
		
		else if (!password.equals(password2)){
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

	public RegisterComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Register Action
		 * Author: Yao H. (Danny) Ho
		 * Date: 1/17/2011
		 * Version:1.01
		 */

		Session session = sessionFactory.openSession();
		
		String username = null;
        String password = null;
        String password2 = null;
        String email = null;
        String email2 = null;
        String deviceType = "phone";
        int appID = 0;
        
        int fid = 0;
        String Req_passcode = "";
        String friendname = "";
		
        if (map.containsKey("username")){
        	username = map.remove("username").toString();
        }
        if (map.containsKey("password")){
        	password = map.remove("password").toString();
        }
        if (map.containsKey("password2")){
        	password2 = map.remove("password2").toString();
        }
        if (map.containsKey("email")){
        	email = map.remove("email").toString();
        }
        if (map.containsKey("email2")){
        	email2 = map.remove("email2").toString();
        }
        if (map.containsKey("appID")){
        	appID = Integer.parseInt(map.get("appID").toString());
        }
      
        //for invite friend join and become a friend
        if (map.containsKey("fid")){
        	fid = Integer.parseInt(map.get("fid").toString());
        }
        
        //for confirm friend request
        if (map.containsKey("Req_passcode")){
        	Req_passcode = map.remove("Req_passcode").toString();
        }
        if (map.containsKey("friendname")){
        	friendname = map.remove("friendname").toString();
        }
        
        //Validate User Input format
        String message = inputFormatChecker(username, password, password2, email, email2);
        if (message != "Pass"){
        	map.put("message", message);
        }
        else {
	        //-------------------------------------------------------------------//
			//check username and email addres is used by others
	        String existUsername = "";
	        String existEmail = null;
	        
	        //1) Query for username	        
	        Criteria criteria = session.createCriteria(T_Login.class);
			criteria.add(Restrictions.eq("username", username));
			//criteria.add(Restrictions.eq("password", map.remove("password").toString()));
			Iterator users = criteria.list().iterator(); 
			if(users.hasNext()) {
				T_Login user = (T_Login) users.next(); 
				existUsername = user.getUsername().toString();
			}
			
			//2) Query for email
			criteria.add(Restrictions.eq("email", email));
			users = criteria.list().iterator();
			if(users.hasNext()) {
				T_Login user = (T_Login) users.next(); 
				existEmail = user.getEmail().toString();
			}
			
			
		    if (existUsername.equals(username)||existEmail != null){
		    	
		    	//Username is not available
		    	if (existUsername.equals(username)){
		    		map.put("message", "Username unavailable");
		    	}
		    	//Email address is unavailable (used by other)
		    	else {
		    		map.put("message", "E-mail unavailable");
		    	}
		    }
		    //-------------------------------------------------------------------//
			//Register new user
			else{
				//Insert into login Table
				
				//-------------------------------------------------------------------//
				//generate a random passcode/password for activation of registration
				String passcode = null;
				passcode = getRandomString(10);
				
				//-------------------------------------------------------------------//
			    //Insert new user information to DB's login Table
				T_Login user = new T_Login();
				//T_Login user = (T_Login) users.next();
				
				user.setUsername(username);
				user.setEmail(email);
				user.setPassword(password);
				user.setPasscode(passcode);
				user.setConfirmed(false);
				
				Transaction tx = session.beginTransaction();
				session.save(user);
				tx.commit();
				
				//TODO: Insert new user information to DB's appUser Table
				
				//-------------------------------------------------------------------//
			    //Email the link and passcode to activate new registered user account
				String host = "http://plash.iis.sinica.edu.tw/plash/";
				String action = "activate.action";
				String temp1 = "?username="+username;
				String temp2 = "&password="+password;
				String temp3 = "&passcode="+passcode;
				String temp4 = "&deviceType="+deviceType;
				//String temp5 = "&appID="+appID;
				String parameter = temp1+temp2+temp3+temp4;//+temp5;
				String activateLink = host+action+parameter;
				
				//Or user can activate account by enter passcode.
				String action2 = "preactivate.action";
				String preactivateLink = host+action2;
				
				String[] to={email};
				sendMail sendAct = new sendMail();
				sendAct.sendActivationCode(to, passcode, preactivateLink, activateLink);
				//-------------------------------------------------------------------//
				
				//If this RegisterAction request is from PreInvaitionAction
				//Then a Confirm Friend Request Email is sent to inviter 
				if (fid != 0){
					action = "confirmfriendrequest.action";
					temp1 = "?fid="+fid;
					temp2 = "&friendname="+friendname;
					temp3 = "&passcode="+Req_passcode;
					temp4 = "&deviceType="+deviceType;
					//temp5 = "&appID="+appID;
					parameter = temp1+temp2+temp3+temp4;//+temp5; 
					String confirmLink = host+action+parameter;
								
				}
				else {
					map.put("message", "Successful Registered");
				}
			
			}
        }
        
		    	
        
        
        
		
        
        
        
        

        
        
		//--------------------------------------------------------------------------------------------------------------------//
		
//        int fid = 0;
//        int useraid = 0;
//        int userbid = 0;
//        //String username = null; //user that sent the Friend Request
//        String friendemail = null;
//        String friendname = null;
//        String passcode = null;
//        String deviceType = null;
//        boolean isFriendNewUser = false;
//        
//        
//        //Input: fid, passcode, deviceType
//        if (map.containsKey("fid")){
//        	fid = Integer.parseInt(map.get("fid").toString());
//        }
//        if (map.containsKey("passcode")){
//        	passcode = map.remove("passcode").toString();int fid = 0;
//      int useraid = 0;
//      int userbid = 0;
//      //String username = null; //user that sent the Friend Request
//      String friendemail = null;
//      String friendname = null;
//      String passcode = null;
//      String deviceType = null;
//      boolean isFriendNewUser = false;
//      
//      
//      //Input: fid, passcode, deviceType
//      if (map.containsKey("fid")){
//      	fid = Integer.parseInt(map.get("fid").toString());
//      }
//      if (map.containsKey("passcode")){
//      	passcode = map.remove("passcode").toString();
//      }
//      if (map.containsKey("friendname")){
//      	friendname = map.get("friendname").toString();
//      }
//      deviceType = "browser";
//   
//      // Check if friend request is existed
//		Criteria criteria = session.createCriteria(T_FriendRequest.class);
//		criteria.add(Restrictions.eq("fid", fid));
//		Iterator friendrequest = criteria.list().iterator();
//
//		// if friend request is existed
//		if (friendrequest.hasNext()) {
//			
//			T_FriendRequest friendlist = (T_FriendRequest) friendrequest.next();
//			useraid = friendlist.getUseraid();
//          userbid = friendlist.getUserbid();
//          friendemail = friendlist.getFriendemail();
//			
//          //User confirmed with passcode
//			if (passcode.equals(friendlist.getPasscode())){
//				
//				//TODO: delete the confirmed entry in friend_request table
//				Transaction tx = session.beginTransaction();
//				
//				//Opt.1 - HQL
//				//String hql = "delete from T_FriendRequest where fid = "+fid;
//				//Query query = session.createQuery(hql);
//				//query.executeUpdate();
//				
//				//Opt.2 - SQL
//				//String sql = "delete from socialcore.friend_request where fid = "+fid;
//				//Query query = session.createSQLQuery(sql);
//				//query.executeUpdate();
//				
//				//Opt.3 - criteria
//				//T_FriendRequest deleteRequest = (T_FriendRequest) session.createCriteria(T_FriendRequest.class).add(Restrictions.eq("fid", fid)).uniqueResult();
//				//session.delete(deleteRequest);
//				
//				//Opt.4 
//				session.delete(friendlist);
//				
//				tx.commit();
//
//				//if ConfrimFriendRequest is called by PreInviteAction
//      		if (userbid == 0){
//      			criteria = session.createCriteria(T_Login.class);
//      			criteria.add(Restrictions.eq("friendemail",friendemail.toString()));
//      			Iterator users = criteria.list().iterator(); 
//      			if(users.hasNext()) {
//      				T_Login user = (T_Login) users.next(); 
//      				map.put("userbid", user.getSid());
//      				isFriendNewUser = true;
//      			}
//      		}
//      		
//      		//Update the friend_list table
//      		
//      		//Insert friendship information to DB's friend_list Table
//      		criteria = session.createCriteria(T_FriendList.class);
//      		T_FriendList friendlist1 = new T_FriendList();
//      		friendlist1.setUseraid(useraid); // useraid
//				friendlist1.setUserbid(userbid); // userbid
//				Transaction tx1 = session.beginTransaction();
//				session.save(friendlist1);
//				tx1.commit();
//      		
//				map.put("message", "Friendship Confirmed");
//				
//				//--------------------------------------------------------------//
//				//Sent a confirm letter to inviter
//				
//				String useraemail = null;
//				criteria = session.createCriteria(T_Login.class);
//  			criteria.add(Restrictions.eq("sid", useraid));
//  			Iterator users = criteria.list().iterator(); 
//  			if(users.hasNext()) {
//  				T_Login user = (T_Login) users.next(); 
//  				useraemail = user.getEmail();
//  			}
//				
//				String host = "http://plash.iis.sinica.edu.tw/plash/";
//				String action = "*.action";
//	            String link = host+action;
//	            
//	            //Sent email
//	            sendMail sendReq = new sendMail();
//              String[] to = {useraemail};
//              sendReq.sendConfirmFriendRequest(to, friendname, link);
//								
//			}
//			
//			//passcode is not matching
//			else {
//				map.put("message","Invalid friend request or passcode");
//			}
//			
//			
//		} //End if (friendrequest.hasNext())
//		else{
//			map.put("message", "No Friend Request Found.");
//		}
//		
//        }
//        if (map.containsKey("friendname")){
//        	friendname = map.get("friendname").toString();
//        }
//        deviceType = "browser";
//     
//        // Check if friend request is existed
//		Criteria criteria = session.createCriteria(T_FriendRequest.class);
//		criteria.add(Restrictions.eq("fid", fid));
//		Iterator friendrequest = criteria.list().iterator();
//
//		// if friend request is existed
//		if (friendrequest.hasNext()) {
//			
//			T_FriendRequest friendlist = (T_FriendRequest) friendrequest.next();
//			useraid = friendlist.getUseraid();
//            userbid = friendlist.getUserbid();
//            friendemail = friendlist.getFriendemail();
//			
//            //User confirmed with passcode
//			if (passcode.equals(friendlist.getPasscode())){
//				
//				//TODO: delete the confirmed entry in friend_request table
//				Transaction tx = session.beginTransaction();
//				
//				//Opt.1 - HQL
//				//String hql = "delete from T_FriendRequest where fid = "+fid;
//				//Query query = session.createQuery(hql);
//				//query.executeUpdate();
//				
//				//Opt.2 - SQL
//				//String sql = "delete from socialcore.friend_request where fid = "+fid;
//				//Query query = session.createSQLQuery(sql);
//				//query.executeUpdate();
//				
//				//Opt.3 - criteria
//				//T_FriendRequest deleteRequest = (T_FriendRequest) session.createCriteria(T_FriendRequest.class).add(Restrictions.eq("fid", fid)).uniqueResult();
//				//session.delete(deleteRequest);
//				
//				//Opt.4 
//				session.delete(friendlist);
//				
//				tx.commit();
//
//				//if ConfrimFriendRequest is called by PreInviteAction
//        		if (userbid == 0){
//        			criteria = session.createCriteria(T_Login.class);
//        			criteria.add(Restrictions.eq("friendemail",friendemail.toString()));
//        			Iterator users = criteria.list().iterator(); 
//        			if(users.hasNext()) {
//        				T_Login user = (T_Login) users.next(); 
//        				map.put("userbid", user.getSid());
//        				isFriendNewUser = true;
//        			}
//        		}
//        		
//        		//Update the friend_list table
//        		
//        		//Insert friendship information to DB's friend_list Table
//        		criteria = session.createCriteria(T_FriendList.class);
//        		T_FriendList friendlist1 = new T_FriendList();
//        		friendlist1.setUseraid(useraid); // useraid
//				friendlist1.setUserbid(userbid); // userbid
//				Transaction tx1 = session.beginTransaction();
//				session.save(friendlist1);
//				tx1.commit();
//        		
//				map.put("message", "Friendship Confirmed");
//				
//				//--------------------------------------------------------------//
//				//Sent a confirm letter to inviter
//				
//				String useraemail = null;
//				criteria = session.createCriteria(T_Login.class);
//    			criteria.add(Restrictions.eq("sid", useraid));
//    			Iterator users = criteria.list().iterator(); 
//    			if(users.hasNext()) {
//    				T_Login user = (T_Login) users.next(); 
//    				useraemail = user.getEmail();
//    			}
//				
//				String host = "http://plash.iis.sinica.edu.tw/plash/";
//				String action = "*.action";
//	            String link = host+action;
//	            
//	            //Sent email
//	            sendMail sendReq = new sendMail();
//                String[] to = {useraemail};
//                sendReq.sendConfirmFriendRequest(to, friendname, link);
//								
//			}
//			
//			//passcode is not matching
//			else {
//				map.put("message","Invalid friend request or passcode");
//			}
//			
//			
//		} //End if (friendrequest.hasNext())
//		else{
//			map.put("message", "No Friend Request Found.");
//		}
//		
        session.close();

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Test End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
