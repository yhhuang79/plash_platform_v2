package tw.edu.sinica.iis.ants.components;

import java.util.*;


import java.util.regex.*;


import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;
import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.sendMail;

/**
 * Set login infomation component
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param   a map that contains the following keys: <br>
 * 			userid - required. This indicates which user you are referring to <br>
 * 			friend_id - optional. When provided, this component returns a list of trip_ids that belongs to the user and this friend <br>
 * 			trip_id - optional. When provided, this component returns a list of friends that the user has shared this trip with <br> 
 *			If neigher friend_id nor trip_id is provided, then the component will return a list of trip ids where the key of trip id indicate friend id.
 * @return  map containing a list of IDs. empty list if no ID found
 * @example	http://localhost:1234/GetAuthFriend?userid=1&tripid=555       
 * @note	Follow the algorithm implemented in the original server
 */
public class SetLoginComponent extends PLASHComponent {


	private Session tskSession; //task session

	public SetLoginComponent() {
		super();	
		//enableDebugLog();
	}//end constructor

	@Override
	public Object serviceMain(Map map) {
		

		tskSession = sessionFactory.openSession(); 


        String username, password;
		
		
		try {
			

			if ((username = (String)map.remove("username")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 002;
		        err.explaination = "User name must be specified";
				return returnUnsuccess(map,err);
			}//fi
			if ((password = (String)map.remove("password")) == null) {				
				getElapsed();
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 002;
		        err.explaination = "Password must be specified";
				return returnUnsuccess(map,err);
			}//fi
			


	        Criteria loginCriteria = tskSession.createCriteria(T_Login.class);
	        loginCriteria.add(Restrictions.eq("username", username));
	        loginCriteria.add(Restrictions.eq("password", password));	        
	        T_Login loginEntry = (T_Login) loginCriteria.uniqueResult();
			
	        if (map.containsKey("new_username") && isValidUsername((String)map.get("new_username")) ){
	        	loginEntry.setUsername((String)map.remove("new_username"));
	        }//fi
	        if (map.containsKey("new_password")){
	        	loginEntry.setPassword((String)map.remove("new_password"));
	        }//fi

	        if (map.containsKey("new_email") && isValidEmailAddress((String)map.get("new_email"))){	        	
	        	loginEntry.setEmail((String)map.remove("new_email"));
	        }//fi
	        if (map.containsKey("new_phonenum")){
	        	loginEntry.setPhonenum((String)map.remove("new_phonenum"));
	        }//fi
	      	        
			
			Transaction trans = tskSession.beginTransaction();
			tskSession.save(loginEntry);
			trans.commit();
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			getElapsed();
	        tskSession.close();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 001;
	        err.explaination = "Null pointer exception, probably due to invalid argument";
			return returnUnsuccess(map,err);
		} catch (NumberFormatException e) { //invalid arguments 
			getElapsed();
	        tskSession.close();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 005;
	        err.explaination = "Invalid argument";
			return returnUnsuccess(map,err);
		} catch (ClassCastException e) {
			getElapsed();
	        tskSession.close();
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 005;
	        err.explaination = "Invalid argument. Arguement provided is not a text.";
			return returnUnsuccess(map,err);			
		}//end try catch
		
		
		
        
            
        
		return map;
	}//end method

	/**
	 * Check if the e-mail address provided is valid
	 * @param emailAddress the e-mail address
	 * @return a boolean value indicating the validity   
	 */
	public boolean isValidEmailAddress(String emailAddress) {
		String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}//end method

	public boolean isValidUsername(String username) {
		String expression = "^[A-Za-z0-9_]{6,20}$";
		CharSequence inputStr = username;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}//end method
	
	

}//end class
