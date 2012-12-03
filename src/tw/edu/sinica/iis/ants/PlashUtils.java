package tw.edu.sinica.iis.ants;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.DB.T_TripHash;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.DB.T_UserTrip;

public class PlashUtils {

	public static Map HashToParam(String hash, Session session){
		Map params = new HashMap();
		Criteria criteria = session.createCriteria(T_TripHash.class);
		criteria.add(Restrictions.eq("hash", hash));
		Iterator paramItr = criteria.list().iterator();
		System.out.println("Hash to Param Start:\t" + hash);
		if(paramItr.hasNext()) {
			T_TripHash paramRec = (T_TripHash) paramItr.next();
			params.put("userid", paramRec.getUserid());
			params.put("trip_id", paramRec.getTrip_id());
		}
		return params;
	}
	
	public static String ParamToHash(Integer userid, Integer trip_id, Session session){
		Criteria criteria = session.createCriteria(T_TripHash.class);
		criteria.add(Restrictions.eq("userid", userid));
		criteria.add(Restrictions.eq("trip_id", trip_id));
		Iterator paramItr = criteria.list().iterator();
		if(paramItr.hasNext()) {
			T_TripHash paramRec = (T_TripHash) paramItr.next();
			return paramRec.getHash();
		}
		return null;
	}
	
	private static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 
 
    public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    } 
    
	public static void tripinfoToHash(Session session) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		Criteria criteria = session.createCriteria(T_TripInfo.class);
		Iterator tripinfoItr = criteria.list().iterator(); 
		while(tripinfoItr.hasNext()) {
			T_TripInfo tti = (T_TripInfo) tripinfoItr.next();
			T_TripHash tth = new T_TripHash();
			tth.setId(tti.getId());
			tth.setTrip_id(tti.getTrip_id());
			tth.setUserid(tti.getUserid());
			String tst = "";
			if (tti.getTrip_st() != null)
				tst = tti.getTrip_st().toString();
			tth.setHash(MD5(tti.getId().toString() + "#" + tst));
			Transaction tx = session.beginTransaction();
			session.save(tth);
			tx.commit();			
		}
	}
    

	public static Map isTripShared(int userid, int trip_id, Session session){
		Map message = new HashMap();
		List<Integer> users = new ArrayList<Integer>();
		boolean isPublic = true; // token OK , change to false
		Criteria criteria = session.createCriteria(T_FriendAuth.class);
		criteria.add(Restrictions.eq("userAID", userid));
		criteria.add(Restrictions.eq("tripID", trip_id));		
		Iterator itr = criteria.list().iterator();
		while(itr.hasNext()) {
			T_FriendAuth rec = (T_FriendAuth) itr.next();
			if(rec.getUserBID() == 0)
				isPublic = true;
			users.add(rec.getUserBID());
		}
		message.put("isPublic", isPublic);
		message.put("users", users);
		return message;
	}
	
	public static int getNewTripId(int userid, Session session){
		int newTripId = 0;
		Criteria criteria = session.createCriteria(T_UserTrip.class); 
		criteria.add(Restrictions.eq("userid", userid));
		Iterator tripids = criteria.list().iterator();
		if (tripids.hasNext()){
			T_UserTrip tripid = (T_UserTrip) tripids.next();
			newTripId = tripid.getTrip_count() + 1;
			tripid.setTrip_count(newTripId);
	        Transaction tx = session.beginTransaction();
	        session.save(tripid);
	        tx.commit();			
		} else {
			newTripId = 1;
    		T_UserTrip tripid1 = new T_UserTrip();
			tripid1.setUserid(userid);
			tripid1.setTrip_count(newTripId);
	        Transaction tx = session.beginTransaction();
	        session.save(tripid1);
	        tx.commit();
		}
		return newTripId;		
	} // getNewTripId End
	
	public static Map login(String username, String password, Session session) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		Map message = new HashMap();
		Criteria criteria = session.createCriteria(T_Login.class);
		criteria.add(Restrictions.eq("username", username));
		criteria.add(Restrictions.eq("password", password));
		Iterator users = criteria.list().iterator(); 
		if(users.hasNext()) {
			T_Login user = (T_Login) users.next(); 
			
			if (user.isConfirmed()) {
				message.put("sid", user.getSid());
				Date date= new java.util.Date();
				message.put("token", MD5(user.getUsername() + date.getTime()));
				message.put("message", "Successful Login");				
			} else { //account is not activate
				//SHOUELD BE REQUESTED BY USER, DO NOT SEND AUTOMATICALLY
				message.put("sid", "0");
				message.put("message", "Inactivate");
			}
		} else {
			message.put("sid", "0");
			message.put("message", "Login Fail");
		}
		return message;		
	} // login End	
	
	public static String getTripName(int userid, int trip_id, Session session) {
    	Criteria criteria = session.createCriteria(T_TripInfo.class);
    	criteria.add(Restrictions.eq("userid", userid));
    	criteria.add(Restrictions.eq("trip_id", trip_id));
    	ProjectionList filterProjList = Projections.projectionList();   
    	filterProjList.add(Projections.property("trip_name"),"trip_name");
    	criteria.setProjection(filterProjList);    	
    	String tripName = null;
    	Iterator trips = criteria.list().iterator();
    	if(trips.hasNext()) {
    		Object tripInfo = trips.next();
    		tripName = tripInfo.toString();
    	}
    	return tripName;
	}//end method	
	
	public static String getUsername(int userid, Session session) {
    	Criteria criteria = session.createCriteria(T_Login.class);
    	criteria.add(Restrictions.eq("sid", userid));
    	ProjectionList filterProjList = Projections.projectionList();   
    	filterProjList.add(Projections.property("username"),"username");
    	criteria.setProjection(filterProjList);    	
    	String username = null;
    	Iterator users = criteria.list().iterator();
    	if(users.hasNext()) {
    		Object userInfo = users.next();
    		username = userInfo.toString();
    	}
    	return username;
	}//end method	
	
	
} // PlashUtils End 
