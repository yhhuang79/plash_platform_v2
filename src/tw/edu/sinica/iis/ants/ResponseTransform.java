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

import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.util.IOUtils;

import java.util.*;


public class ResponseTransform extends AbstractTransformer {
    
    public ResponseTransform()   {
        super();
        this.registerSourceType(Map.class);
        this.setReturnClass(String.class);
    }//end constructor

    public Object doTransform(Object src, String encoding) throws TransformerException   {
    	System.out.print("gone here, ResponseTransform");
    	System.out.print(((Map)src).toString());
    	if (((Map)src).containsKey("resultstatus")) {
    		//do something meaningful
    		((Map)src).remove("resultstatus");
    		
    	}//fi
    	
 		
		JSONObject j = new JSONObject((Map)src);
		
		return j.toString();

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
