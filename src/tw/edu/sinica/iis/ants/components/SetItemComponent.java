package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import tw.edu.sinica.iis.ants.DB.T_Item;



/**
 * @goal      create item
 * @author    Kenny
 * @version   1, 05/13/2011
 * @param     userid, activityid, name, price, assigned, assigneduserid
 * @return    message 
 * 
 */
public class SetItemComponent extends PLASHComponent {

	@Override
	public Object serviceMethod(Map map) {
		
        System.out.println("SetItemComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here

		if (!map.containsKey("userid") || !map.containsKey("activityid") || !map.containsKey("name") || !map.containsKey("price") || !map.containsKey("assigned") || !map.containsKey("assigneduserid")) {
        	map.put("message", "Lacking of parameters");
        	return map;
        }
        Session session = sessionFactory.openSession();
        Integer userid = null;
        Integer activityid = null;
        String name = null;
        Double price = 0.0;
        Boolean assigned = false;
        Integer assigneduserid = -1;

        if (map.containsKey("userid")) 
        	userid = Integer.parseInt(map.get("userid").toString());
        if (map.containsKey("activityid")) 
        	activityid = Integer.parseInt(map.get("activityid").toString());
        if (map.containsKey("name")) 
        	name = map.get("name").toString();
        if (map.containsKey("price")) 
        	price = Double.parseDouble(map.get("price").toString());
        if (map.containsKey("assigned")) 
        	assigned = Boolean.parseBoolean(map.get("assigned").toString());
        if (map.containsKey("assigneduserid")) 
        	activityid = Integer.parseInt(map.get("assigneduserid").toString());
        
        if (userid.equals("") || name.equals("") || activityid.equals("")) {
			map.put("message", "Required information is empty");
		} else {   				
				
				//store the <userid> into the database
				T_Item item = new T_Item();
				item.setUserid(userid);
				item.setActivityid(activityid);
				item.setName(name);
				item.setPrice(price);
				item.setAssigned(assigned);
				item.setAssigneduserid(assigneduserid);
				
				Transaction tx = session.beginTransaction();
				session.save(item);
				tx.commit();
				
				map.put("message", "Success creating an item"); 
			}
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("SetItemComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	}
	
}
