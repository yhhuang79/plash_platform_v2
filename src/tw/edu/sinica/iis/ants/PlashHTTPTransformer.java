package tw.edu.sinica.iis.ants;

import java.util.*;
import java.net.*;


import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.http.transformers.HttpRequestBodyToParamMap;


public class PlashHTTPTransformer extends HttpRequestBodyToParamMap {

	@Override
	public Object transform(MuleMessage message, String encoding)	throws TransformerException {
		
		System.out.println("PlashHTTPTransformer Start:\t"+ Calendar.getInstance().getTimeInMillis());
		System.out.println("PlashHTTPTransformer End:\t"+ Calendar.getInstance().getTimeInMillis());
		return super.transform(message, encoding);
	}

}//end class
