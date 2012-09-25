package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;

import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;


import javax.net.ssl.HttpsURLConnection;


import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.*;
import org.hibernate.type.*;
import org.json.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.*;

import tw.edu.sinica.iis.ants.*;
import tw.edu.sinica.iis.ants.DB.*;
import tw.edu.sinica.iis.ants.componentbase.PLASHComponent;

/**
 * This is test component.  <br>
 * 
 *   
 * @author	Yi-Chun Teng 
 * @param	map A map object that contains trip data
 */
public class TestComponent extends PLASHComponent {



	private Session tskSession; //task session

	
	public TestComponent() {
		super();	
		enableDebugLog();
	}//end constructor

	public Object serviceMain(Map map) {



        GeometryFactory gf = new GeometryFactory(new PrecisionModel(10));
        Point p = gf.createPoint(new Coordinate(-9999,-999));
        Coordinate[] list_c = {new Coordinate(180,180),new Coordinate(180,-180),
        		new Coordinate(-180,-180),new Coordinate(-180,180)};
        LinearRing lr = new LinearRing(list_c, new PrecisionModel(), 4326);
        Polygon pol = gf.createPolygon(lr, null);
        Point pp = gf.createPoint(new Coordinate(25,-125));
		System.out.println("disjoint? " + pol.disjoint(p));
		System.out.println("disjoint? " + pol.disjoint(pp));		
		
		    

		

		return map;
   			
	

	}//end method
	

	



}//end class
