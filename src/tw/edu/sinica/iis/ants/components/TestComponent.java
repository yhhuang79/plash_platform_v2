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
import org.hibernate.criterion.Restrictions;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.DB.*;

public class TestComponent {


	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public TestComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"
				+ Calendar.getInstance().getTimeInMillis());

		
		//store the <userid> into the database
		T_TripInfo t = new T_TripInfo();
		t.setUserid(123);
		
		//store the <trip_id> into the database
		t.setTrip_id(456);
	
		//get the value of <server_timestamp>, and store it into the database
		t.setTrip_st(new Timestamp(new Date().getTime()));
		
        Session session = sessionFactory.openSession();						
		Transaction tx = session.beginTransaction();
		session.save(t);
		tx.commit();

		
		System.out.println("Test End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}//end class
