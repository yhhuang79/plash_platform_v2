package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_FriendRequest;
import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.sendMail;

public class FriendRequestComponent {

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
		String expression = "^[\\w\\_-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
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

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public FriendRequestComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"
				+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Friend Request Action
		 * Author: Yao H. (Danny) Ho
		 * Date: 1/10/2011
		 */

		Session session = sessionFactory.openSession();

		String username = null;
		Integer userid = null;

		String personalname = null;
		String friendname = null;
		String friendemail = null;

		if (map.containsKey("username")) {
			username = map.get("username").toString();
		}
		
		if (map.containsKey("sid")) {
			userid = Integer.parseInt(map.get("sid").toString());
		}
		else {
			userid = Integer.parseInt(map.get("userid").toString());
		}
		if (map.containsKey("friendname")) {
			friendname = map.remove("friendname").toString();
		}
		if (map.containsKey("friendemail")){
			friendemail = map.remove("friendemail").toString();
		}
		if (map.containsKey("personalname")) {
			personalname = map.remove("personalname").toString();
			// if personalname is empty, set it equal with username
			if (personalname == null || personalname.equals("")) {
				personalname = username;
			}
		}

		// Check user input begin
		if (friendemail.equals("")) {
			map.put("message", "Friend email is empty");
		} else if (friendname.equals("")) {
			map.put("message", "Friend name is empty");
		}

		// Validate e-mail format
		else if (!isValidEmailAddress(friendemail)) {
			map.put("message", "E-mail invalid");
		}
		// Validate friendname field
//		else if (!isValidUsername(friendname)) {
//			map.put("message", "Friend's name invalid");
//		} 
		// -------------------------------------------------------------------//
		// Pass all the format checks
		
