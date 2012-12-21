package tw.edu.sinica.iis.ants.components;


import java.util.*;



import org.hibernate.*;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;


import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db.antrip.TripSharing;


/**
 *
 * This component move location point data into depreciated point table and delete the ones in the original table. <br>
 * 
 * The following parameters are required: <br>
 * userid : Required. This parameter indicates which user the input trip belongs to<br>
 * trip_id: Required. This parameter indicates which trip <br>
 * 				
 *  *  
 * @author  Yi-Chun Teng
 * @param	map A map object that contains userid, trip_id, update_status and any of the items listed above 
 *

 * @version   1.2, 09/03/2012
 * @param     
 * @return    return status 
 * @example   https://localhost:8080/DelTripComponent?userid=1&trip_id=2
 * 
 */
public class DelTripComponent extends PLASHComponent {
		
	/**
	 * Fields
	 */
	private int userid;
	private int trip_id;
	private Session tskSession; //task session



	public DelTripComponent() {

	}//end constructor

	
	public Object serviceMain(Map map) {
		

		try {
	        
	        if (map.containsKey("userid")) {
	        	userid = Integer.parseInt(map.remove("userid").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "User ID must be specified";
				return returnUnsuccess(map,err);        	
	        }//fi
	        if (map.containsKey("trip_id"))  {
	        	trip_id = Integer.parseInt(map.remove("trip_id").toString());
	        } else {
				getElapsed();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "Trip id must be specified";
				return returnUnsuccess(map,err);     
	        }//fi

		

			
			//select the rows using userid and tripid from user_point_location_time
	        tskSession = sessionFactory.openSession();

			
		    Criteria criteria = tskSession.createCriteria(T_TripData.class); 
		    criteria.add(Restrictions.eq("userid", userid));
		    criteria.add(Restrictions.eq("trip_id", trip_id));
		    //insert the data to depreciated_location_data table
		    for(T_TripData obj : (List<T_TripData>)criteria.list()) {
		    	T_TripData u = obj;
		    	T_DepreciatedLocationData t = new T_DepreciatedLocationData();
		    	t.setId(u.getId());
		    	t.setGps(u.getGps());
		    	t.setLabel(u.getLabel());
		    	t.setTimestamp(u.getTimestamp());
		    	t.setServer_timestamp(u.getServer_timestamp());
		    	t.setTrip_id(u.getTrip_id());
		    	t.setUserid(u.getUserid());
		    	tskSession.save(t);
		    	tskSession.delete(obj); // delete data from original table
		    }//rof
		    
		    tskSession.beginTransaction().commit();
		    
		    //delete the entry in trip_info table with the same userid and trip_id
		    criteria = tskSession.createCriteria(T_TripInfo.class);
		    criteria.add(Restrictions.eq("userid", userid));
		    criteria.add(Restrictions.eq("trip_id", trip_id));
		    
		    for(Object obj : criteria.list()) { //use for to avoid deleting with null entity
		    	tskSession.delete(obj);
		    }//rof		
			
			//now delete entries in trip_sharing table
		    criteria = tskSession.createCriteria(TripSharing.class);
		    if (criteria == null  ){
			    System.out.println("criterion is null la gan");
		    }//fi
		    criteria.add(Restrictions.eq("id.userId", userid));
		    criteria.add(Restrictions.eq("id.tripId", trip_id));
		    
		    for(Object obj : criteria.list()) { //use for to avoid deleting with null entity
			    if (obj == null  ){
				    System.out.println("obj is null la gan");
			    }//fi		    	
		    	tskSession.delete(obj);
		    }//rof
		    			
		    tskSession.beginTransaction().commit();
	        
	        map.put("code", 200);
	        map.put("message", "OK");
	        return 	returnSuccess(map);
	        
		} catch (ConstraintViolationException e) {
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 004;
	        err.explaination = "Insert or update on table violates foreign key constraint";
			return returnUnsuccess(map,err);			
		} catch (Exception e){
			getElapsed();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 005;
	        err.explaination = e.toString();
	        e.printStackTrace();
			return returnUnsuccess(map,err);			
		}//end try
	
	}//end service main
}//end class
