package tw.edu.sinica.iis.ants.components;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

public class GetAssignedItemInfoOfUserComponent extends PLASHComponent {

	@Override
	public Object serviceMain(Map map) {

        if(!map.containsKey("userid") || map.get("userid").toString().equals("") || !map.containsKey("activityid") || map.get("activityid").toString().equals("")){
        	map.put("message", "Lacking of parameters or required information");
        	return map;        
        }

		System.out.println("GetAssignedItemInfoOfUserComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession(); 

		Criteria criteria = session.createCriteria(T_Item.class);
		
		criteria.add(Restrictions.eq("assigneduserid", Integer.parseInt(map.get("userid").toString())));
		
		List item_list = new ArrayList<Map>();
		Iterator ils = criteria.list().iterator(); 
		Map oneItem = null;
		while(ils.hasNext()) {
			T_Item item = (T_Item) ils.next();
			oneItem = new HashMap();
			oneItem.put("itemid", item.getId());
			oneItem.put("name", item.getName());
			oneItem.put("price", item.getPrice());
			item_list.add(oneItem);			
		}
		map.put("item_list", item_list);
		
		session.close();

		System.out.println("GetAssignedItemInfoOfUserComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
	
}
