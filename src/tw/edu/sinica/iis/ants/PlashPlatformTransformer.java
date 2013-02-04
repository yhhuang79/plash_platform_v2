package tw.edu.sinica.iis.ants;

import java.util.*;



import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;



public class PlashPlatformTransformer extends AbstractTransformer {

    
    public PlashPlatformTransformer()   {
        super();
        
        this.registerSourceType(Map.class);
        this.setReturnClass(String.class);
        //this.setReturnClass(java.util.Map.class);
       // this.setReturnClass(MuleMessage.class);
    }//end constructor

    public Object doTransform(Object src, String encoding) throws TransformerException   {
    	System.out.println("This is PLASH(R) transformer specifically designed for geospatial data, encoding: " + encoding);
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
    	
    	JSONObject converter = new JSONObject(srcMap);	
		return converter.toString(); //*/


    }//end method
    
}//end class
