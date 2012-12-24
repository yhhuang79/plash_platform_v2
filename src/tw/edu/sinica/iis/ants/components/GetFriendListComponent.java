package tw.edu.sinica.iis.ants.components;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.PlashUtils;
import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_Login;

public class GetFriendListComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public GetFriendListComponent() {

	}

	public Object greet(Map map) {
		System.out.println("FriendList Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession(); 

		Criteria criteria = session.createCriteria(T_FriendList.class);
		Criteria criteriaOfFriendName;
		
		criteria.add(Restrictions.or(Restrictions.eq("useraid", Integer.parseInt(map.get("sid").toString())),Restrictions.eq("userbid", Integer.parseInt(map.get("sid").toString()))));
		//criteria.add(Restrictions.eq("password", map.remove("password").toString()));
		List friend_list = new ArrayList<Map>();
		Iterator fls = criteria.list().iterator(); 
		Map oneFriend;
		while(fls.hasNext()) {
			T_FriendList fl = (T_FriendList) fls.next();
			oneFriend = new HashMap();
			criteriaOfFriendName = session.createCriteria(T_Login.class);
			if(fl.getUseraid() == Integer.parseInt(map.get("sid").toString())){
				oneFriend.put("id", fl.getUserbid());
				criteriaOfFriendName.add(Restrictions.eq("sid", fl.getUserbid()));
				oneFriend.put("name", ((T_Login)criteriaOfFriendName.list().get(0)).getUsername());
				oneFriend.put("image", "http://developer.android.com/assets/images/icon_download.jpg");
				oneFriend.put("shareTripNum", PlashUtils.getShareTripNum(Integer.parseInt(map.get("sid").toString()), fl.getUserbid(), session)); 
			}
			else{
				oneFriend.put("id", fl.getUseraid());	
				criteriaOfFriendName.add(Restrictions.eq("sid", fl.getUseraid()));
				oneFriend.put("name", ((T_Login)criteriaOfFriendName.list().get(0)).getUsername());
				oneFriend.put("image", "http://developer.android.com/assets/images/icon_download.jpg");	
				oneFriend.put("shareTripNum", PlashUtils.getShareTripNum(Integer.parseInt(map.get("sid").toString()), fl.getUseraid(), session)); 
			}	
			friend_list.add(oneFriend);
		}
		session.close();
		map.put("friend_list", friend_list);

		System.out.println("FriendList End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;

	}
}