		else {

			// Check if friend's e-mail address in the login table friend is registered user of Plash Project
			Criteria criteria = session.createCriteria(T_Login.class);
			criteria.add(Restrictions.eq("email", friendemail));
			Iterator friends = criteria.list().iterator();
			
			// if friend is registered in Plash Project
			if (friends.hasNext()) {

				T_Login friend = (T_Login) friends.next();
				String existUsername = friend.getUsername();
				int friendid = friend.getSid();

				// Check if friendship is already existed.
				criteria = session.createCriteria(T_FriendList.class);
				criteria.add(Restrictions.eq("userbid", friendid));
				Iterator friendship = criteria.list().iterator();

				
				// if friendship is existed
				if (friendship.hasNext()) {
					map.put("message", "Friendship Already Existed!");
				}

				// if friendship is not existed
				else {

					int fid = 0;
					String passcode = null;
					
					// Check if friend request is existed
					criteria = session.createCriteria(T_FriendRequest.class);
					criteria.add(Restrictions.eq("useraid", userid));
					criteria.add(Restrictions.eq("userbid", friendid));
					Iterator friendrequest = criteria.list().iterator();

					// if friend request is existed
					if (friendrequest.hasNext()) {
						//map.put("message", "Friend Request Existed");
						
						T_FriendRequest friendlist = (T_FriendRequest) friendrequest.next();
						fid = friendlist.getFid();
						passcode = friendlist.getPasscode();
						map.put("fid", fid);
						
					}

					// if friend request is not existed
					else {
						
						//--------------------------------------------------------------//
						//Insert into friend request table
						criteria = session.createCriteria(T_FriendRequest.class);
						
						//TODO: incorrect fid - 1/17/11
//						List a = criteria.list();
//						//friendrequest = criteria.list().get(index).iterator();
//						
//						T_FriendRequest friendlist = (T_FriendRequest) a.get(a.size()-1);//friendrequest.next();
//						int fid = friendlist.getFid()+1;	
						//------------------//	
						
						T_FriendRequest friendlist = new T_FriendRequest();
						
						friendlist.setUseraid(userid); // useraid
						friendlist.setUserbid(friendid); // userbid
						friendlist.setFriendemail(friendemail);

						// generate a random passcode/password for
						// activation of friend request
						passcode = getRandomString(10);
						friendlist.setPasscode(passcode);
						boolean isConfirmed = false;
						friendlist.setConfirmed(isConfirmed);
						
						System.out.println("---------FriendRequestComponent: (" + userid
								+ "," + friendid + "," + friendemail + ","
								+ passcode+","+isConfirmed+")");
						Transaction tx = session.beginTransaction();
						session.save(friendlist);
						tx.commit();
						
						fid = friendlist.getFid();
						map.put("fid", fid);
						//--------------------------------------------------------------//
						
						
					}//end of if (friendrequest.hasNext()) {...} else {...}
					
					//--------------------------------------------------------------//
					// create a Confirmation Link to confirm the friendship
					//String host = "http://plash.iis.sinica.edu.tw/plash/";
					//String action = "confirmfriendrequest.action";
					String host = "https://plash.iis.sinica.edu.tw:8080/";
					String action = "ConfirmFriendRequest";
					String temp1 = "?fid=" + fid;
					// String temp2 = "&userid="+userid; //sid
					// String temp3 = "&friendid="+friendid; //friend's sid
					String temp4 = "&friendname=" + friendname;
					String temp5 = "&passcode=" + passcode;
					String temp6 = "&deviceType=browser";
					String parameter = temp1 + temp4 + temp5 + temp6;
					String confirmLink = host + action + parameter;
	
					// send a Friend Request e-mail to the friend
					sendMail sendReq = new sendMail();
					String[] to = { friendemail };
					String personalmessage = null;
					sendReq.sendFriendRequest(to, personalname, friendname,	personalmessage, confirmLink);
					
					map.put("message", "Successful Friend Request");
					//--------------------------------------------------------------//
					
					
				}//end of if (friendship.hasNext()) {...} else {...}
			}//if (friends.hasNext()) ... else
			//friend is not register with plash
			else {
				
				
				int fid = 0;
				String passcode = null;
				
				// CSuccessful Friend Requestheck if friend request is existed
				criteria = session.createCriteria(T_FriendRequest.class);
				criteria.add(Restrictions.eq("useraid", userid));
				criteria.add(Restrictions.eq("friendemail", friendemail));
				Iterator friendrequest = criteria.list().iterator();

				// if friend request is existed
				if (friendrequest.hasNext()) {
					map.put("message", "Friend Request Existed");
					
					T_FriendRequest friendlist = (T_FriendRequest) friendrequest.next();
					fid = friendlist.getFid();
					passcode = friendlist.getPasscode();
					map.put("fid", fid);
					
				}
				// if friend request is not existed
				else {
					//--------------------------------------------------------------//
					//since friend is not register with PLASH, set friendid = 0
					int friendid = 0;
					
					//Insert into friend request table
					criteria = session.createCriteria(T_FriendRequest.class);
					T_FriendRequest friendlist = new T_FriendRequest();
					
					friendlist.setUseraid(userid); // useraid
					friendlist.setUserbid(friendid); // userbid
					friendlist.setFriendemail(friendemail);

					// generate a random passcode/password for
					// activation of friend request
					passcode = getRandomString(10);
					friendlist.setPasscode(passcode);
					friendlist.setConfirmed(false);
					
					Transaction tx = session.beginTransaction();
					session.save(friendlist);
					tx.commit();
					
					fid = friendlist.getFid();
					map.put("fid", fid);
					//--------------------------------------------------------------//
					
				}
				
				//--------------------------------------------------------------//
				// create a Confirmation Link to confirm the friendship and invite friend join PLASH
				String host = "http://plash.iis.sinica.edu.tw/plash/";
				String action = "preinvite.action";
				String temp1 = "?fid=" + fid;
				String temp2 = "&userid=" + userid; // sid
				String temp3 = "&friendemail=" + friendemail;
				String temp4 = "&friendname=" + friendname;
				String temp5 = "&passcode=" + passcode;
				String parameter = temp1 + temp2 + temp3 + temp4 + temp5;
				String inviteLink = host + action + parameter;
				String personalmessage = null;
				
				// send an invitation to friend
				sendMail sendReq = new sendMail();
				String[] to = { friendemail };
				sendReq.sendInvitation(to, personalname, friendname,personalmessage, inviteLink);
				//--------------------------------------------------------------//
				
				map.put("message",	"Invite Friend to Register with Plash");
				
			}
		}//end of else
		session.close();

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Test End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
