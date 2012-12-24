package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Map;
import java.lang.Math;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class UploadTripComplete extends PLASHComponent {

	private Session tskSession; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("UploadTripComplete  Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Set Upload Trip Complete 
		 * Author: Yu-Hsiang Huang
		 * Date: 12/24/2012
		 * Version:1.0
		 */
		tskSession = sessionFactory.openSession(); 
		try{
			String userid = map.get("userid").toString();
			String trip_id = map.get("trip_id").toString();
				
			Criteria criteria = tskSession.createCriteria(T_TripInfo.class);
	    	criteria.add(Restrictions.eq("userid", Integer.parseInt(userid)));
	    	criteria.add(Restrictions.eq("trip_id", Integer.parseInt(trip_id)));
	    	
			T_TripInfo trips = (T_TripInfo) criteria.list().iterator().next();
			
			Transaction tx = tskSession.beginTransaction();
			T_TripInfo trip = (T_TripInfo) tskSession.get(T_TripInfo.class, trips.getId());
			trip.setIs_completed(true);
			tskSession.update(trip);
			tx.commit();
	        tskSession.close();
	        map.put("code", 200);
			map.put("message", "ok");			
		} catch (NullPointerException ne) {
			map.put("code", 400);
			map.put("message", "fail");
		}

		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("UploadTripComplete End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
