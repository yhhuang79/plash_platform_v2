package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;

import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;


import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.*;
import org.hibernate.type.*;
import org.json.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.*;

import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

/**
 * This is test component.  <br>
 * 
 *   
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains trip data
 */
public class TestComponent extends PLASHComponent {



	private Session tskSession; //task session

	
	public TestComponent() {
		super();	
		enableDebugLog();
	}//end constructor

	public Object serviceMain(Map map) {



        
        int userid;
        int[] trip_id, friend_id;	
        int num_of_trip_ids, num_of_friend_ids; 
        StringTokenizer ids_st;        
		String tmpUserid, tmpTrip_ids, tmpFriend_ids;
		
		try {
			

			if ((tmpUserid = (String)map.remove("userid")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "User id must be specified";
				return returnUnsuccess(map,err);
			} else {
				userid = Integer.parseInt(tmpUserid);
			}//fi
			
			if ((tmpTrip_ids = (String)map.remove("trip_id")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
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
					System.out.println(trip_id[i]);	
					i++;
				}//end while
				
			}//fi
			
			if ((tmpFriend_ids = (String)map.remove("friend_id")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "trip id must be specified";
				return returnUnsuccess(map,err);
			} else {
				ids_st = new StringTokenizer(tmpFriend_ids,","); 
				num_of_friend_ids = ids_st.countTokens();
				friend_id = new int[num_of_friend_ids];
				int i = 0;
				while (ids_st.hasMoreTokens()) {
					trip_id[i] = Integer.parseInt(ids_st.nextToken());
					System.out.println(friend_id[i]);	
					i++;
				}//end while
				
			}//fi		
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 002;
	        err.explaination = "NullPointerException, most likely due to invalid parameters";
			return returnUnsuccess(map,err);
			
		} catch (NumberFormatException e) { //invalid arguments 
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 002;
	        err.explaination = "NumberFormatException, most likely due to invalid parameters";
			return returnUnsuccess(map,err);
			
		}//end try catch
		
        tskSession = sessionFactory.openSession(); 
        Criteria criteriaFriendAuth = tskSession.createCriteria(T_FriendAuth.class);
        
        for (int i = 0; i < num_of_trip_ids; i++) {
        	for (int f=0; f < num_of_friend_ids; f++) {
            	
        		try {			
        			criteriaFriendAuth.add(Restrictions.eq("userAID", userid));
        			criteriaFriendAuth.add(Restrictions.eq("userBID", friend_id[i]));	
        			criteriaFriendAuth.add(Restrictions.eq("tripID", trip_id[i]));			

        			
        			if (criteriaFriendAuth.uniqueResult() == null) { //element does not exist
  
        				T_FriendAuth entry = new T_FriendAuth();
        				
        				entry.setUserAID(userid);
        				entry.setUserBID(friend_id[i]);
        				entry.setTripID(trip_id[i]);						
        				tskSession.save(entry);
        				tskSession.beginTransaction().commit();

        				//put code for sending e-mail here
        				//Suggestion: the process of sending e-mail should be initiated by router rather than this component
        				

        		        return map;			
        			}//end if        


        		} catch (HibernateException he) {
        	        AbnormalResult err = new AbnormalResult(this,'E');
        	        err.refCode = 002;
        	        err.explaination = "HibernateException, please check the validity of input and database integrity";
        			return returnUnsuccess(map,err);
        		}//end try catch        	
            	
      		
        	}//rof
 	
      	
        	
        	
        }//rof
		
        

		

		return map;
   			
	

	}//end method
	

	



}//end class
