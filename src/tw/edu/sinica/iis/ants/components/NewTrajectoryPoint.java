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
import tw.edu.sinica.iis.ants.db.common.*;


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
 * @param	map A map object that contains userid, trip_id, update_status and any of the items listed above 
 *

 * @version   1.2, 01/5/2012
 * @param     
 * @return    return status 
 * @example   https://localhost:8080/NewTrajectoryPoint?trajectory_id=1&record_time=2011-11-11 11:11:11.111&latitude=1.123456&longitude=2.34567
 * 
 */
public class NewTrajectoryPoint extends PLASHComponent {
	
	/**
	 * Fields
	 */
	private int trajectoryId;
	private Timestamp recordTime;
	private double latitude;
	private double longitude;
	private Float altitude;
	private Float accuracy;
	private Float speed;
	private Float bearing;
	private Float accelX;
	private Float accelY;
	private Float accelZ;
	private Float azimuth;
	private Float pitch;
	private Float roll;
	
		
	private Geometry gpsBinary;

	private String gsmInfo;
	private String wifiInfo;
	private Short application;
	private String deviceInfo;
	private String battery;
	private String imei;
	private String extra;
	private boolean hasDeviceInfo = false; //supplied parameters got device info?

	private Session tskSession; //task session



    public NewTrajectoryPoint() {

    }//end constructor


	@Override
	public Object serviceMain(Map map) {
    	

		try {
	        
	        if (map.containsKey("trajectory_id")) {
	        	trajectoryId = Integer.parseInt(map.get("trajectory_id").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "Trajectory ID must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi
	        if (map.containsKey("record_time"))  {
	        	recordTime = Timestamp.valueOf(map.get("record_time").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "Record time must be specified";
				return returnUnsuccess(map,err);     
	        }//fi
	        if (map.containsKey("latitude")) { 
	        	latitude = Double.parseDouble(map.get("latitude").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "latitude must be specified";
				return returnUnsuccess(map,err);     
	        }//fi
	        if (map.containsKey("longitude")) {     
	        	longitude = Double.parseDouble(map.get("longitude").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "longitude must be specified";
				return returnUnsuccess(map,err);     
	        }//fi
	        
	        //make PostGIS Geometry type  			
			gpsBinary = new WKTReader().read("POINT("+longitude+" "+latitude+")");
			gpsBinary.setSRID(4326);

	        
	
	    	//read point device data
	        if (map.containsKey("altitude")) {
	        	altitude = Float.valueOf(map.get("altitude").toString());
	        }//fi
	        if (map.containsKey("accuracy")) {
	        	accuracy = Float.valueOf(map.get("accuracy").toString());
	    	}//fi        
	        if (map.containsKey("speed")) {
	        	speed = Float.valueOf(map.get("speed").toString());
			}//fi
	        if (map.containsKey("bearing")) {
	        	bearing = Float.valueOf(map.get("bearing").toString());
	    	}//fi
	        if (map.containsKey("accel_x")) {
	        	accelX = Float.valueOf(map.get("accel_x").toString());
	    	}//fi
	        if (map.containsKey("acce_y")) {
	        	accelY = Float.valueOf(map.get("acce_y").toString());
	    	}//fi
	        if (map.containsKey("acce_z")) {
	        	accelZ = Float.valueOf(map.get("acce_z").toString());
	    	}//fi
	        if (map.containsKey("azimuth")) {
	        	azimuth = Float.valueOf(map.get("azimuth").toString());
	    	}//fi        
	        if (map.containsKey("pitch")) {
	        	pitch = Float.valueOf(map.get("pitch").toString());
	    	}//fi        
	        if (map.containsKey("roll")) {
	        	roll = Float.valueOf(map.get("roll").toString());
	    	}//fi
	        
	        if (map.containsKey("gsm_info")) {
	        	gsmInfo = map.get("gsm_info").toString();
	        	hasDeviceInfo = true;
	    	}//fi
	        if (map.containsKey("wifi_info")) {
	        	wifiInfo = map.get("wifi_info").toString();
	        	hasDeviceInfo = true;
	    	}//fi	             
	        if (map.containsKey("application")) {
	        	application = Short.valueOf(map.get("application").toString());
	        	hasDeviceInfo = true;
	    	}//fi
	        if (map.containsKey("device_info")) {
	        	deviceInfo = map.get("device_info").toString();
	        	hasDeviceInfo = true;
	    	}//fi
	        if (map.containsKey("battery")) {
	        	battery = map.get("battery").toString();
	        	hasDeviceInfo = true;
	    	}//fi	        
	        if (map.containsKey("imei")) {
	        	imei = map.get("imei").toString();
	        	hasDeviceInfo = true;
	    	}//fi	        
	        if (map.containsKey("extra")) {
	        	extra = map.get("extra").toString();
	        	hasDeviceInfo = true;
	    	}//fi	        

   
	        
    
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
	        	
		
		TrajectoryPoints pt = new TrajectoryPoints();

		pt.setTrajectoryId(trajectoryId);
		pt.setRecordTime(recordTime);
		pt.setLatitude(latitude);
		pt.setLongitude(longitude);		
		pt.setReceiveTime(new Timestamp(new java.util.Date().getTime()));


		pt.setAltitude(altitude);
		pt.setAccuracy(accuracy);
		pt.setSpeed(speed);
		pt.setBearing(bearing);
		pt.setAccelX(accelX);
		pt.setAccelY(accelY);
		pt.setAccelZ(accelZ);  
		pt.setAzimuth(azimuth);
		pt.setPitch(pitch);
		pt.setRoll(roll);
		
		
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
	
		PointBinaryData pbd = new PointBinaryData(pt.getPointId(),gpsBinary);
		PointDeviceData pdd = new PointDeviceData(
				pt.getPointId(),
				gsmInfo, 
				wifiInfo,
				application,
				deviceInfo,
				battery,
				imei,
				extra
				);			
        		
		
		try {
	        tskSession = sessionFactory.openSession();
			Transaction tx = tskSession.beginTransaction();
			tskSession.save(pbd);
			if (hasDeviceInfo) {
				tskSession.save(pdd);
			}//fi
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
