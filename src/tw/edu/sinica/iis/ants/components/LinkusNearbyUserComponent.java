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
		String fb_id; 
		
		
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
		
			String createSQLQuery = new String("SELECT nearby_user(' "+fb_id+"')");
			String resultString = (String)tskSession.createSQLQuery(createSQLQuery).uniqueResult();
	
			int StringSize= resultString.length();
			ArrayList<String> List1 = new ArrayList<String>();
			ArrayList<String> List2 = new ArrayList<String>();
			ArrayList<String> List3 = new ArrayList<String>();
		
			System.out.println(resultString.substring(1, StringSize-1));
			StringTokenizer st = new StringTokenizer(resultString.substring(1, StringSize-1),",");
			while(st.hasMoreTokens())
			{
			int i=0;
	
			StringTokenizer st1 = new StringTokenizer(st.nextToken(),"~");
			while(st1.hasMoreTokens()){
				
				switch(i){ 
				case 0:
				List1.add(st1.nextToken());
                case 1:
			    List2.add(st1.nextToken());
		        case 2:
				List3.add(st1.nextToken());
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