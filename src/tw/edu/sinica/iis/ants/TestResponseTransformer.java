/*
 * $Id: HttpRequestToNameString.java 11328 2008-03-12 10:27:11Z tcarlson $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package tw.edu.sinica.iis.ants;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.json.*;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.util.*;

import java.util.*;

import javax.jms.JMSException;


public class TestResponseTransformer extends AbstractTransformer {
    
    public TestResponseTransformer()   {
        super();
        this.registerSourceType(Map.class);
        this.setReturnClass(String.class);
        //this.setReturnClass(ActiveMQMapMessage.class);
    }//end constructor

    public Object doTransform(Object src, String encoding) throws TransformerException   {
    	System.out.println("This is ResponseTransform transformer, encoding: " + encoding);
    	Map srcMap = (Map)src;
    	System.out.println("Before: " + srcMap.keySet().toString());     	
    	if (srcMap.containsKey("resultstatus")) {
    		//do something meaningful
    		Stack<ExecutionResultStatus> statusStack = (Stack<ExecutionResultStatus>) srcMap.remove("resultstatus");
    		for (Object status:statusStack) {

        		if ( status instanceof AbnormalResult){        			
        	   		System.out.println("Reason: " + ((AbnormalResult)status).explaination); 			
        		} else {

        		}//fi
   			
    		}//rof

    		
    		
    		System.out.println("result status has been removed");
    	}//fi
    	
 		
    	System.out.println("After: " + srcMap.keySet().toString()); 

    	JSONObject j = new JSONObject(srcMap);
		System.out.println("Transformed: " + j.toString());
		return j.toString(); //*/
    	/*
		ActiveMQMapMessage mm = new  ActiveMQMapMessage();
		try {
			//om.setObject((Serializable) haha.remove("teststatus"));
			mm.setString("asdf", "jklm");
		} catch (JMSException e) {
			System.out.println("error:  " + e.toString());
			e.printStackTrace();
		}
		return mm;    	//*/

    }//end method
    
    public static Map checkMap(Map m){
    	Iterator i = m.keySet().iterator();
    	while(i.hasNext()){
    		String key = i.next().toString();
    		if(m.get(key) instanceof List){
    			m.put(key, makeListIntoJSONArray((List)m.get(key)));
    		}
    		if(m.get(key) instanceof Map){
    			m.put(key, checkMap((Map)m.get(key)));
    		}
    	}
    	return m;
    }
    
    public static JSONArray makeListIntoJSONArray(List l){
		return new JSONArray(l);
    	
    }
}
