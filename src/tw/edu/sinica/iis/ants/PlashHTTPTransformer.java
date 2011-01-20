package tw.edu.sinica.iis.ants;

import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.http.transformers.HttpRequestBodyToParamMap;


public class PlashHTTPTransformer extends HttpRequestBodyToParamMap {

	@Override
	public Object transform(MuleMessage message, String encoding)
			throws TransformerException {
		
		System.out.println("HTTP Transformer Start:\t"+ Calendar.getInstance().getTimeInMillis());
		//System.out.println(message.getPayload().toString());
		String url = message.getPayload().toString() + "&timefrom1="+Calendar.getInstance().getTimeInMillis();
		/*Map map = (Map) message.getPayload();
    	map.put("timefrom1", Calendar.getInstance().getTimeInMillis());    
    	message.setPayload(map);*/
		message.setPayload(url);
		System.out.println("HTTP Transformer End:\t"+ Calendar.getInstance().getTimeInMillis());
		return super.transform(message, encoding);
	}

}
