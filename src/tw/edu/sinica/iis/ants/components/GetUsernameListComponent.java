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
public class GetUsernameListComponent extends PLASHComponent {

	/* (non-Javadoc)
	 * @see tw.edu.sinica.iis.ants.componentbase.PLASHComponent#serviceMain(java.util.Map)
	 */
	@Override
	public Object serviceMain(Map map) {
		tskSession = sessionFactory.openSession();
		
		List<String> queryResult = null;
		long lastAccessTime = 0;
	     
		try {


			int userID;				
			String tmpUserID, tmpName;
			if ((tmpUserID = (String)map.remove("user_id")) == null) {				

		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "User id must be specified";
				//return returnUnsuccess(map,err);
		        return map;
		        
			} else {
				userID = Integer.parseInt(tmpUserID);
			}//fi
						
			
			if ((tmpName = (String)map.remove("name")) == null) {
				
		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "Name field must be specified";
				//return returnUnsuccess(map,err);
		        return map;								
			
				
			} else if (tmpName.length()<1){ 

		        tskSession.close();
		        AbnormalResult err = new AbnormalResult(this,'E');
		        err.refCode = 001;
		        err.explaination = "Name string lenght is too short";
				//return returnUnsuccess(map,err);
		        return map;		
			}//fi

			if (Calendar.getInstance().getTimeInMillis() - lastAccessTime > 1800000) {
				//obtain the record			
		    	Criteria criteriaNameData = tskSession.createCriteria(T_Login.class);
		    	criteriaNameData.add(Restrictions.ne("sid", userID));
		    	ProjectionList filterProjList = Projections.projectionList();  
		    	filterProjList.add(Projections.property("username"),"name");
		    	criteriaNameData.setProjection(filterProjList);

		    	criteriaNameData.setFetchSize(10);
		    	criteriaNameData.setResultTransformer(Criteria.ROOT_ENTITY );
		    				
		    	queryResult = (List<String>) criteriaNameData.list();
		    	lastAccessTime = Calendar.getInstance().getTimeInMillis();
				tskSession.close();				
			} //fi
			
			List<String> resultList = new ArrayList();
			if (queryResult.size()  == 0 ) {
				return map;
			} else {	
				for (String nameItem:queryResult) {			
			
					if (nameItem.toLowerCase().startsWith(tmpName.toLowerCase())) {						
						resultList.add(nameItem);
					}//fi */
				}//rof
				map.put("name_list", resultList);

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
