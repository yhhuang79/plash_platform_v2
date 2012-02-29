package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Item;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class DelAssignItemComponent extends PLASHComponent {

	@Override
	public Object serviceMain(Map map) {

        System.out.println("DelAssignItemComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        
        if(!map.containsKey("userid") || map.get("userid").toString().equals("") || !map.containsKey("itemid") || map.get("itemid").toString().equals("")){
        	map.put("message", "Lacking of parameters or required information");
        	return map;        
        }


        Session session = sessionFactory.openSession();
        Integer userid = null;
        Integer itemid = null;

        if (map.containsKey("userid")) 
        	userid = Integer.parseInt(map.get("userid").toString());
        if (map.containsKey("itemid")) 
        	itemid = Integer.parseInt(map.get("itemid").toString());
        
        if (userid.equals("") || itemid.equals("")) {
			map.put("message", "Required information is empty");
		} else {   				

			Criteria c = session.createCriteria(T_Item.class);
			c.add(Restrictions.and(Restrictions.eq("itemid", itemid), Restrictions.eq("assigneduserid", userid)));
			if(c.list().size()<1){
				map.put("message", "no such item"); 				
			}
			else{
				T_Item item = (T_Item) c.list().get(0);
				item.setAssigned(false);
				item.setAssigneduserid(-1);
				Transaction tx = session.beginTransaction();
				session.save(item);
				tx.commit();
				map.put("message", "Success assigning this item to no one");
			} 
		}
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("DelAssignItemComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	}
	
}
