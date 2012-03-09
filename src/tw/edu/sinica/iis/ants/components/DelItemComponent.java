package tw.edu.sinica.iis.ants.components;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class DelItemComponent extends PLASHComponent {

	@Override
	public Object serviceMain(Map map) {

        if(!map.containsKey("itemid") || map.get("itemid").toString().equals("")){
        	map.put("message", "Lacking of parameters or required information");
        	return map;        
        }

    	
        System.out.println("DelItemComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        
 
        
        Session session = sessionFactory.openSession(); 
        Criteria criteria = session.createCriteria(T_Item.class);
		       
        
		try {
			criteria.add(Restrictions.eq("itemid", Integer.parseInt(map.get("itemid").toString())));
			Iterator fls = criteria.list().iterator();
			session.delete((T_Item)fls.next());
			session.beginTransaction().commit();       
			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
			map.put("DelItemComponent",false); //result flag, flag name to be unified
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("DelItemComponent failure end1:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;
		} catch (NumberFormatException e) { //invalid arguments 
			map.put("DelItemComponent",false); //result flag, flag name to be unified, para_failed as appeared in excel file
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("DelItemComponent failure end2:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;			
		} catch (NoSuchElementException e) { //Element not found
			map.put("DelItemComponent",false); //result flag, flag name to be unified
			//map.put("error detail" , e.toString()); //perhaps put error detail?
	        System.out.println("DelItemComponent failure end3:\t"+ Calendar.getInstance().getTimeInMillis());
			return map;			
		}//end try catch              

	
		map.put("DelItemComponent",true); 
        System.out.println("DelItemComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	}
	
}
