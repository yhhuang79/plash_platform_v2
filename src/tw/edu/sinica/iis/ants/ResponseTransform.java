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

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.util.IOUtils;

import com.vividsolutions.jts.geom.Geometry;


import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ResponseTransform extends AbstractTransformer {
    
    public ResponseTransform()   {
        super();
        this.registerSourceType(Map.class);
        this.setReturnClass(String.class);
    }

    public Object doTransform(Object src, String encoding) throws TransformerException   {
    	
    	Map map = (Map)src;
    	map.put("timeto", Calendar.getInstance().getTimeInMillis());    

		Logger logger = Logger.getLogger("tw.edu.plash");     
		
		
		logger.debug(((Long)map.get("timeto") - Long.parseLong((String)map.get("timefrom1"))));


		//map = checkMap(map);
		
		JSONObject j = new JSONObject(map);
		
		return j.toString();
    	//return map;
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
