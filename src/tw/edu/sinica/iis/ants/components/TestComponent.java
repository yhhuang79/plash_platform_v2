package tw.edu.sinica.iis.ants.components;

import java.sql.Timestamp;

import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;


import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.*;
import org.hibernate.type.*;
import org.json.*;

import com.vividsolutions.jts.geom.Geometry;
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
		TestMsgObj tst = new TestMsgObj();
        tst.test = "This is a test status.";
        map.put("teststatus", tst);
		//PLASHComponentExecutionResultStatus errLog = new PLASHComponentExecutionResultStatus(this);
		return map;				
   			
	

	}//end method
	

	



}//end class
