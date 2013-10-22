package tw.edu.sinica.iis.ants.components;

import java.sql.*;
import java.util.*;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db_pojo.common.PointBinaryData;
import tw.edu.sinica.iis.ants.db_pojo.common.PointDeviceData;
import tw.edu.sinica.iis.ants.db_pojo.common.TrajectoryPoints;

import tw.edu.sinica.iis.ants.db_pojo.linkus.LinkusLocations;

/**
 *
 * This component receives trajectory point data, creates a new record and stores them on the appropriate place of database. <br>
 * 
 * The following parameters are required: <br>
 * trajectory_id : Required. This parameter indicates which trajectory the input point belongs to<br>
 * record_time: Required. This parameter indicates recording time <br>
 * latitude: Required. This parameter indicates this point's latitude <br>
 * longitude: Required. This parameter indicates this point's longitude <br>
 * 				
 * Optional arguments: Available arguments are as follows: <br>
 * 	trajectory_id <br>
 * 	record_time <br>
 * 	latitude <br>
 *	longitude <br>
 * 	altitude <br>
 * 	accuracy <br>
 * 	speed <br>
 * 	bearing <br>
 * 	accel_x <br>
 * 	accel_y <br>
 * 	accel_z <br>
 * 	azimuth <br>
 * 	pitch <br>
 * 	roll <br>
 *	gsmInfo <br>
 *	wifiInfo <br>
 * 	application <br>
 *  deviceInfo <br>
 *  battery <br>
 *  imei <br>
 *  extra <br>	 
 *  
 * @author  Yi-Chun Teng
 * @param	map A map object that contains trajectory_id, record_time, latitude, longitude and/or any of the items listed above 
 *

 * @version   1.3, Nov 15/2012
 * @param     
 * @return    return status 
 * @example  https://localhost:8080/NewLinkusLocations?fbId=2798&locationId=12891&locationName=30&lng=90&lat=30
 */
public class NewLinkusLocations extends PLASHComponent {
	
	/**
	 * Fields
	 */
	private String fbId;
	private String locationId;
	private String locationName;
	private String lng;
	private String lat;


	private Session tskSession; //task session



    public NewLinkusLocations() {
    }//end constructor


	@Override
	public Object serviceMain(Map map) {
    	

		try {
	        
			 if (map.containsKey("fbId")) {
				 fbId = map.get("fbId").toString();
		       } /*else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "fbId must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi */
			 if (map.containsKey("locationId")) {
				 locationId = map.get("locationId").toString();
		       } /*else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "fb must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi*/
			
			 if (map.containsKey("locationName")) {
				 locationName = map.get("locationName").toString();
		       } /*else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "education must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi */
			 if (map.containsKey("lng")) {
				 lng = map.get("lng").toString();
		       } /*else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "education must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi */
			 if (map.containsKey("lat")) {
				 lat = map.get("lat").toString();
		       } /*else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "education must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi */
			
		    
	
    
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 003;
	        err.explaination = "NullPointerException occured, probably due to invalid argument";
			return returnUnsuccess(map,err);					
	
		} catch (NumberFormatException e) { //invalid arguments 
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 003;
	        err.explaination = "NumberFormatException occured, probably due to invalid argument";
			return returnUnsuccess(map,err);			
		}
	        	
		
		LinkusLocations pt = new LinkusLocations();

		pt.setFbId(fbId);
		pt.setLocationId(locationId);
		pt.setLocationName(locationName);
		pt.setLng(lng);
		pt.setLat(lat);
		
		
		try {
			
			
	
				//Check whether such locationId exists or not and is updated or not
			
			tskSession = sessionFactory.openSession();
			Criteria locationInfo = tskSession.createCriteria(LinkusLocations.class);
			locationInfo.add(Restrictions.eq("fbId", fbId));
			locationInfo.add(Restrictions.eq("locationId", locationId));
			LinkusLocations locationInfoRec = (LinkusLocations) locationInfo.uniqueResult();
			if (locationInfoRec == null) {	
					Transaction tx = tskSession.beginTransaction();
					tskSession.save(pt);
					tx.commit();
					String resultString = (String)tskSession.createSQLQuery("SELECT fb_geog_update('"+fbId+"','"+locationId+"')").uniqueResult();
					tskSession.close();
				}//fi				
			
			
			
		} catch (ConstraintViolationException e) {
			tskSession.close();
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 004;
	        err.explaination = "Insert or update on table violates foreign key constraint";
			return returnUnsuccess(map,err);			
		} catch (Exception e){
			e.printStackTrace();
			tskSession.close();
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 005;
	        err.explaination = "Database session error";
			return returnUnsuccess(map,err);			
		}//end try
	
	
		

		return 	returnSuccess(map);
			
	} //end serviceMain
    
	/**
	 * Initialize the variables. <br>
	 * This method is used to set default values for all fields 
	 * 
	 */
	private void initFields() {


	}//end method
}  //close class
