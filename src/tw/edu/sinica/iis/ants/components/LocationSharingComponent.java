package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class LocationSharingComponent extends PLASHComponent{

	private Session session; //task session
	
	@Override
	public Object serviceMain(Map map) {

	    return map;
	}
}