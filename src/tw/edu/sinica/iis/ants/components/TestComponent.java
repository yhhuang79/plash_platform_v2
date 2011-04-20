package tw.edu.sinica.iis.ants.components;

import java.util.ArrayList;
import java.util.Calendar;
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

import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_FriendRequest;
import tw.edu.sinica.iis.ants.DB.T_Login;

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

		Map a = new HashMap();
		a.put("AA", "BB");
		a.put("CC", "DD");
		Map b = new HashMap();
		b.put("AA", "BB");
		b.put("CC", "DD");
		Map c = new HashMap();
		c.put("AA", "BB");
		c.put("CC", "DD");
		
		List l = new ArrayList();
		l.add(a);
		l.add(b);
		l.add(c);
		
		map.put("AAAAA", l);

		
		System.out.println("Test End:\t"
				+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
