package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.DB.*;

public class DelTripComponent {


	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public DelTripComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"
				+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession();
		
		//select the rows using userid and tripid from user_point_location_time
	    Criteria criteria = session.createCriteria(T_UserPointLocationTime.class); 
	    criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString())));
	    criteria.add(Restrictions.eq("trip_id", Integer.parseInt(map.get("trip_id").toString())));
	    //insert the data to depreciated_location_data table
	    for(Object obj : criteria.list()) {
	    	T_UserPointLocationTime u = (T_UserPointLocationTime)obj;
	    	T_DepreciatedLocationData t = new T_DepreciatedLocationData();
	    	t.setId(u.getId());
	    	t.setGps(u.getGps());
	    	t.setLabel(u.getLabel());
	    	t.setTimestamp(u.getTimestamp());
	    	t.setServer_timestamp(u.getServer_timestamp());
	    	t.setTrip_id(u.getTrip_id());
	    	t.setUserid(u.getUserid());
	    	session.save(t);
	    	session.delete(obj); // delete data from original table
	    }
	    //delete the entry in trip_info table with the same userid and trip_id
	    criteria = session.createCriteria(T_TripInfo.class);
	    criteria.add(Restrictions.eq("userid", Integer.parseInt(map.get("userid").toString())));
	    criteria.add(Restrictions.eq("trip_id", Integer.parseInt(map.get("trip_id").toString())));
	    for(Object obj : criteria.list()) {
	    	session.delete(obj);
	    }
		
		Transaction tx = session.beginTransaction();
		tx.commit();
		
		System.out.println("Test End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}//end class
