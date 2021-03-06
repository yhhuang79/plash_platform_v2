package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Map;
import java.lang.Math;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;

import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class CheckServerStatus extends PLASHComponent {

	private Session session; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("CheckServerStatus Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Function: Check Server Status  
		 * Author: Yu-Hsiang Huang
		 * Date: 12/28/2012
		 * Version:1.0
		 */
		session = sessionFactory.openSession();
		Statistics stats = sessionFactory.getStatistics();
		stats.setStatisticsEnabled(true);
	    map.put("code", 200);
		map.put("message", "ok");
		map.put("Number_of_connection_requests", stats.getConnectCount() +" / "+ stats.getFlushCount());
		map.put("Number_of_sessions_opened", stats.getSessionOpenCount() +" / "+ stats.getSessionCloseCount());
		map.put("Time_of_the_slowest_query", stats.getQueryExecutionMaxTime());
	    stats.logSummary();
	    session.close();
		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("CheckServerStatus End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
