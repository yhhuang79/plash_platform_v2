package tw.edu.sinica.iis.ants.components;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import tw.edu.sinica.iis.ants.PlashUtils;
import tw.edu.sinica.iis.ants.DB.T_TripHash;
import tw.edu.sinica.iis.ants.DB.T_TripInfo;
import tw.edu.sinica.iis.ants.DB.T_UserPointLocationTime;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * 
 * This component lets user Input trip info manually.  <br>
 * 
 * This component takes a Map object that contains the following keys: <br>
 * userid : Required. This parameter indicates which user <br>
 * trip_id: Required. This parameter indicates which trip of the user <br>
 * update_status: Required. This parameter indicates which update status this record is currently in <br>
 * 					Warning: use this component carefully! Misuse will ruin the integrity of the database table <br> 
 * 				
 * Optional arguments: Available arguments are as follows: <br>
 * 	trip_name <br>
 * 	trip_st <br>
 * 	trip_et <br>
 * 	trip_length <br>
 * 	num_of_pts <br>
 * 	st_addr_prt1 <br>
 * 	st_addr_prt2 <br>
 * 	st_addr_prt3 <br>  
 * 	st_addr_prt4 <br>
 * 	st_addr_prt5 <br>
 * 	et_addr_prt1 <br>
 * 	et_addr_prt2 <br>
 * 	et_addr_prt3 <br>
 *  et_addr_prt4 <br>
 *  et_addr_prt5 <br>
 *  is_completed <br>
 * Example: https://localhost:8080/InputTripInfoComponent?userid=5&trip_id=3&update_status=4&st_addr_prt5=%22haha%22
 *  
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains userid, trip_id, update_status and any of the items listed above 
 */
public class InputTripInfoComponent extends PLASHComponent {


	private Session tskSession; //task session




	public Object serviceMain(Map map) {
    	
        
        tskSession = sessionFactory.openSession();
        
        String trip_name = "Untitled";
        Integer userid = null;
        Integer trip_id = null;      
        Timestamp trip_st = new Timestamp(1);
        Timestamp trip_et = new Timestamp(1);
                
        Integer trip_length = 0;
        Integer num_of_pts = 0;        
        String st_addr_prt1 = "";
        String st_addr_prt2 = ""; 
        String st_addr_prt3 = ""; 
        String st_addr_prt4 = ""; 
        String st_addr_prt5 = "";  
        String et_addr_prt1 = "";
        String et_addr_prt2 = ""; 
        String et_addr_prt3 = ""; 
        String et_addr_prt4 = ""; 
        String et_addr_prt5 = "";        
        Short update_status = -1;
        Boolean is_completed = true;
       
        if (map.containsKey("trip_name")) {
        	trip_name = map.get("trip_name").toString();
        }//fi             
        
        if (map.containsKey("userid")) {
        	userid = Integer.parseInt(map.get("userid").toString());
        }//fi
        if (map.containsKey("trip_id")) {  
        	trip_id = Integer.parseInt(map.get("trip_id").toString());
    	}//fi
        if (map.containsKey("trip_st")) {
        	trip_st = Timestamp.valueOf(map.get("trip_st").toString());
		}//fi
        if (map.containsKey("trip_et")) {
        	trip_et = Timestamp.valueOf(map.get("trip_et").toString());
    	}//fi
        if (map.containsKey("trip_length")) {
        	trip_length = Integer.parseInt(map.get("trip_length").toString());
        }//fi
        if (map.containsKey("num_of_pts")) {
        	num_of_pts = Integer.parseInt(map.get("num_of_pts").toString());
        }//fi
       
        if (map.containsKey("st_addr_prt1")){
        	st_addr_prt1 = map.get("st_addr_prt1").toString();
        }//fi        	
        if (map.containsKey("st_addr_prt2")){
        	st_addr_prt2 = map.get("st_addr_prt2").toString();
        }//fi  
        if (map.containsKey("st_addr_prt3")){
        	st_addr_prt3 = map.get("st_addr_prt3").toString();
        }//fi  
        if (map.containsKey("st_addr_prt4")){
        	st_addr_prt4 = map.get("st_addr_prt4").toString();
        }//fi          
        if (map.containsKey("st_addr_prt5")){
        	st_addr_prt5 = map.get("st_addr_prt5").toString();
        }//fi     

        if (map.containsKey("et_addr_prt1")){
        	et_addr_prt1 = map.get("et_addr_prt1").toString();
        }//fi        	
        if (map.containsKey("et_addr_prt2")){
        	et_addr_prt2 = map.get("et_addr_prt2").toString();
        }//fi  
        if (map.containsKey("et_addr_prt3")){
        	et_addr_prt3 = map.get("et_addr_prt3").toString();
        }//fi  
        if (map.containsKey("et_addr_prt4")){
        	et_addr_prt4 = map.get("et_addr_prt4").toString();
        }//fi          
        if (map.containsKey("et_addr_prt5")){
        	et_addr_prt5 = map.get("et_addr_prt5").toString();
        }//fi    
        
        if (map.containsKey("update_status")) {
        	update_status = Short.parseShort(map.get("update_status").toString());
        }//fi      

        if (map.containsKey("is_completed")) {
        	is_completed = Boolean.parseBoolean(map.get("is_completed").toString());
        }//fi      

        if (userid == null || trip_id == null || update_status == -1) { 
        	tskSession.close();
        	return map;
        	
			
		} else {   
				
				T_TripInfo tmpTripInfo = new T_TripInfo();
				tmpTripInfo.setUserid(userid);			
				tmpTripInfo.setTrip_id(trip_id);
				tmpTripInfo.setTrip_name(trip_name);
				tmpTripInfo.setTrip_st(trip_st);
				tmpTripInfo.setTrip_et(trip_et);
				tmpTripInfo.setTrip_length(trip_length);
				tmpTripInfo.setNum_of_pts(num_of_pts);
				tmpTripInfo.setSt_addr_prt1(st_addr_prt1);
				tmpTripInfo.setSt_addr_prt2(st_addr_prt2);
				tmpTripInfo.setSt_addr_prt3(st_addr_prt3);
				tmpTripInfo.setSt_addr_prt4(st_addr_prt4);
				tmpTripInfo.setSt_addr_prt5(st_addr_prt5);
				tmpTripInfo.setEt_addr_prt1(et_addr_prt1);				
				tmpTripInfo.setEt_addr_prt2(et_addr_prt2);
				tmpTripInfo.setEt_addr_prt3(et_addr_prt3);
				tmpTripInfo.setEt_addr_prt4(et_addr_prt4);
				tmpTripInfo.setEt_addr_prt5(et_addr_prt5);				
				tmpTripInfo.setUpdate_status(update_status);

		        
		        
				Transaction tx = tskSession.beginTransaction();
				tskSession.persist(tmpTripInfo);
				tx.commit();
				
				T_TripHash tth = new T_TripHash();
				tth.setId(tmpTripInfo.getId());
				tth.setTrip_id(tmpTripInfo.getTrip_id());
				tth.setUserid(tmpTripInfo.getUserid());
				try {
					String tst = "";
					if (tmpTripInfo.getTrip_st() != null)
						tst = tmpTripInfo.getTrip_st().toString();
					tth.setHash(PlashUtils.MD5(tmpTripInfo.getId().toString() + "#" + tst));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				tx = tskSession.beginTransaction();
				tskSession.save(tth);
				tx.commit();
				
			}//fi
        
        tskSession.close();

        return map;
	} //close Object greet
    
}  //close class inputGpsLocationComponent
