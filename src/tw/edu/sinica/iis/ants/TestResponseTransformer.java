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

import org.json.*;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;



import java.util.*;



public class TestResponseTransformer extends AbstractTransformer {
    
    public TestResponseTransformer()   {
        super();
        
        this.registerSourceType(Map.class);
        this.setReturnClass(String.class);
        //this.setReturnClass(java.util.Map.class);
       // this.setReturnClass(MuleMessage.class);
    }//end constructor

    public Object doTransform(Object src, String encoding) throws TransformerException   {
    	System.out.println("This is TestResponseTransform transformer, encoding: " + encoding);
    	Map srcMap = (Map)src;
    	System.out.println("Before: " + srcMap.keySet().toString());     	
    	if (srcMap.containsKey("resultstatus")) {
    		//do something meaningful
    		Stack<ExecutionResultStatus> statusStack = (Stack<ExecutionResultStatus>) srcMap.remove("resultstatus");
    		for (Object status:statusStack) {
    			
        		if ( status instanceof AbnormalResult){        			        	   		
        	   		srcMap.put("abnormal_result ", "true");
        	   		srcMap.put("type", ((AbnormalResult)status).type);
        	   		srcMap.put("ref_code", ((AbnormalResult)status).refCode);
        	   		srcMap.put("explanation", ((AbnormalResult)status).explaination);        	   	
        	   		
        		} else {

        		}//fi
   			
    		}//rof

    		
    	
    	}//fi
    	
    	JSONObject j = new JSONObject();
    	Set<String> keyset = srcMap.keySet();
    	for (String key:keyset){
    		try {
				j.put(key, srcMap.get(key));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//end try catch
    		
    	}//fi
    			

    
    	//JSONObject j = new JSONObject(srcMap);				
		return j.toString(); //*/


    }//end method
    
    public static Map checkMap(Map m){
    	Iterator i = m.keySet().iterator();
    	while(i.hasNext()){
    		String key = i.next().toString();
    		if(m.get(key) instanceof List){
    			m.put(key, makeListIntoJSONArray((List)m.get(key)));
    		}//fi
    		if(m.get(key) instanceof Map){
    			m.put(key, checkMap((Map)m.get(key)));
    		}//fi
    	}//end while
    	return m;
    }//end method
    
    public static JSONArray makeListIntoJSONArray(List l){
		return new JSONArray(l);
    	
    }
}//end class
