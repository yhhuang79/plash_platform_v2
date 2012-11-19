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
	
import tw.edu.sinica.iis.ants.PlashUtils;
import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
	
	/**
	 * This component returns the trip data.  <br>
	 * 
	 * This component takes a Map object that contains the following keys: <br>
	 * userid : Required. This parameter indicates which user's trip to return <br>
	 * trip_id: Optional. This parameter indicates which trip to return. This is optional <br>
	 * 			If trip_id is absent, this component simply returns the newest trip belonging to the user with id userid. <br>
	 * field_mask: Optional. This mask indicates which columns in the trip data record are included
	 * sort: Not enabled yet. This parameter indicate which column will be served as the sorting key
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
	 * 	<br>
	 * Example:  GetTripDataComponent?userid=1&latest_pt_only=true&field_mask=0100000000000000 <br>
	 * 
	 *   
	 * @author	Yi-Chun Teng, Yu-Hsiang 
	 * @param	map A map object that contains trip data
	 */
	public class GetCheckinDataComponent extends PLASHComponent {
	
	
	
		private Session tskSession; //task session
		private long timeID;
		private int requestCount;
	
		
		public GetCheckinDataComponent() {
			super();
			requestCount = 0;
			enableDebugLog();
			
		}
		
		
		public Object serviceMain(Map map) {
			trackTimeBegin("service_main");
			requestCount++;

			try {
				tskSession = sessionFactory.getCurrentSession();
			} catch ( HibernateException e) {
				tskSession = sessionFactory.openSession();
			}//end try
	
			
			
		    
			try {
	
	
				int userid, trip_id, field_mask;
				boolean latest_pt_only;
				String tmpUserid, tmpTrip_id, tmpField_mask, tmpReturn_latest, tmphash;
				
				//PlashUtils.tripinfoToHash(tskSession);
				
				if ((tmphash = (String)map.remove("hash")) == null) {
				} else {
					Map params = PlashUtils.HashToParam(tmphash, tskSession);
					if(params != null){
						if(params.containsKey("userid")){
							userid = Integer.parseInt(params.get("userid").toString());
						} else {
							map.put("GetCheckInInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
					        //tskSession.close();
							return map;
						}
						if(params.containsKey("trip_id")){
							trip_id = Integer.parseInt(params.get("trip_id").toString());
						} else {
							map.put("GetCheckInInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
					        //tskSession.close();
							return map;
						}
						if(params.containsKey("field_mask")){
							field_mask = Integer.parseInt(params.get("field_mask").toString(),2);
						} else {
							field_mask = Integer.parseInt("1100011110000001",2);						
						}
						
						Map isPublic = PlashUtils.isTripShared(userid, trip_id, tskSession);
					    if(isPublic.get("isPublic").toString() == "true"){
					    	map.put("CheckInDataList", getTripData(userid, trip_id,field_mask));
							map.put("userid", userid);
							map.put("trip_id", trip_id);
							map.put("tripName", PlashUtils.getTripName(userid, trip_id, tskSession));
					    }else{
					    	map.put("CheckInDataList","null");
					    }
						trackTimeEnd("service_main");
						return map;
					} else {
						map.put("GetCheckInInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
				        System.out.println("GetCheckInInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
				        tskSession.close();
						return map;						
					}
				}//fi

				
				if ((tmpUserid = (String)map.remove("userid")) == null) {
					//user id must be specified
					map.put("GetCheckInInfoComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
			        System.out.println("GetCheckInInfoComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			        tskSession.close();
					return map;
				} else {
					userid = Integer.parseInt(tmpUserid);
					map.put("userid", userid);
				}//fi
				
				if ((tmpField_mask = (String)map.remove("field_mask")) == null) {
					field_mask = Integer.parseInt("1111111111111111",2);				
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
					//System.out.println("GetTripDataComponent End:\t"+ Calendar.getInstance().getTimeInMillis());					
					tskSession.close();	
					return map;				
					
				} else {
					//return all trip
					map.put("CheckInDataList", getTripData(userid, trip_id,field_mask));
					map.put("tripName", PlashUtils.getTripName(userid, trip_id, tskSession));
					System.out.println("GetCheckInDataComponent end:\t"	+ timeID + " Obj ID: " + this);
					tskSession.close();
					return map;				
	
				}//fi
				
	   			
			} catch (NullPointerException e) { //Most likely due to invalid arguments 
				map.put("GetTripDataComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file		
		        System.out.println("GetTripDataComponent failure end1:\t" + e.toString() + " : requestID: " + requestCount);
		        tskSession.close(); 	
				return map;
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
	    	criteriaTripData.add(Restrictions.eq("userid", userid));
	    	criteriaTripData.add(Restrictions.eq("trip_id", trip_id));	    	
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
			   		if ((field_mask & 8192) != 0) { 
			    		Geometry tmpGPS = (Geometry)resultList.get(0).remove("gps");		    		
			    		resultList.get(0).put("lng", tmpGPS.getCoordinate().x*1000000);
			    		resultList.get(0).put("lat", tmpGPS.getCoordinate().y*1000000);		    	
			    	}//fi 				
					return resultList.get(0);
				}//fi			//*/
	
							
			} catch (HibernateException he) {
				
				System.out.println("Warning: hibernation exception");	
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
	    	criteriaTripData.add(Restrictions.eq("userid", userid));
	    	criteriaTripData.add(Restrictions.eq("trip_id", trip_id));
	    	ProjectionList filterProjList = Projections.projectionList();     	
	    	criteriaTripData.setProjection(addFilterList(filterProjList,field_mask));
	    	criteriaTripData.addOrder(Order.asc("timestamp"));
	    	criteriaTripData.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    	
			try {
				List<Map> tripDataList = (List<Map>) criteriaTripData.list();
		   		if ((field_mask & 16384) != 0) { 
		    		Geometry tmpGPS, prevGPS = null;
		    		String tmpCheckIn = null;
		    		Integer tmpPointId, pCounter = 0;
		    		Map checkindata;
		    		double sd = 0, avg = 0;
		    		for (Map tmpMap:tripDataList) {
		    			tmpGPS = (Geometry)tmpMap.get("gps");
		    			if(tmpMap.containsKey("accu")) {
		    				if((tmpGPS.getCoordinate().x != -999) &&
		    						(tmpGPS.getCoordinate().y != -999) &&
		    						(Double.parseDouble(tmpMap.get("accu").toString()) < 500) &&
		    						(Double.parseDouble(tmpMap.get("accu").toString()) != -999)) {
		    					avg += Double.parseDouble(tmpMap.get("accu").toString());
		    					pCounter++;
		    				}
		    			}
		    		}
		    		avg = avg / pCounter;
		    		for (Map tmpMap:tripDataList) {
		    			tmpGPS = (Geometry)tmpMap.get("gps");
		    			if(tmpMap.containsKey("accu")) {
		    				if((tmpGPS.getCoordinate().x != -999) &&
		    						(tmpGPS.getCoordinate().y != -999) && 
		    						(Double.parseDouble(tmpMap.get("accu").toString()) < 500) &&
		    						(Double.parseDouble(tmpMap.get("accu").toString()) != -999))
		    					sd += Math.pow((Double.parseDouble(tmpMap.get("accu").toString()) - avg),2);
		    			}
		    		}
		    		sd = Math.sqrt(sd / pCounter);
		    		for (Map tmpMap:tripDataList) {
		    			tmpGPS = (Geometry)tmpMap.remove("gps");
		    			if(tmpMap.containsKey("accu")) {
		    				if(((tmpGPS.getCoordinate().x != -999) &&
		    						(tmpGPS.getCoordinate().y != -999) && 
		    						(Double.parseDouble(tmpMap.get("accu").toString()) != -999) &&
		    						(Double.parseDouble(tmpMap.get("accu").toString()) <= avg + (sd * 2))) ||
		    						(tmpMap.get("checkin").toString() == "true")){
				    			tmpMap.put("lng", tmpGPS.getCoordinate().x*1000000);
				    			tmpMap.put("lat", tmpGPS.getCoordinate().y*1000000);
				    			prevGPS = tmpGPS;
		    				} else {
		    					if(prevGPS == null) {
		    						tmpMap.put("lng", -999*1000000);
				    				tmpMap.put("lat", -999*1000000);
		    					} else {
					    			tmpMap.put("lng", prevGPS.getCoordinate().x*1000000);
					    			tmpMap.put("lat", prevGPS.getCoordinate().y*1000000);		    						
		    					}
		    				} //fi
		    			} else {
			    			tmpMap.put("lng", tmpGPS.getCoordinate().x*1000000);
			    			tmpMap.put("lat", tmpGPS.getCoordinate().y*1000000);		    				
		    			} //fi
		    			System.out.println("GetCheckInDataComponent GPS:\t"	+ tmpGPS.getCoordinate().x*1000000 + " : " + tmpGPS.getCoordinate().y*1000000 + " : " + tmpMap.get("accu").toString());
		    			if (tmpMap.get("checkin") != null) tmpCheckIn = tmpMap.get("checkin").toString();		    			
		    			tmpPointId = (Integer)tmpMap.remove("id");
		    			System.out.println("GetCheckInDataComponent PointId:\t"	+ tmpPointId + " : " + tmpCheckIn);
				   		if (tmpCheckIn == "true"){
				   			try {
					   			Criteria criteriaCheckInData = tskSession.createCriteria(T_CheckInInfo.class);
					   			criteriaCheckInData.add(Restrictions.eq("id", tmpPointId));
					   			Iterator clist = criteriaCheckInData.list().iterator();	
					   			T_CheckInInfo c = (T_CheckInInfo) clist.next();
					   			checkindata = new HashMap();
					   			checkindata.put("message", c.getMessage());
					   			checkindata.put("emotion", c.getEmotion());
					   			checkindata.put("picture_uri", c.getPicture_uri());
					   			tmpMap.put("CheckIn", checkindata);
				   			} catch (NoSuchElementException ne) {
				   				
				   			}
					   	}//fi
				   		tmpMap.remove("checkin");
				   		//System.out.println("checkin:"+ tmpMap.toString());
		    		}//rof
		    	
		    	}//fi */
				return tripDataList;
												
			} catch (HibernateException he) {
				System.out.println("Warning: hibernation exception"); 	
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
	    		filterProjList.add(Projections.property("gps"),"gps");
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
	    	filterProjList.add(Projections.property("id"),"id"); 
			return filterProjList;
			
		}//end method
		
	
	}//end class
