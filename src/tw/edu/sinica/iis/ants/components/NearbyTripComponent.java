package tw.edu.sinica.iis.ants.components;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;



public class NearbyTripComponent extends PLASHComponent {

	
	/**
	 * Constructor
	 * 
	 */
	public NearbyTripComponent() {		
		}//end constructor
	

	
	/*

	
	*
	 
	 
	 */	
	@Override 
	public Object serviceMain(Map map) {
		String latitude, longitude, queryRadius; 
		
		// read in command
		if (map.containsKey("latitude")) {
			latitude = map.get("latitude").toString();
		} else { //invalid argument 
        	AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "latitude is not supplied";
			return returnUnsuccess(map,err);    			
		}//end try catch
		if (map.containsKey("longitude")) {
			longitude = map.get("longitude").toString();
		} else { //invalid argument 
        	AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "longitude is not supplied";
			return returnUnsuccess(map,err);    			
		}//end try catch
		if (map.containsKey("queryRadius")) {
			 queryRadius = map.get("queryRadius").toString();
		} else { //invalid argument 
        	AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "queryRadius is not supplied";
			return returnUnsuccess(map,err);    			
		}//end try catch
		
		
		try {
			
	        tskSession = sessionFactory.openSession();
	        String GeogUpdate = new String("SELECT to_geog_update('"+"gogorock"+"')");
			String resultString1 = (String)tskSession.createSQLQuery(GeogUpdate).uniqueResult();	
			String createSQLQuery = new String("SELECT nearby_trip('"+ latitude +"','"+longitude+"',' "+queryRadius+"')");
			String resultString = (String)tskSession.createSQLQuery(createSQLQuery).uniqueResult();
			//String input =new String("Select latitude from  user_location.user_point_location_time where userid=722");
			//ArrayList<Double> test= (ArrayList<Double>)tskSession.createSQLQuery(input).list();
			//System.out.println("ejwfojewf " + Arrays.toString(test.toArray()));
			int StringSize= resultString.length();
			ArrayList<String> List1 = new ArrayList<String>();
			ArrayList<String> List2 = new ArrayList<String>();
			ArrayList<String> List3 = new ArrayList<String>();
			//System.out.println("line 78 " + resultString);
			System.out.println(resultString.substring(1, StringSize-1));
			StringTokenizer st = new StringTokenizer(resultString.substring(1, StringSize-1),",");
			while(st.hasMoreTokens())
			{
			int i=0;
			//System.out.println(map.size());
			StringTokenizer st1 = new StringTokenizer(st.nextToken(),"~");
			while(st1.hasMoreTokens()){
				
				switch(i){ 
				case 0:
				
				List1.add(st1.nextToken());
				//System.out.println(map.toString());
				case 1:
				
				List2.add(st1.nextToken());
				//System.out.println(map.toString());
				case 2:
					
				List3.add(st1.nextToken());
				//System.out.println(map.toString());
				//System.out.println(map.size());
				}
		        i++;
				}
			}
		    map.put("distance",List1);
		    map.put("userid",List2);
		    map.put("tripid",List3);
			System.out.println("here " + map.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			tskSession.close();
		}
		return map;
		}




}//end class