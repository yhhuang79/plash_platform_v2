package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Login;

public class ActivateComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public ActivateComponent() {

	}

	public Object greet(Map map) {
		System.out.println("Test Start:\t"+ Calendar.getInstance().getTimeInMillis());
		/*
		 * Activate Action 
		 */
		
		
		Session session = sessionFactory.openSession(); 

		String username = null;
        String password = null;
        String passcode = null;
        String deviceType = null;
        
        if (map.containsKey("username")) {
        	username = map.get("username").toString();
        }
        if (map.containsKey("password")){
        	password = map.remove("password").toString();
        }
        if (map.containsKey("passcode")){
        	passcode = map.remove("passcode").toString();
        }
        if (map.containsKey("deviceType")){
        	deviceType = map.get("deviceType").toString();
        }
        
        if (!username.isEmpty() && !password.isEmpty() && !passcode.isEmpty()){
        	
        	Criteria criteria = session.createCriteria(T_Login.class);
        	criteria.add(Restrictions.eq("username", username));
        	
        	Iterator users = criteria.list().iterator(); 
        	
        	if (users.hasNext()){
        		T_Login user = (T_Login) users.next();
        		
        		if (username.equals(user.getUsername())){ //check username
        			
        			if(password.equals(user.getPassword())){ //check password
        				
        				if(passcode.equals(user.getPasscode())){ //check passcode
        					
        					user.setConfirmed(true);
                			Transaction tx = session.beginTransaction();
                			session.save(user);
                			tx.commit();
                			map.put("message", "Successful Activate");
                			
        				} else{ //wrong passcode
        					
        					map.put("message", "Invalid Activation Code");
        				}
        			} else{ //wrong password
        				
        				map.put("message", "Invalid Password");
        			}
        		} else { //wrong username
        			
        			map.put("message", "Invalid Username");
        		}
        		
        	} else { //end if (result.next())
        		//if (result.next()) is empty = no item found in DB
        		map.put("message", "Invalid Username");
        	}
        	
        
        	
        } else {
        	//Username, Password, or Activation Code is empty
        	map.put("message", "Empty Field");
        }
        
             
		session.close();
		
		/*
		 * End of Programming Logic Implementation
		 */
		System.out.println("Test End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
}
