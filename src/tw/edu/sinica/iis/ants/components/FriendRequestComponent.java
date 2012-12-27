package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.sendMail;
/**
 * This component handles friend requests
 * @Author: Yao H. (Danny) Ho
 */
public class FriendRequestComponent extends PLASHComponent{

	public String getRandomString(int length) {

		// final String charset =
		// "!0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String charset = "0123456789";
		Random rand = new Random(System.currentTimeMillis());

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int pos = rand.nextInt(charset.length());
			sb.append(charset.charAt(pos));
		}//rof

		return sb.toString();
	}//end method

	public boolean isValidEmailAddress(String emailAddress) {
		String expression = "^[\\w\\_-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}//end method

	public static boolean isValidUsername(String username) {
		String expression = "^[A-Za-z0-9_]{6,20}$";
		CharSequence inputStr = username;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}




	public FriendRequestComponent() {

	}

	@Override
	public Object serviceMain(Map map) {
		tskSession = sessionFactory.openSession();
		

		int userID, friendUserID;	
		String tmpUserID, friendName, userName, friendEmail, personalName;

		try {



			if ((tmpUserID = (String)map.remove("user_id")) == null) {				

		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "User id must be specified";
				//return returnUnsuccess(map,err);
		        return map;
		        
			} else {
				userID = Integer.parseInt(tmpUserID);
		    	Criteria criteriaNameData = tskSession.createCriteria(T_Login.class);
		    	criteriaNameData.add(Restrictions.eq("sid", userID));
		    	ProjectionList filterProjList = Projections.projectionList();  
		    	filterProjList.add(Projections.property("username"),"name");
		    	criteriaNameData.setProjection(filterProjList);
		    	userName = (String) criteriaNameData.uniqueResult();		    								
		    	personalName = userName;
			}//fi
						
			
			if ((friendName = (String)map.remove("friend_name")) == null) {
				
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "Name field must be specified";
				//return returnUnsuccess(map,err);
		        return map;								
			
				
			} else {
				
		    	Criteria criteriaNameData = tskSession.createCriteria(T_Login.class);
		    	criteriaNameData.add(Restrictions.eq("username", friendName));
		    	T_Login tmpFriendRecord = (T_Login)criteriaNameData.uniqueResult();
		    	friendEmail = tmpFriendRecord.getEmail();
		    	friendUserID = tmpFriendRecord.getSid();
		    	
			}//fi
			System.out.println("user name: " + userName);
			System.out.println("friend email: " + friendEmail);


			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 002;
	        err.explaination = "NullPointerException, most likely due to invalid parameters";
	        System.out.println(err.explaination);
	        return map;		
			//return returnUnsuccess(map,err);

			
		} catch (NumberFormatException e) { //invalid arguments 
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 002;
	        err.explaination = "NumberFormatException, most likely due to invalid parameters";
	        System.out.println(err.explaination);
	        return map;		
			//return returnUnsuccess(map,err);
		} catch (HibernateException he) {
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 002;
	        err.explaination = "HibernateException, please check the validity of user and trip ids and database integrity";
	        System.out.println(err.explaination);
	        return map;		
			//return returnUnsuccess(map,err);

		}//end try catch			//*/
		
		
		
		// Check user input begin
		if (friendEmail.equals("")) {
			map.put("code", 400);
			map.put("message", "Friend email is empty");
		} else if (friendName.equals("")) {
			map.put("code", 400);			
			map.put("message", "Friend name is empty");
		} else if (!isValidEmailAddress(friendEmail)) {		// Validate e-mail format
			map.put("code", 400);
			map.put("message", "E-mail invalid");
		} else {

	
			// Check if friendship is already existed.
			Criteria criteria = tskSession.createCriteria(T_FriendList.class);
			criteria.add(Restrictions.eq("useraid", friendUserID));
			criteria.add(Restrictions.eq("userbid", friendUserID));
			Iterator friendship = criteria.list().iterator();
	
			// if friendship is existed
			if (friendship.hasNext()) {
				map.put("code", 400);
				map.put("message", "Friendship Already Existed!");
			} else {				// if friendship is not existed

				int fid = 0;
				String passcode = null;
				
				// Check if friend request is existed
				criteria = tskSession.createCriteria(T_FriendRequest.class);
				criteria.add(Restrictions.eq("useraid", userID));
				criteria.add(Restrictions.eq("userbid", friendUserID));
				Iterator friendrequest = criteria.list().iterator();

				// if friend request is existed
				if (friendrequest.hasNext()) {
					//map.put("message", "Friend Request Existed");
					
					T_FriendRequest friendlist = (T_FriendRequest) friendrequest.next();
					fid = friendlist.getFid();
					passcode = friendlist.getPasscode();
					//map.put("fid", fid);
					
				}	else {// if friend request is not existed
					

					//Insert into friend request table
					criteria = tskSession.createCriteria(T_FriendRequest.class);
					

					
					T_FriendRequest friendlist = new T_FriendRequest();
					
					friendlist.setUseraid(userID); // useraid
					friendlist.setUserbid(friendUserID); // userbid
					friendlist.setFriendemail(friendEmail);

					// generate a random passcode/password for
					// activation of friend request
					passcode = getRandomString(10);
					friendlist.setPasscode(passcode);
					boolean isConfirmed = false;
					friendlist.setConfirmed(isConfirmed);
					
					System.out.println("---------FriendRequestComponent: (" + userID
							+ "," + friendUserID + "," + friendEmail + ","
							+ passcode+","+isConfirmed+")");
					Transaction tx = tskSession.beginTransaction();
					tskSession.save(friendlist);
					tx.commit();
					
					//fid = friendlist.getFid();
					//map.put("fid", fid);
					//--------------------------------------------------------------//
					
					
				}//end of if (friendrequest.hasNext()) {...} else {...}
				
				//--------------------------------------------------------------//
				// create a Confirmation Link to confirm the friendship
				//String host = "http://plash.iis.sinica.edu.tw/plash/";
				//String action = "confirmfriendrequest.action";
				/*String host = "https://plash.iis.sinica.edu.tw:8080/";
				String action = "ConfirmFriendRequest";
				String temp1 = "?fid=" + fid;
				// String temp2 = "&userid="+userid; //sid
				// String temp3 = "&friendid="+friendid; //friend's sid
				String temp4 = "&friendName=" + friendName;
				String temp5 = "&passcode=" + passcode;
				String temp6 = "&deviceType=browser";
				String parameter = temp1 + temp4 + temp5 + temp6;
				String confirmLink = host + action + parameter;

				// send a Friend Request e-mail to the friend
				sendMail sendReq = new sendMail();
				String[] to = { friendEmail };
				String personalmessage = null;
				sendReq.sendFriendRequest(to, personalName, friendName,	personalmessage, confirmLink);
				*/
				if(fid != 0){
					map.put("code", 200);
					map.put("fid", fid);
					map.put("message", "Successful Friend Request");
				} else {
					map.put("code", 400);
					map.put("message", "Friend Request Error");					
				}
				//--------------------------------------------------------------//
				
				
			}//end of if 
			
		}//end if else //*/
			
		tskSession.close();


		return map;
	}//end method


}//end class
