package tw.edu.sinica.iis.ants.components;

import java.sql.*;
import java.util.*;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;
import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db_pojo.common.UserSpecified;


/**
 *
 * This component receives user specified data associated with a trajectory point, creates a new record and stores them on the appropriate place of database. <br>
 * 
 * 
* 
 * The following parameters are required: <br>
 * point_id : Required. This parameter indicates which point the input data belongs to<br>
 * 				
 * Optional arguments: Available arguments are as follows: <br>
 * 	mood <br>
 * 	transportation_type <br>
 
 *  
 * @author  Yi-Chun Teng
 * @param	map A map object that contains point_id and any of the items listed above 
 * @version   1.2, 01/7/2012
 * @param     
 * @return    return status 
 * @example   https://localhost:8080/NewUserSpecified?point_id=2&mood=1
 * 
 */
public class NewUserSpecified extends PLASHComponent {
	
	private long pointId;
	private Short mood;
	private Short transportationType;
	
	private Session tskSession; //task session



    public NewUserSpecified() {

    }//end constructor


	@Override
	public Object serviceMain(Map map) {
    	

		try {
	        
	        if (map.containsKey("point_id")) {
	        	pointId = Integer.parseInt(map.get("point_id").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "Point ID must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi
	            	
	        if (map.containsKey("mood")) {
	        	mood = Short.parseShort(map.get("mood").toString());
	        }//fi
	        if (map.containsKey("transportation_type")) {
	        	transportationType = Short.parseShort(map.get("transportation_type").toString());
	        }//fi	        
	        
	        UserSpecified us = new UserSpecified(pointId, mood, transportationType);
	        
	        tskSession = sessionFactory.openSession();
			Transaction tx = tskSession.beginTransaction();
			tskSession.save(us);	
			tx.commit();
	        tskSession.close();	            
	        return returnSuccess(map);
	        
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
		} catch (ConstraintViolationException e) {
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 004;
	        err.explaination = "Insert or update on table violates foreign key constraint. Perhaps the point_id is invalid";
			return returnUnsuccess(map,err);			
		} catch (Exception e){
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 005;
	        err.explaination = e.toString();
	        e.printStackTrace();
			return returnUnsuccess(map,err);			
		}//end try catch
	
	    	        		        
	     
	
        

	} //end serviceMain
    
	/**
	 * Initialize the variables. <br>
	 * This method is used to set default values for all fields 
	 * 
	 */
	private void initFields() {


	}//end method
}  //close class
