package tw.edu.sinica.iis.ants.components;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import tw.edu.sinica.iis.ants.DB.T_FriendList;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;

public class distanceBearingComponent {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
    

    public distanceBearingComponent() {

    }

    public Object greet(Map map) {
        System.out.println("distanceBearing Start:\t"+ Calendar.getInstance().getTimeInMillis());
        /*
         * Please Implement Your Programming Logic From Here 
         */
        
        /**
         * distanceBearingComponent.java
         * 
         * return distance and bearing of two gps points
         * 
         * @author    Yu-Hsiang Huang 
         * @version   1.0, 01/26/2011; 1.1, 01/29/2011
         * @param     lat1, lng1, lat2, lng2
         * @return    distance, bearing
         * @see       connpost.java
         */

        Session session = sessionFactory.openSession(); 
        //Criteria criteria = session.createCriteria(T_FriendList.class);
        
        Double lat1 = null, lng1 = null, lat2 = null, lng2 = null;
        
        // get input params
        if (map.containsKey("lat1")) {
			lat1 = Double.valueOf(map.get("lat1").toString()).doubleValue();
		}
        if (map.containsKey("lng1")) {
        	lng1 = Double.valueOf(map.get("lng1").toString()).doubleValue();
		}
        if (map.containsKey("lat2")) {
			lat2 = Double.valueOf(map.get("lat2").toString()).doubleValue();
		}
        if (map.containsKey("lng2")) {
        	lng2 = Double.valueOf(map.get("lng2").toString()).doubleValue();
		}
        
        if (lat1==null || lng1==null || lat2==null || lng2==null) {
			map.put("message", "param error");
		} else {   
			/*	JTS Method 
			 * 	
			WKTReader fromText = new WKTReader();
            Geometry geom1 = null, geom2 = null;

            try {
				geom1 = fromText.read("POINT("+lng1+" "+lat1+")");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				geom2 = fromText.read("POINT("+lng2+" "+lat2+")");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			geom1.setSRID(4326);
			geom2.setSRID(4326);
			String returnResult = "<distance>" + DistanceOp.distance(geom1, geom2) + " <bearing>" + Angle.toDegrees(Angle.angle(geom1.getCoordinate(), geom2.getCoordinate()));
			*/
			
			String sql = "SELECT round(CAST(ST_Distance_Sphere( ST_GeomFromText('POINT("+lng1+" "+lat1+")',4326), ST_GeomFromText('POINT("+lng2+" "+lat2+")',4326)) as numeric),2) as dis, ST_Azimuth(ST_GeomFromText('POINT("+lng1+" "+lat1+")',4326),  ST_GeomFromText('POINT("+lng2+" "+lat2+")',4326))/(2*pi())*360 as bearing;";
			SQLQuery sqlQuery = session.createSQLQuery(sql);
			Iterator iterator = sqlQuery.list().iterator();
			
			Object [] oneResult = (Object []) iterator.next();
			String returnResult = oneResult[0].toString() + ";" + oneResult[1].toString();



			//map.put("trip_list", returnResult);
			map.put("result", returnResult);
		}
        
        session.close();
       
        /*
         * End of Programming Logic Implementation
         */
        System.out.println("distanceBearing End:\t"+ Calendar.getInstance().getTimeInMillis());
        return map;
    }
}