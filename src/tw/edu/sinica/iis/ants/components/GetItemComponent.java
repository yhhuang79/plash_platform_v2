package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tw.edu.sinica.iis.ants.DB.T_Activity;
import tw.edu.sinica.iis.ants.DB.T_Item;

public class GetItemComponent extends PLASHComponent {

	@Override
	public Object serviceMain(Map map) {
        if(!map.containsKey("itemid") || map.get("itemid").toString().equals("")){
        	map.put("message", "Lacking of parameters or required information");
        	return map;        
        }

		System.out.println("GetItemComponent Start:\t"+ Calendar.getInstance().getTimeInMillis());

		Session session = sessionFactory.openSession(); 

		Criteria criteria = session.createCriteria(T_Item.class);
		
		criteria.add(Restrictions.eq("id", Integer.parseInt(map.get("itemid").toString())));
		
		List theResultList = criteria.list();
		
		if(theResultList.size()<1){
			map.put("message", "No such item");
		}
		else{
			T_Item item = (T_Item) theResultList.get(0);
			map.put("userid", item.getUserid().toString());
			map.put("activityid", item.getActivityid().toString());
			map.put("name", item.getName());
			map.put("price", item.getPrice().toString());
			map.put("assigned", item.getAssigned().toString());
			map.put("assigneduserid", item.getAssigneduserid().toString());
		}
		
		session.close();

		System.out.println("GetItemComponent End:\t"+ Calendar.getInstance().getTimeInMillis());
		return map;
	}
	
}
