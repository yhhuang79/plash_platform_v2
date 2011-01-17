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

public class ConfirmFriendRequestComponent {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public ConfirmFriendRequestComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Confirm Friend Request Action
		 * Author: Yao H. (Danny) Ho
		 * Date: 1/17/2011
		 */

		Session session = sessionFactory.openSession();
		
        int fid = 0;
        int useraid = 0;
        int userbid = 0;
        //String username = null; //user that sent the Friend Request
        String friendemail = null;
        String friendname = null;
        String passcode = null;
        String deviceType = null;
        boolean isFriendNewUser = false;
        
        
        //Input: fid, passcode, deviceType
        if (map.containsKey("fid")){
        	fid = Integer.parseInt(map.get("fid").toString());
        }
        if (map.containsKey("passcode")){
        	passcode = map.remove("passcode").toString();
        }
        if (map.containsKey("friendname")){
        	friendname = map.get("friendname").toString();
        }
        deviceType = "browser";
     
        // Check if friend request is existed
		Criteria criteria = session.createCriteria(T_FriendRequest.class);
		criteria.add(Restrictions.eq("fid", fid));
		Iterator friendrequest = criteria.list().iterator();

		// if friend request is existed
		if (friendrequest.hasNext()) {
			
			T_FriendRequest friendlist = (T_FriendRequest) friendrequest.next();
			useraid = friendlist.getUseraid();
            userbid = friendlist.getUserbid();
            friendemail = friendlist.getFriendemail();
			
            //User confirmed with passcode
			if (passcode.equals(friendlist.getPasscode())){
				
				//TODO: delete the confirmed entry in friend_request table
				Transaction tx = session.beginTransaction();
				
				//Opt.1 - HQL
				//String hql = "delete from T_FriendRequest where fid = "+fid;
				//Query query = session.createQuery(hql);
				//query.executeUpdate();
				
				//Opt.2 - SQL
				//String sql = "delete from socialcore.friend_request where fid = "+fid;
				//Query query = session.createSQLQuery(sql);
				//query.executeUpdate();
				
				//Opt.3 - criteria
				//T_FriendRequest deleteRequest = (T_FriendRequest) session.createCriteria(T_FriendRequest.class).add(Restrictions.eq("fid", fid)).uniqueResult();
				//session.delete(deleteRequest);
				
				//Opt.4 
				session.delete(friendlist);
				
				tx.commit();

				//if ConfrimFriendRequest is called by PreInviteAction
        		if (userbid == 0){
        			criteria = session.createCriteria(T_Login.class);
        			criteria.add(Restrictions.eq("friendemail",friendemail.toString()));
        			Iterator users = criteria.list().iterator(); 
        			if(users.hasNext()) {
        				T_Login user = (T_Login) users.next(); 
        				map.put("userbid", user.getSid());
        				isFriendNewUser = true;
        			}
        		}
        		
        		//Update the friend_list table
        		
        		//Insert friendship information to DB's friend_list Table
        		criteria = session.createCriteria(T_FriendList.class);
        		T_FriendList friendlist1 = new T_FriendList();
        		friendlist1.setUseraid(useraid); // useraid
				friendlist1.setUserbid(userbid); // userbid
				Transaction tx1 = session.beginTransaction();
				session.save(friendlist1);
				tx1.commit();
        		
				map.put("message", "Friendship Confirmed");
				
				//--------------------------------------------------------------//
				//Sent a confirm letter to inviter
				
				String useraemail = null;
				criteria = session.createCriteria(T_Login.class);
    			criteria.add(Restrictions.eq("sid", useraid));
    			Iterator users = criteria.list().iterator(); 
    			if(users.hasNext()) {
    				T_Login user = (T_Login) users.next(); 
    				useraemail = user.getEmail();
    			}
				
				String host = "http://plash.iis.sinica.edu.tw/plash/";
				String action = "*.action";
	            String link = host+action;
	            
	            //Sent email
	            sendMail sendReq = new sendMail();
                String[] to = {useraemail};
                sendReq.sendConfirmFriendRequest(to, friendname, link);
								
			}
			
			//passcode is not matching
			else {
				map.put("message","Invalid friend request or passcode");
			}
			
			
		} //End if (friendrequest.hasNext())
		else{
			map.put("message", "No Friend Request Found.");
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
