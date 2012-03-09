package tw.edu.sinica.iis.ants.components;

import java.util.*;

import org.hibernate.Session;

import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class MergeTripComponent extends PLASHComponent {

	private Session tskSession; //task session
	
	@Override
	public Object serviceMain(Map map) {
		// TODO Auto-generated method stub
		System.out.println("MergeTripComponent Start:\t"	+ Calendar.getInstance().getTimeInMillis());
		
		tskSession = sessionFactory.openSession();
		
		return null;
	}

}//end class
