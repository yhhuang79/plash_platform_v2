package tw.edu.sinica.iis.ants.components;


import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.AbnormalResult;


import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.db_pojo.antrip.TripSharing;
import tw.edu.sinica.iis.ants.db_pojo.antrip.TripSharingId;


/**
 * Set authorized friend component. This component sets which friends are allowed to see a particular trip. <br>
 * 
 * This component takes a Map object that contains the following keys: <br>
 * 
 * userid : Required. An int value that indicates whose trip this entry belongs to <br>
 * trip_id: Required. An int value that indicates the trip id <br>
 * friend_id: Required. An int value that indicates friend's id <br>
 *          Warning: This component does not check whether the supplied friend (id) is indeed the user's friend or not. <br>
 *          		 It is recommended that this component is used in conjunction with a component that check friendship validity. <br>
 * 
 * @author	Yi-Chun Teng 
 * @param   a map that contains the following keys: userid,friend_id, trip_id
 * @return  map containing result status
 * @example	https://localhost:8080/SetAuthFriendComponent?userid=334&trip_id=4,5,6,74&friend_id=5,4,51,22      
 */
public class SetAuthFriendComponent extends PLASHComponent {



	private Session tskSession; //task session
	
	public SetAuthFriendComponent() {
		super();	
		enableDebugLog();
	}//end constructor

	public Object serviceMain(Map map) {

        
        int user_id;
        int[] trip_id, friend_id;	
        int num_of_trip_ids, num_of_friend_ids; 
        StringTokenizer ids_st;        
		String tmpUserid, tmpTrip_ids, tmpFriend_ids;
		boolean duplicateEntry = false;
		try {
		

			if ((tmpUserid = (String)map.remove("userid")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        map.put("code", 400);
		        map.put("message", "parameter error");		        
		        err.refCode = 001;
		        err.explaination = "User id must be specified";
				return returnUnsuccess(map,err);
			} else {
				user_id = Integer.parseInt(tmpUserid);
			}//fi
			
			if ((tmpTrip_ids = (String)map.remove("trip_id")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        map.put("code", 400);
		        map.put("message", "parameter error");		        		        
		        err.refCode = 001;
		        err.explaination = "friend's user id must be specified";
				return returnUnsuccess(map,err);
			} else {
				ids_st = new StringTokenizer(tmpTrip_ids,","); 
				num_of_trip_ids = ids_st.countTokens();
				trip_id = new int[num_of_trip_ids];
				int i = 0;
				while (ids_st.hasMoreTokens()) {
					trip_id[i] = Integer.parseInt(ids_st.nextToken());
					i++;
				}//end while
				
			}//fi
			
			if ((tmpFriend_ids = (String)map.remove("friend_id")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        map.put("code", 400);
		        map.put("message", "parameter error");		        		        
		        err.refCode = 001;
		        err.explaination = "trip id must be specified";
				return returnUnsuccess(map,err);
			} else {
				ids_st = new StringTokenizer(tmpFriend_ids,","); 
				num_of_friend_ids = ids_st.countTokens();
				friend_id = new int[num_of_friend_ids];
				int i = 0;
				while (ids_st.hasMoreTokens()) {
					friend_id[i] = Integer.parseInt(ids_st.nextToken());
					i++;
				}//end while
				
			}//fi		
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
	        AbnormalResult err = new AbnormalResult(this,'E');
	        map.put("code", 400);
	        map.put("message", "parameter error");		        	        
	        err.refCode = 002;
	        err.explaination = "NullPointerException, most likely due to invalid parameters";
			return returnUnsuccess(map,err);
			
		} catch (NumberFormatException e) { //invalid arguments 
	        AbnormalResult err = new AbnormalResult(this,'E');
	        map.put("code", 400);
	        map.put("message", "parameter error");		        	        
	        err.refCode = 002;
	        err.explaination = "NumberFormatException, most likely due to invalid parameters";
			return returnUnsuccess(map,err);
			
		}//end try catch
		
        tskSession = sessionFactory.openSession(); 
        Criteria criteriaTripSharing = tskSession.createCriteria(TripSharing.class);
        
        for (int i = 0; i < num_of_trip_ids; i++) {
        	for (int f=0; f < num_of_friend_ids; f++) {
        		
        		TripSharingId entry = new TripSharingId(user_id,friend_id[f],trip_id[i]);
        		
        		try {			
        			criteriaTripSharing.add(Restrictions.eq("id", entry));
  			
        			if (criteriaTripSharing.uniqueResult() == null) { //element does not exist
  
        				
        				TripSharing ts = new TripSharing(entry);
        				tskSession.save(ts);
        				
        			} else {
        				duplicateEntry = true;    
        			}//end if  


        		} catch (HibernateException he) {
        	        AbnormalResult err = new AbnormalResult(this,'E');
    		        map.put("code", 500);
    		        map.put("message", "internal server error");		        
        	        err.refCode = 003;
        	        err.explaination = "Warning, HibernateException occurred. Please check database integrity.";
        			return returnUnsuccess(map,err);
        			
        		}//end try catch        	
            	
      		
        	}//rof
 	
      	
        	
        	
        }//rof
		
        if (duplicateEntry) {
	        AbnormalResult war = new AbnormalResult(this,'W');
	        map.put("code", 400);
	        map.put("message", "parameter error");		        	        
	        war.refCode = 003;
	        war.explaination = "Warning, there were duplicate trip sharing combinations assigned.";
        	return returnUnsuccess(map,war);
        } else  {
			tskSession.beginTransaction().commit();
	        map.put("code", 200);
	        map.put("message", "ok");		        
        	return returnSuccess(map);
        }//fi
        
	}//end method
    
} //end class
