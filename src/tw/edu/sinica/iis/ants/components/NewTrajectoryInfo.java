package tw.edu.sinica.iis.ants.components;

import java.sql.*;
import java.util.*;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;

import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db.common.*;



/**
 *
 * This component receives trajectory info, creates a new record and stores them on the appropriate place of database. <br>
 * 
 * The following parameters are required: <br>
 * trajectory_id : Required. This parameter indicates the id of the trajectory <br>
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
	 
 *  
 * @author  Yi-Chun Teng
 * @param	map A map object that contains userid, trip_id, update_status and any of the items listed above 
 *

 * @version   1.2, 01/5/2012
 * @param     
 * @return    return status 
 * @example   https://localhost:8080/NewTrajectoryInfo?upload_status=1
 * 
 */
public class NewTrajectoryInfo extends PLASHComponent {
	
	/**
	 * Fields
	 */
	private Short updateStatus;


	private Session tskSession; //task session



    public NewTrajectoryInfo() {

    }//end constructor


	@Override
	public Object serviceMain(Map map) {
    	

		try {
	        
	       			
	        if (map.containsKey("update_status"))  {
	        	updateStatus = Short.valueOf(map.get("update_status").toString());
	        } else {
	        	updateStatus = 0;
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
		}//end try catch
	        	
		
		TrajectoryInfo ti = new TrajectoryInfo();

		ti.setUpdateStatus(updateStatus);
		
		try {
	        tskSession = sessionFactory.openSession();
			Transaction tx = tskSession.beginTransaction();
			tskSession.save(ti);	
			tx.commit();
	        tskSession.close();
	        map.put("trajectory_id", ti.getTrajectoryId());
	        return 	returnSuccess(map);
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
	        err.explaination = e.toString();
	        e.printStackTrace();
			return returnUnsuccess(map,err);			
		}//end try
	
        

	} //end serviceMain
    
	/**
	 * Initialize the variables. <br>
	 * This method is used to set default values for all fields 
	 * 
	 */
	private void initFields() {


	}//end method
}  //close class
