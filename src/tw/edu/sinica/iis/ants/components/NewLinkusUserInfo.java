package tw.edu.sinica.iis.ants.components;

import java.sql.*;
import java.util.*;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db_pojo.common.PointBinaryData;
import tw.edu.sinica.iis.ants.db_pojo.common.PointDeviceData;
import tw.edu.sinica.iis.ants.db_pojo.common.TrajectoryPoints;

import tw.edu.sinica.iis.ants.db_pojo.linkus.LinkusUserInfo;

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
 * @example  https://localhost:8080/NewLinkusUserInfo?fbId=2798&fbToken=12891&education=30&lng=12&lat=12&links=mary&bday=1950-08-01&gender=female
 */
public class NewLinkusUserInfo extends PLASHComponent {
	
	/**
	 * Fields
	 */
	private String fbId;
	private String fbToken;
	private String links;
	private String education;
	private String bday;
	private String gender;
	private String lng;
	private String lat;

	private Session tskSession; //task session



    public NewLinkusUserInfo() {

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
			 if (map.containsKey("fbToken")) {
				 fbToken = map.get("fbToken").toString();
		       } /*else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "fb must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi*/
			
			 if (map.containsKey("education")){
				  education = map.get("education").toString();
				 if (education.length()==0){
					    education = "Secret";}
		       }
			 //fi */
			
		     if (map.containsKey("lat")) { 
		        	lat = map.get("lat").toString();
		        } /*else {
					getElapsed();
			        AbnormalResult err = new AbnormalResult(this,'E');
			        err.refCode = 001;
			        err.explaination = "latitude must be specified";
					return returnUnsuccess(map,err);     
		        }//fi */
		     if (map.containsKey("lng")) {     
		        	lng = map.get("lng").toString();
		        } /*else {
					getElapsed();
			        AbnormalResult err = new AbnormalResult(this,'E');
			        err.refCode = 001;
			        err.explaination = "longitude must be specified";
					return returnUnsuccess(map,err);     
		        }//fi */

		     if (map.containsKey("links")) {
				 links = map.get("links").toString();
		       } //fi
		     if (map.containsKey("bday")) {
				 bday = map.get("bday").toString();
		       }//fi
			 if (map.containsKey("gender")) {
				 gender = map.get("gender").toString();
		       } //fi
	
    
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
	        	
		
		LinkusUserInfo pt = new LinkusUserInfo();

		pt.setFbId(fbId);
		pt.setFbToken(fbToken);
		pt.setEducation(education);
		pt.setLng(lng);		
		
        pt.setLat(lat);
		pt.setTime(new Timestamp(new java.util.Date().getTime()));
		pt.setLinks(links);
		pt.setBday(bday);
		pt.setGender(gender);


		
		try {
	        tskSession = sessionFactory.openSession();
			Transaction tx = tskSession.beginTransaction();
			tskSession.saveOrUpdate(pt);
			tx.commit();
            //String resultString = (String)tskSession.createSQLQuery("SELECT to_geog_update('"+fbId+"')").uniqueResult();
		
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
		}finally{
			tskSession.close();
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
