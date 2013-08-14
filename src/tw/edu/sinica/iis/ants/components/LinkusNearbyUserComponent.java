package tw.edu.sinica.iis.ants.components;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;



public class LinkusNearbyUserComponent extends PLASHComponent {

	
	/**
	 * Constructor
	 * 
	 */
	public LinkusNearbyUserComponent() {		
		}//end constructor
	

	
	/*

	
	*
	 
	 
	 */	
	@Override 
	public Object serviceMain(Map map) {
		String lat,lon, fb_id; 
		
		// read in command
		if (map.containsKey("lat")) {
			lat = map.get("lat").toString();
		} else { //invalid argument 
        	AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "lat is not supplied";
			return returnUnsuccess(map,err);    			
		}//end try catch
		if (map.containsKey("lon")) {
			lon = map.get("lon").toString();
		} else { //invalid argument 
        	AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "lon is not supplied";
			return returnUnsuccess(map,err);    			
		}//end try catch
		if (map.containsKey("fb_id")) {
			 fb_id = map.get("fb_id").toString();
		} else { //invalid argument 
        	AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "fb_id is not supplied";
			return returnUnsuccess(map,err);    			
		}//end try catch
		
		
		try {
			
	        tskSession = sessionFactory.openSession();
		
			String createSQLQuery = new String("SELECT nearby_trip('"+ lat +"','"+lon+"',' "+fb_id+"')");
			String resultString = (String)tskSession.createSQLQuery(createSQLQuery).uniqueResult();
			//String input =new String("Select lat from  user_location.user_point_location_time where userid=722");
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
		    map.put("fb_id",List2.toString());
		    map.put("education",List3.toString());
			System.out.println(map.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
		}




}//end class