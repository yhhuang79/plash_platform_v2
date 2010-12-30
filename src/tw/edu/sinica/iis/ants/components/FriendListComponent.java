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
import tw.edu.sinica.iis.ants.DB.T_Login;

public class FriendListComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public FriendListComponent() {

	}

	public Object greet(Map map) {
		System.out.println("FriendList Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession(); 

		Criteria criteria = session.createCriteria(T_FriendList.class);
		criteria.add(Restrictions.or(Restrictions.eq("useraid", Integer.parseInt(map.get("sid").toString())),Restrictions.eq("userbid", Integer.parseInt(map.get("sid").toString()))));
		//criteria.add(Restrictions.eq("password", map.remove("password").toString()));
		List friend_list = new ArrayList();
		Iterator fls = criteria.list().iterator(); 
		while(fls.hasNext()) {
			T_FriendList fl = (T_FriendList) fls.next(); 
			if(fl.getUseraid() == Integer.parseInt(map.get("sid").toString())){
				friend_list.add(fl.getUserbid());
			}
			else{
				friend_list.add(fl.getUseraid());				
			}	
		}
		session.close();
		map.put("friend_list", friend_list);

		System.out.println("FriendList End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;

	}
}
