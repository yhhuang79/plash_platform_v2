package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Activity;
import tw.edu.sinica.iis.ants.DB.T_ActivityUser;
import tw.edu.sinica.iis.ants.DB.T_Item;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @goal      assign an item to an user
 * @author    Kenny
 * @version   1, 05/13/2011
 * @param     userid, itemid
 * @return    message 
 * 
 */
public class SetAssignItemComponent extends PLASHComponent {

	@Override
	public Object serviceMethod(Map map) {

        System.out.println("SetAssignItemComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());
        // Please Implement Your Programming Logic From Here

        if(!map.containsKey("userid") || !map.containsKey("itemid")){
        	map.put("message", "Lacking of parameters");
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
			c.add(Restrictions.eq("id", itemid));
			if(c.list().size()<1){
				map.put("message", "no such item"); 				
			}
			else{
				T_Item item = (T_Item) c.list().get(0);
				item.setAssigned(true);
				item.setAssigneduserid(userid);
				Transaction tx = session.beginTransaction();
				session.save(item);
				tx.commit();
				map.put("message", "Success assigning this item");
			} 
		}
        
        session.close();
        //End of Programming Logic Implementation
        System.out.println("SetAssignItemComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
	}
	
}
