package tw.edu.sinica.iis.ants.components;
import java.util.*;

import Utilities.HibernateUtil;
import Utilities.PLASHComponent;
public class NearByTripComponent extends PLASHComponent {

	
	/**
	 * Constructor
	 * 
	 */
	public NearBySearch() {		
		super();
	}//end constructor
	

	
	/*

	
	*
	 
	 
	 */
	public Object serviceMain(Map map) {
		String latitude="25.022073" , longitude="121.543501", QueryRadius="30"; 
		
		/*// read in command
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
		if (map.containsKey(" QueryRadius")) {
			 QueryRadius = map.get(" QueryRadius").toString();
		} else { //invalid argument 
        	AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "QueryRadius is not supplied";
			return returnUnsuccess(map,err);    			
		}//end try catch
		
		*/
		try {
			tskSession = HibernateUtil.configureSessionFactory().openSession();
			String createSQLQuery = new String("SELECT NearBySearch('"+ latitude +"','"+longitude+"',' "+QueryRadius+"')");
			String resultString = (String)tskSession.createSQLQuery(createSQLQuery).uniqueResult();
			//String input =new String("Select latitude from  user_location.user_point_location_time where userid=722");
			//ArrayList<Double> test= (ArrayList<Double>)tskSession.createSQLQuery(input).list();
			//System.out.println("ejwfojewf " + Arrays.toString(test.toArray()));
			int StringSize= resultString.length();
			ArrayList<String> List1 = new ArrayList<String>();
			ArrayList<String> List2 = new ArrayList<String>();
			ArrayList<String> List3 = new ArrayList<String>();
			System.out.println("line 78 " + resultString);
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
		}
		return map;
		}


}//end class