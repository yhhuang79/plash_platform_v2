package tw.edu.sinica.iis.ants.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.lang.Math;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_FriendAuth;
import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class GetTripShareUserComponent extends PLASHComponent {

	private Session session; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("GetTripShareUser Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Get Trip share user 
		 * Author: Yu-Hsiang Huang
		 * Date: 10/25/2012
		 * Version:1.0
		 */
		session = sessionFactory.openSession(); 
		try{
			Integer userid = Integer.parseInt(map.remove("userid").toString());
			Integer trip_id = Integer.parseInt(map.remove("trip_id").toString());
			Integer friend_id = null;
			
			if(map.containsKey("friend_id")){
				friend_id = Integer.parseInt(map.remove("friend_id").toString());
			}
			Criteria criteria = session.createCriteria(T_FriendAuth.class);
			Criteria criteriaOfFriendName;
			criteria.add(Restrictions.eq("userAID", userid));
			criteria.add(Restrictions.eq("tripID", trip_id));
			if(friend_id != null){
				criteria.add(Restrictions.eq("userBID", friend_id));
			}						
			List friend_list = new ArrayList<Map>();
			Iterator itr = criteria.list().iterator(); 
			Map oneFriend;
			boolean isShareTrip = false;
			while(itr.hasNext()) {
				if(friend_id == null){
					T_FriendAuth fl = (T_FriendAuth) itr.next();
					oneFriend = new HashMap();
					criteriaOfFriendName = session.createCriteria(T_Login.class);
					oneFriend.put("id", fl.getUserBID());
					criteriaOfFriendName.add(Restrictions.eq("sid", fl.getUserBID()));
					oneFriend.put("name", ((T_Login)criteriaOfFriendName.list().get(0)).getUsername());
					oneFriend.put("image", "http://developer.android.com/assets/images/icon_download.jpg");
					friend_list.add(oneFriend);
				} else {
					isShareTrip = true;
					break;
				}
			}
			if(friend_id != null){
				if(isShareTrip){
					map.put("isShareTrip", 1);
				} else {
					map.put("isShareTrip", 0);
				}
			} else {
				map.put("TripShareUser", friend_list);
				map.put("ShareUserNum", getShareUserNum(userid, trip_id));
			}
			session.close();
		} catch (NullPointerException ne) {
			map.put("message", "error");
		}

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("GetTripShareUser End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
	
	private Integer getShareUserNum(int userid, int tripid) {
		
		//obtain the record
    	Criteria criteriaShareUserNum = session.createCriteria(T_FriendAuth.class);
		criteriaShareUserNum.add(Restrictions.eq("userAID", userid));
		criteriaShareUserNum.add(Restrictions.eq("tripID", tripid));
    	ProjectionList filterProjList = Projections.projectionList();     	
    	criteriaShareUserNum.setProjection(Projections.rowCount());
    	int shareUserNum = 0;
		try {
			List shareUserNums = criteriaShareUserNum.list();
			Iterator it = shareUserNums.iterator();
			  if (!it.hasNext()){
				  shareUserNum = 0;
			  } else {
				  while(it.hasNext()){
					  Object count = it.next();
					  shareUserNum = Integer.parseInt(count.toString());  
				  }
			  }
			return shareUserNum;
											
		} catch (HibernateException he) {
			
			return 0;
		}//end try catch			//*/
		
	}//end method	
	
}
