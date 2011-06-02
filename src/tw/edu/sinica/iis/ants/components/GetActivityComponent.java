package tw.edu.sinica.iis.ants.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.vividsolutions.jts.geom.Geometry;

import tw.edu.sinica.iis.ants.DB.T_Activity;
import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_Login;

public class GetActivityComponent extends PLASHComponent {

	@Override
	public Object theMainLogic(Map map) {
        if(!map.containsKey("activityid") || map.get("activityid").toString().equals("")){
        	map.put("message", "Lacking of parameters or required information");
        	return map;        
        }

		System.out.println("GetActivityComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession(); 

		Criteria criteria = session.createCriteria(T_Activity.class);
		
		criteria.add(Restrictions.eq("id", Integer.parseInt(map.get("activityid").toString())));
		
		List theResultList = criteria.list();
		
		if(theResultList.size()<1){
			map.put("message", "No such activity");
		}
		else{
			T_Activity activity = (T_Activity) theResultList.get(0);
			map.put("userid", activity.getUserid().toString());
			map.put("name", activity.getName());
			map.put("timestamp", activity.getTimestamp().toString());
			String gps = activity.getGps().toString().substring(6);
			System.out.println(gps);
			String lng = gps.substring(gps.indexOf('(')+1, gps.indexOf(' '));
			String lat = gps.substring(gps.indexOf(' ')+1, gps.indexOf(')'));	
			map.put("lat", lat);
			map.put("lng", lng);
			map.put("image", activity.getImage());
		}
		
		session.close();

		System.out.println("GetActivityComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
	
}
