/**
 * 
 */
package tw.edu.sinica.iis.ants.components;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.vividsolutions.jts.geom.Geometry;

import tw.edu.sinica.iis.ants.AbnormalResult;
import tw.edu.sinica.iis.ants.DB.T_FriendList;
import tw.edu.sinica.iis.ants.DB.T_Login;
import tw.edu.sinica.iis.ants.DB.T_TripData;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

/**
 * This service component tries to parse input argument and find all matching user names
 * The component receives following arguments:
 * name - the name string to be matched
 * userID - the user id to be excluded
 * 
 * @author Yi-Chun Teng
 *
 */
public class GetAllUserListComponent extends PLASHComponent {

	/* (non-Javadoc)
	 * @see tw.edu.sinica.iis.ants.componentbase.PLASHComponent#serviceMain(java.util.Map)
	 */
	@Override
	public Object serviceMain(Map map) {
		tskSession = sessionFactory.openSession();
		
		List<String> queryResult = null;
		long lastAccessTime = 0;
	     
		try {
			int userID, FirstResult, MaxResult;				
			String tmpUserID, tmpFR, tmpMR, tmpName;
			if ((tmpUserID = (String)map.remove("userid")) == null) {				
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "User id must be specified";
				//return returnUnsuccess(map,err);
		        return map;		        
			} else {
				userID = Integer.parseInt(tmpUserID);
			}//fi

			if ((tmpFR = (String)map.remove("FirstResult")) == null) {
				FirstResult = 0;
			} else {
				FirstResult = Integer.parseInt(tmpFR);
			}//fi

			if ((tmpMR = (String)map.remove("MaxResult")) == null) {
				MaxResult = 0;
			} else {
				MaxResult = Integer.parseInt(tmpMR);
			}//fi
			
			if ((tmpName = (String)map.remove("name")) == null) {
				tmpName = "*";
			}
			
			if (Calendar.getInstance().getTimeInMillis() - lastAccessTime > 1800000) {
				//obtain the record			
		    	Criteria criteriaNameData = tskSession.createCriteria(T_Login.class);
		    	criteriaNameData.add(Restrictions.ne("sid", userID));
		    	criteriaNameData.setFirstResult(FirstResult);
		    	if(MaxResult != 0)
		    		criteriaNameData.setMaxResults(MaxResult);
		    	//criteriaNameData.setResultTransformer(Criteria.ROOT_ENTITY );
		    				
		    	queryResult = (List<String>) criteriaNameData.list();
		    	lastAccessTime = Calendar.getInstance().getTimeInMillis();
				tskSession.close();				
			} //fi
			
			List<Map> resultList = new ArrayList<Map>();
			if (queryResult.size()  == 0 ) {
				return map;
			} else {
				Iterator uls = queryResult.iterator();
				Map oneuser;
				while(uls.hasNext()) {
					T_Login ul = (T_Login) uls.next();
					if (ul.getUsername().toLowerCase().startsWith(tmpName.toLowerCase())) {						
						oneuser = new HashMap();
						oneuser.put("id", ul.getSid());
						oneuser.put("name", ul.getUsername());
						oneuser.put("email", ul.getEmail());
						oneuser.put("image", "http://developer.android.com/assets/images/icon_download.jpg");
						resultList.add(oneuser);
					}//fi */
				}//rof
				map.put("user_list", resultList);

			}//fi		

			
		} catch (NullPointerException e) { //Most likely due to invalid arguments 
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 002;
	        err.explaination = "NullPointerException, most likely due to invalid parameters";
	        System.out.println(err.explaination);
	        return map;		
			//return returnUnsuccess(map,err);

			
		} catch (NumberFormatException e) { //invalid arguments 
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 002;
	        err.explaination = "NumberFormatException, most likely due to invalid parameters";
	        System.out.println(err.explaination);
	        return map;		
			//return returnUnsuccess(map,err);
		} catch (HibernateException he) {
	        AbnormalResult err = new AbnormalResult(this,'E');
	        err.refCode = 002;
	        err.explaination = "HibernateException, please check the validity of user and trip ids and database integrity";
	        System.out.println(err.explaination);
	        return map;		
			//return returnUnsuccess(map,err);

		}//end try catch			//*/
		
		return map;
		
	}//end method

}//end class
