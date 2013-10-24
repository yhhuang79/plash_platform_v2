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
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db_pojo.common.PointBinaryData;
import tw.edu.sinica.iis.ants.db_pojo.common.PointDeviceData;
import tw.edu.sinica.iis.ants.db_pojo.common.TrajectoryPoints;

import tw.edu.sinica.iis.ants.db_pojo.linkus.LinkusInteraction;


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
 * @example  https://localhost:8080/NewLinkusInteraction?fbId=1229830845&lng=12&lat=12&candidateId=uni&status=300
 */
public class NewLinkusInteraction extends PLASHComponent {
	
	/**
	 * Fields
	 */
	private String fbId;
	private String lng;
	private String lat;
	private String candidateId;
	private int status;

    

	private Session tskSession; //task session



    public NewLinkusInteraction() {

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

		     if (map.containsKey("candidateId")) {
				 candidateId = map.get("candidateId").toString();
		       } //fi
		     if (map.containsKey("status")) {
		    	 status=Integer.parseInt(map.get("status").toString());
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
		}
	        	
		
	
		LinkusInteraction pt = new LinkusInteraction();

		pt.setFbId(fbId);
		pt.setLng(lng);		
		pt.setLat(lat);
		pt.setTime(new Timestamp(new java.util.Date().getTime()));
		pt.setCandidateId(candidateId);
		pt.setStatus(status);
		


		
		try {
	        tskSession = sessionFactory.openSession();
	        Criteria interactionInfo = tskSession.createCriteria(LinkusInteraction.class);
			interactionInfo.add(Restrictions.eq("candidateId", fbId));
			interactionInfo.add(Restrictions.eq("fbId", candidateId));
			interactionInfo.add(Restrictions.eq("status",1));
			LinkusInteraction interactionInfoRec = (LinkusInteraction) interactionInfo.uniqueResult();
	        if(interactionInfoRec != null){
	          map.put("matched_id0",fbId);
	          map.put("matched_id1",candidateId);}
	        else{
	          map.put("matched_id0",-1);}
			Transaction tx = tskSession.beginTransaction();
			tskSession.save(pt);
			tx.commit();
						
			
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
	        System.out.println("ininink"+map.toString());
			return returnUnsuccess(map,err);		
		} finally{
			if (tskSession.isOpen()) {
			tskSession.close();
			}
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
