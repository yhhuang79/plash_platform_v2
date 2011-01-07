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

public class TestComponent {

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
		System.out.println("Test Start:\t"
				+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Friend Request Action
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
		if (map.containsKey("userid")) {
			userid = Integer.parseInt(map.get("sid").toString());
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
		else if (!isValidUsername(friendname)) {
			map.put("message", "Friend's name invalid");
		} 
		// -------------------------------------------------------------------//
		// Pass all the format checks
		
		else {

			// Check if friend's e-mail address in the login table (friend
			// is registered user of Plash Project
			Criteria criteria = session.createCriteria(T_Login.class);
			criteria.add(Restrictions.eq("username", username));

			Iterator friends = criteria.list().iterator();
			
			// if friend is registered in Plash Project
			if (friends.hasNext()) {

				T_Login friend = (T_Login) friends.next();
				String existUsername = friend.getUsername();
				int friendid = friend.getSid();

				// Check if friendship is already existed.
				criteria = session.createCriteria(T_FriendList.class);
				criteria.add(Restrictions.eq("cid", friendid));
				Iterator friendship = criteria.list().iterator();

				// if friendship is existed
				if (friendship.hasNext()) {
					map.put("message", "Friendship Already Existed!");
				}

				// if friendship is not existed
				else {

					// Check if friend request is existed
					criteria = session.createCriteria(T_FriendRequest.class);
					criteria.add(Restrictions.eq("useraid", userid));
					criteria.add(Restrictions.eq("userbid", friendid));
					Iterator friendrequest = (Iterator) criteria.list();

					// if friend request is existed
					if (friendrequest.hasNext()) {
						map.put("message", "Friend Request Existed");
					}

					// if friend request is not existed
					else {
//						criteria = session.createCriteria(T_FriendRequest.class);
//						friendrequest = (Iterator) criteria.list();
//						T_FriendRequest friendlist = (T_FriendRequest) friendrequest.next();
//						friendlist.setUseraid(userid); // useraid
//						friendlist.setUserbid(friendid); // userbid
//						friendlist.setFriendemail(friendemail);
//
//						// generate a random passcode/password for
//						// activation of friend request
//						String passcode = getRandomString(10);
//						friendlist.setPasscode(passcode);
//
//						Transaction tx = session.beginTransaction();
//						session.save(friendlist);
//						tx.commit();

						map.put("message", "Successful Friend Request");
					}//end of else
				}//end of if (friendship.hasNext()) ... else
			}//if (friends.hasNext()) ... else
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
