package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Map;

import org.hibernate.Session;

public class MergeTripComponent extends PLASHComponent {

	private Session tskSession; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("MergeTripComponent Start:\t"	+ Calendar.getInstance().getTimeInMillis());
		
		tskSession = sessionFactory.openSession();
		
		return null;
	}

}
