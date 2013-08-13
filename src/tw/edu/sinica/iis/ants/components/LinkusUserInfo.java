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

import tw.edu.sinica.iis.ants.db_pojo.linkus.UserInfo;
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
 * @example   https://localhost:8080/LinkusUserInfo?trajectory_id=1&record_time=2011-11-11 11:11:11.111&latitude=1.123456&longitude=2.34567
 * 
 */
public class LinkusUserInfo extends PLASHComponent {
	
	/**
	 * Fields
	 */
	private String fbId;
	private String fbToken;
	private String name;
	private String education;
	private String bday;
	private String gender;
	private String workExp;
	private String lon;
	private String lat;
	private Timestamp time;

	private Session tskSession; //task session



    public LinkusUserInfo() {

    }//end constructor


	@Override
	public Object serviceMain(Map map) {
    	

		try {
	        
			 if (map.containsKey("fbId")) {
				 fbId = map.get("fbId").toString();
		       } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "fbId must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi
			 if (map.containsKey("fbToken")) {
				 fbToken = map.get("fbToken").toString();
		       } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "fb must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi
			
			 if (map.containsKey("education")) {
				 education = map.get("education").toString();
		       } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "education must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi
			
		     if (map.containsKey("lat")) { 
		        	lat = map.get("lat").toString();
		        } else {
					getElapsed();
			        AbnormalResult err = new AbnormalResult(this,'E');
			        err.refCode = 001;
			        err.explaination = "latitude must be specified";
					return returnUnsuccess(map,err);     
		        }//fi
		     if (map.containsKey("lon")) {     
		        	lon = map.get("lon").toString();
		        } else {
					getElapsed();
			        AbnormalResult err = new AbnormalResult(this,'E');
			        err.refCode = 001;
			        err.explaination = "longitude must be specified";
					return returnUnsuccess(map,err);     
		        }//fi
		     if (map.containsKey("time"))  {
	        	time = Timestamp.valueOf(map.get("time").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "time must be specified";
				return returnUnsuccess(map,err);     
	        }//fi

		     if (map.containsKey("name")) {
				 name = map.get("name").toString();
		       } //fi
		     if (map.containsKey("bday")) {
				 bday = map.get("bday").toString();
		       }//fi
			 if (map.containsKey("gender")) {
				 gender = map.get("gender").toString();
		       } //fi
			 if (map.containsKey("workExp")) {
				 workExp = map.get("workExp").toString();
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
		} catch (ParseException e) {
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 005;
	        err.explaination = "ParseException occured, probably due to invalid argument";
			return returnUnsuccess(map,err);
		} //end try
        // end//end try catch
	        	
		
		UserInfo pt = new UserInfo();

		pt.setfbId(fbId);
		pt.setfbToken(fbToken);
		pt.seteducation(education);
		pt.setlon(lon);		
		
        pt.setlat(lat);
		pt.settime(time);
		pt.setname(name);
		pt.setbday(bday);
		pt.setgender(gender);
		pt.setworkExp(workExp);

		
		try {
	        tskSession = sessionFactory.openSession();
			Transaction tx = tskSession.beginTransaction();
			tskSession.save(pt);
			tx.commit();
	        tskSession.close();
		} catch (ConstraintViolationException e) {
			tskSession.close();
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 004;
	        err.explaination = "Insert or update on table violates foreign key constraint";
			return returnUnsuccess(map,err);			
		} catch (Exception e){
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
