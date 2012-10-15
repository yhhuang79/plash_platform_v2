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

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_FriendRequest;
import tw.edu.sinica.iis.ants.DB.T_Login;

public class GetFriendRequestListComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public GetFriendRequestListComponent() {

	}

	public Object greet(Map map) {
		System.out.println("FriendRequestList Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession(); 

		Criteria criteria = session.createCriteria(T_FriendRequest.class);
		Criteria criteriaOfFriendName;
		
		//criteria.add(Restrictions.or(Restrictions.eq("useraid", Integer.parseInt(map.get("sid").toString())),Restrictions.eq("userbid", Integer.parseInt(map.get("sid").toString()))));
		criteria.add(Restrictions.eq("userbid", Integer.parseInt(map.remove("sid").toString())));
		//criteria.add(Restrictions.eq("password", map.remove("password").toString()));
		List friendrequest_list = new ArrayList<Map>();
		Iterator frls = criteria.list().iterator(); 
		Map oneFriendRequest;
		while(frls.hasNext()) {
			T_FriendRequest frl = (T_FriendRequest) frls.next();
			oneFriendRequest = new HashMap();
			criteriaOfFriendName = session.createCriteria(T_Login.class);
			if(frl.getUserbid() != 0){
				oneFriendRequest.put("fid", frl.getFid());
				oneFriendRequest.put("id", frl.getUserbid());
				oneFriendRequest.put("passcode", frl.getPasscode());
				criteriaOfFriendName.add(Restrictions.eq("sid", frl.getUseraid()));
				oneFriendRequest.put("name", ((T_Login)criteriaOfFriendName.list().get(0)).getUsername());
				oneFriendRequest.put("image", "http://developer.android.com/assets/images/icon_download.jpg");
			//}else{
			//	oneFriend.put("id", fl.getUseraid());	
			//	criteriaOfFriendName.add(Restrictions.eq("sid", fl.getUseraid()));
			//	oneFriend.put("name", ((T_Login)criteriaOfFriendName.list().get(0)).getUsername());
			//	oneFriend.put("image", "http://developer.android.com/assets/images/icon_download.jpg");	
			}	
			friendrequest_list.add(oneFriendRequest);
		}
		session.close();
		map.put("friendrequest_list", friendrequest_list);

		System.out.println("FriendRequestList End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;

	}
}
