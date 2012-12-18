	package tw.edu.sinica.iis.ants.components;
	
	import java.sql.Timestamp;
	
	import java.util.*;

	
	
	import javax.net.ssl.HttpsURLConnection;
	

	import org.hibernate.*;
	import org.hibernate.criterion.*;
	import org.hibernate.transform.*;
	import org.hibernate.type.*;
	import org.json.*;
	
	import com.vividsolutions.jts.geom.Geometry;
	import com.vividsolutions.jts.io.*;
	
import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
	
	/**
	 * 
	 * This component returns the trip data.  <br>
	 * 
	 * This component takes a Map object that contains the following keys: <br>
	 * userid : Required. This parameter indicates which user's trip to return <br>
	 * trip_id: Optional. This parameter indicates which trip to return. This is optional <br>
	 * 			If trip_id is absent, this component simply returns the newest trip belonging to the user with id userid. <br>
	 * field_mask: Optional. This mask indicates which columns of the trip data record are included. <br>
	 * sort: Currently disabled for performance issue. This parameter indicates which column will be served as the sorting key
	 * latest_pt_only: Optional. This is a boolean value indicating whether the returned result should be a list of all points or a single point indicating the latest point. <br>
	 * 			If return_latest is not specified, the default value is false and the component will return a list of all trip points. <br>
	 * fields and corresponding bit positions (from left to right):  <br>
	 * 	1. timestamp <br>
	 * 	2. gps <br>
	 * 	3. server_timestamp <br>
	 * 	4. trip_id <br>
	 * 	5. label <br>
	 * 	6. alt <br>
	 * 	7. accu <br>
	 * 	8. spd <br>
	 * 	9. bear <br>
	 * 	10. accex <br>
	 * 	11. accey <br>
	 * 	12. accez <br>
	 * 	13. gsminfo <br>
	 * 	14. wifiinfo <br>
	 *  15. app <br>
	 *  16. checkin <br>
	 * 	<br>
	 * Example:  https://localhost:8080/GetTripDataComponent?userid=1&latest_pt_only=true&field_mask=01000000000000010 <br>
	 * 
	 *   
	 * @author	Yi-Chun Teng 
	 * @param	map A map object that contains userid and optionally trip_id, field_mask, latest_pt_only.
	 * @return	map A map object that contains trip data, with field names as keys and field data as values.
	 */
	public class GetTripDataComponent extends PLASHComponent {
	
	
	
		private Session tskSession; //task session
		private long timeID;
		private int requestCount;
	
		
		public GetTripDataComponent() {
			super();
			requestCount = 0;
			enableDebugLog();
			
		}
		
		
		public Object serviceMain(Map map) {
			
			requestCount++;

			try {
				tskSession = sessionFactory.getCurrentSession();
			} catch ( HibernateException e) {
				tskSession = sessionFactory.openSession();
			}//end try
	
			
			
		    
			try {
	
	
				int userid, trip_id, field_mask;
				boolean latest_pt_only;
				String tmpUserid, tmpTrip_id, tmpField_mask, tmpReturn_latest;
				if ((tmpUserid = (String)map.remove("userid")) == null) {
					//user id must be specified
					map.put("GetTripInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
			        System.out.println("GetTripInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			        tskSession.close();
					return map;
				} else {
					userid = Integer.parseInt(tmpUserid);
					map.put("userid", userid);
				}//fi
				
				if ((tmpField_mask = (String)map.remove("field_mask")) == null) {
					field_mask = Integer.parseInt("111111111111111",2);				
				} else {
					field_mask = Integer.parseInt(tmpField_mask,2);
				}//fi
				
				if ((tmpTrip_id = (String)map.remove("trip_id")) == null) {				
					Criteria latestTripCriteria = tskSession.createCriteria(T_TripData.class);
					latestTripCriteria.add(Restrictions.eq("userid", userid));				    
			    	latestTripCriteria.setProjection(Projections.projectionList().add(Projections.max("trip_id")));						    					
					Integer tmpIntTrip_id = (Integer)latestTripCriteria.uniqueResult();								
						
					if (tmpIntTrip_id == null) {
						tskSession.close();	
						return map;
					} else {
						trip_id = tmpIntTrip_id;
					}//fi
				} else {
	
					trip_id = Integer.parseInt(tmpTrip_id);
	
				}//fi
				
				if ((tmpReturn_latest = (String)map.remove("latest_pt_only")) == null) {
					latest_pt_only = false;				
				} else {
					latest_pt_only = Boolean.parseBoolean(tmpReturn_latest);
					//field_mask = Integer.parseInt(tmpField_mask,2);
				}//fi
				

				
				if(latest_pt_only){
					//return latest trip point
					map.putAll(getLatestTripPt(userid,trip_id,field_mask));					
					tskSession.close();	
					return returnSuccess(map);				
					
				} else {
					//return all trip
					map.put("tripDataList", getTripData(userid, trip_id,field_mask));
					tskSession.close();
					return returnSuccess(map);				
	
				}//fi
				
	   			
			} catch (NullPointerException e) { //Most likely due to invalid arguments 
				map.put("GetTripDataComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
		        System.out.println("GetTripDataComponent failure end1:\t" + e.toString() + " : requestID: " + requestCount);
		        tskSession.close(); 	
				return map; //*/
				
			} catch (NumberFormatException e) { //invalid arguments 
				map.put("GetTripDataComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
				//map.put("error detail" , e.toString()); //perhaps put error detail?
		        System.out.println("GetTripDataComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
		        tskSession.close(); 	
				return map;
			} catch (HibernateException he) { //bad db validity
		        System.out.println("GetTripDataComponent failure end3:\t"+ Calendar.getInstance().getTimeInMillis());
		        tskSession.close(); 	
				return map;
			}//end try catch
			
	
		}//end method
		
		/**
		 * This method return a trip's latest point
		 * @param userid Required. This parameter indicates user id.
		 * @param trip_id Required. This parameter indicates trip id.
		 * @param field_mask Required. This parameter indicates which field to be included in the result returned.
		 * @return a map object containing specified fields and corresponding values
		 * 			If such info is not found, the map will not contain corresponding key-value pairs	   
		 */
		private Map getLatestTripPt(int userid, int trip_id, int field_mask) {
			System.out.println("Gone here getLatestTripPt Obj ID: " + this + " request ID: " + requestCount);		
			//obtain the record
	    	Criteria criteriaTripData = tskSession.createCriteria(T_TripData.class);
	    	criteriaTripData.add(Restrictions.eq("this.userid", userid));
	    	criteriaTripData.add(Restrictions.eq("this.trip_id", trip_id));	    	
	    	ProjectionList filterProjList = Projections.projectionList();     	
	    	criteriaTripData.setProjection(addFilterList(filterProjList,field_mask));
	    	criteriaTripData.addOrder(Order.desc("timestamp"));
	    	criteriaTripData.setFetchSize(1);
	    	criteriaTripData.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			
			try {
				List<Map> resultList = (List<Map>) criteriaTripData.list();	
				if (resultList.size()  == 0 ) {
					return new HashMap();
				} else {				
			   		if ((field_mask & 16384) != 0) { 			   			
			    		/*
			   			Geometry tmpGPS = (Geometry)resultList.get(0).remove("gps");		    		
			    		resultList.get(0).put("lng", tmpGPS.getCoordinate().x*1000000);
			    		resultList.get(0).put("lat", tmpGPS.getCoordinate().y*1000000);	//*/
			    	}//fi 				
					return resultList.get(0);
				}//fi			//*/
	
							
			} catch (HibernateException he) {
				System.out.println(he.toString()); 	
				return null;  
			}//end try catch			//*/
			
	    
				
			
		}//end method
		
		/**
		 * This method returns all point data of a trip 
		 * @param userid Required. This parameter indicates user id.
		 * @param trip_id Required. This parameter indicates trip id.
		 * @param field_mask Required. This parameter indicates which field to be included in the result returned.
		 * @return a List object containing a list of map objects where each map object contains data of a point
		 */
		private List<Map> getTripData(int userid, int trip_id, int field_mask) {
			System.out.println("Gone here getTripData Obj ID: " + this + " request ID: " + requestCount);
			//obtain the record
	    	Criteria criteriaTripData = tskSession.createCriteria(T_TripData.class);
	    	criteriaTripData.add(Restrictions.eq("this.userid", userid));
	    	criteriaTripData.add(Restrictions.eq("this.trip_id", trip_id));
	    	ProjectionList filterProjList = Projections.projectionList();     	
	    	criteriaTripData.setProjection(addFilterList(filterProjList,field_mask));
	    	//criteriaTripData.addOrder(Order.desc("timestamp"));
	    	criteriaTripData.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    	
			try {
				List<Map> tripDataList = (List<Map>) criteriaTripData.list();
		   		if ((field_mask & 16384) != 0) { 
		    		Geometry tmpGPS;
		    		/*
		    		for (Map tmpMap:tripDataList) {
		    			tmpGPS = (Geometry)tmpMap.remove("gps");
		    			tmpMap.put("lng", tmpGPS.getCoordinate().x*1000000);
		    			tmpMap.put("lat", tmpGPS.getCoordinate().y*1000000);
		    		}//rof */
		    	
		    	}//fi */
				return tripDataList;
												
			} catch (HibernateException he) {
				System.out.println(he.toString()); 	
				return null;
			}//end try catch			//*/
		}//end method	
		
		/**
		 * Add corresponding projection property according to field_mask
		 * 
		 * @param filterProjList The projection list to contain various properties
		 * @param field_mask Indicates which field to be included in the returned map
		 * @return
		 */
		private ProjectionList addFilterList(ProjectionList filterProjList, int field_mask) {
	
	    	if ((field_mask & 32768) != 0) { 
	        	filterProjList.add(Projections.sqlProjection("timestamp", new String[] {"timestamp"}, new Type[] { new StringType() }));
	    	}//fi
	    	if ((field_mask & 16384) != 0) { 
	    		//filterProjList.add(Projections.property("gps"),"gps");
	    		filterProjList.add(Projections.property("latitude"),"latitude");
	    		filterProjList.add(Projections.property("longitude"),"longitude");
	    	}//fi
	    	if ((field_mask & 8192) != 0) { 
	        	filterProjList.add(Projections.property("server_timestamp"),"server_timestamp");  
	    	}//fi
	    	if ((field_mask & 4096) != 0) { //4096 = 1000000000000
	        	filterProjList.add(Projections.property("trip_id"),"trip_id");
	    	}//fi
	    	if ((field_mask & 2048) != 0) { 
	    		filterProjList.add(Projections.property("label"),"label");
	    	}//fi
	    	if ((field_mask & 1024) != 0) { //1024 = 10000000000
	    		filterProjList.add(Projections.property("alt"),"alt");
	    	}//fi
	    	if ((field_mask & 512) != 0) { 
	    		filterProjList.add(Projections.property("accu"),"accu");
	    	}//fi
	    	if ((field_mask & 256) != 0) { 
	    		filterProjList.add(Projections.property("spd"),"spd");
	    	}//fi
	    	if ((field_mask & 128) != 0) { 
	    		filterProjList.add(Projections.property("bear"),"bear");
	    	}//fi
	    	if ((field_mask & 64) != 0) { 
	    		filterProjList.add(Projections.property("accex"),"accex");
	    	}//fi
	    	if ((field_mask & 32) != 0) { //32 = 100000
	    		filterProjList.add(Projections.property("accey"),"accey");
	    	}//fi
	    	if ((field_mask & 16) != 0) { 
	    		filterProjList.add(Projections.property("accez"),"accez");
	    	}//fi
	    	if ((field_mask & 8) != 0) { 
	        	filterProjList.add(Projections.sqlProjection("gsminfo", new String[] {"gsminfo"}, new Type[] { new StringType() }));    		
	    	}//fi
	    	if ((field_mask & 4) != 0) { 
	        	filterProjList.add(Projections.sqlProjection("wifiinfo", new String[] {"wifiinfo"}, new Type[] { new StringType() }));    		
	    	}//fi
	    	
	    	if ((field_mask & 2) != 0) { //2 = 10
	    		filterProjList.add(Projections.property("app"),"app");    		
	    	}//fi    	
	    	
	    	if ((field_mask & 1) != 0) { //1=1
	    		filterProjList.add(Projections.property("checkin"),"checkin");    		
	    	}//fi    		    	
			return filterProjList;
			
		}//end method
		
	
	}//end class
