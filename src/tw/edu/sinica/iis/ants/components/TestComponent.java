package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;


import java.util.Random;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_FriendRequest;
import tw.edu.sinica.iis.ants.DB.T_Login;

public class TestComponent {
	
	public static String getRandomString(int length) {
		
		//final String charset = "!0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
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
		 * Activate Action 
		 */
				
//		Session session = sessionFactory.openSession(); 
//
//		String username = null;
//		Integer userid = null;
//		
//		String personalname = null;
//		String friendname = null;
//		String friendemail = null;
//		
//		if (map.containsKey(username)){
//			username = map.get("username").toString();
//		}
//		if (map.containsKey(userid)){
//			userid = Integer.parseInt(map.get("sid").toString());
//		}
//		if (map.containsKey(friendemail)){
//			friendname = map.remove("friendname").toString();
//		}
//		
//		if (map.containsKey(personalname)){
//			personalname = map.remove("personalname").toString();
//			// personalname is empty
//			if (personalname == null || personalname.equals("")) {
//				personalname = username;
//			}
//		}
//		
//		
//        
//        
//        
//        // Check if friend's e-mail address in the login table (friend
//		// is registered user of Plash Project
//        Criteria criteria = session.createCriteria(T_Login.class);
//    	criteria.add(Restrictions.eq("username", username));
//    	
//    	Iterator friends = criteria.list().iterator(); 
//    	// if friend is registered in Plash Project
//    	if (friends.hasNext()){
//    		
//    		T_Login friend = (T_Login) friends.next();
//    		String existUsername = friend.getUsername();
//    		int friendid = friend.getSid();
//    		
//    		// Check if friendship is already existed.
//    		criteria = session.createCriteria(T_FriendList.class);
//    		criteria.add(Restrictions.eq("cid", friendid));
//    		Iterator friendship = criteria.list().iterator();
//    		
//    		//if friendship is existed
//    		if (friendship.hasNext()){
//    			map.put("message", "Friendship Already Existed!");
//    		}
//    		
//    		//if friendship is not existed
//    		else {
//    			
//    			//Check if friend request is existed
//    			criteria = session.createCriteria(T_FriendRequest.class);
//    			criteria.add(Restrictions.eq("useraid", userid));
//    			criteria.add(Restrictions.eq("userbid", friendid));
//    			Iterator friendrequest = criteria.list();
//    			
//    			//if friend request is existed
//    			if (friendrequest.hasNext()){
//    				map.put("message",	"Friend Request Existed");
//    			}
//    			
//    			//if friend request is not existed
//    			else{
//    				criteria = session.createCriteria(T_FriendRequest.class);
//    				friendrequest = (Iterator) criteria.list();
//    				T_FriendRequest friendlist = (T_FriendRequest) friendrequest.next();
//    				friendlist.setUseraid(userid); //useraid
//    				friendlist.setUserbid(friendid); //userbid
//    				friendlist.setFriendemail(friendemail);
//    				
//					// generate a random passcode/password for
//					// activation of friend request
//					String passcode = getRandomString(10);
//					friendlist.setPasscode(passcode);
//					
//					Transaction tx = session.beginTransaction();
//        			session.save(friendlist);
//        			tx.commit();
//					    			
//        			map.put("message",	"Successful Friend Request");
//    				
//    				
//    			}
//    			
//    		}
//    		
//    		
//    	}
//        
//        
//        //------------------------------------------------------------------------------//
//        if (!username.isEmpty() && !password.isEmpty() && !passcode.isEmpty()){
//        	
//        	Criteria criteria = session.createCriteria(T_Login.class);
//        	criteria.add(Restrictions.eq("username", username));
//        	
//        	Iterator users = criteria.list().iterator(); 
//        	
//        	if (users.hasNext()){
//        		T_Login user = (T_Login) users.next();
//        		
//        		if (username.equals(user.getUsername()) && password.equals(user.getPassword()) && passcode.equals(user.getPasscode())){
//        			
//        			user.setConfirmed(true);
//        			Transaction tx = session.beginTransaction();
//        			session.save(user);
//        			tx.commit();
//        			map.put("message", "Successful Activate");
//        		}
//        		else {
//        			map.put("message", "Invalid password or activation code");
//        			
//        		}
//        		
//        	} else { //end if (result.next())
//        		//if (result.next()) is empty = no item found in DB
//        		map.put("message", "Username invalid");
//        	}
//        	
//        
//        	
//        } else {
//        	//Username, Password, or Activation Code is empty
//        	map.put("message", "Empty Field");
//        }
//        
//             
//		session.close();
		
		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Test End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
