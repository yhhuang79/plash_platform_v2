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

import tw.edu.sinica.iis.ants.db_pojo.linkus.LinkusUser;

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
 * @example  https://localhost:8080/GetLinkusUser?fbId=2798&lng=12&lat=12&preference=uni&radius=300
 */
public class GetLinkusUser extends PLASHComponent {
	
	/**
	 * Fields
	 */
	private String fbId;
	private String lng;
	private String lat;
    private Timestamp time;	
	private String preference;
	private String radius;

    

	private Session tskSession; //task session



    public GetLinkusUser() {

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

		     if (map.containsKey("preference")) {
				 preference = map.get("preference").toString();
		       } //fi
		     if (map.containsKey("radius")) {
				 radius = map.get("radius").toString();
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
	        	
		
		time = new Timestamp(new java.util.Date().getTime()) ;
		LinkusUser pt = new LinkusUser();

		pt.setFbId(fbId);
		pt.setLng(lng);		
		pt.setLat(lat);
		pt.setTime(time);
		pt.setPreference(preference);
		pt.setRadius(radius);
		


		
		try {
	        tskSession = sessionFactory.openSession();
			Transaction tx = tskSession.beginTransaction();
			tskSession.save(pt);
			tx.commit();
			int dummyString = (Integer) tskSession.createSQLQuery("SELECT geog_update('"+fbId+"','"+time+"')").uniqueResult();
			String createSQLQuery = new String("SELECT nearby_user('"+ fbId +"','"+preference+"','"+radius+"')");
			String resultString = (String)tskSession.createSQLQuery(createSQLQuery).uniqueResult();
			System.out.println(resultString);
			Map<String,Object>[] around = new Map[3];
			for (int i= 0; i < around.length; i++){
				   around[i]= new HashMap<String,Object>();
			        }	
			if(resultString==null){
			around[0].put("nearby_id",-1);	
			 }
			else{
			
			int StringSize= resultString.length();
			String temp;		
			StringTokenizer st = new StringTokenizer(resultString.substring(1, StringSize-1),",");
			int j=0;
			while(st.hasMoreTokens())
			{
			int i=0;
			
			StringTokenizer st1 = new StringTokenizer(st.nextToken(),"~");
			while(st1.hasMoreTokens()){
				
				switch(i){ 
				case 0:
                st1.nextToken();
				
				case 1:
				temp = st1.nextToken();
				around[j].put("nearby_id",temp);
				
				case 2:
				temp = st1.nextToken();
				around[j].put("education",temp.substring(0,temp.length()-2));
				
				}
		        i++;
				}
			   j++;
			}
		    map.put("data",around);
		   }
			
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
