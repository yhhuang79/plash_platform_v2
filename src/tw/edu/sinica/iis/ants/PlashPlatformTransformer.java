package tw.edu.sinica.iis.ants;

import java.util.Calendar;
import java.util.Map;


import org.hibernate.SessionFactory;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.http.transformers.HttpRequestBodyToParamMap;


public class PlashPlatformTransformer extends HttpRequestBodyToParamMap {
    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Object transform(MuleMessage message, String encoding)
			throws TransformerException {

		System.out.println("PLASH Platform Transformer Start:\t"+ Calendar.getInstance().getTimeInMillis());
		//System.out.println(message.getPayload().toString());		
		String url = message.getPayload().toString() + "&timefrom1="+Calendar.getInstance().getTimeInMillis();
		String asl_id = "";
		if(url.indexOf("&", url.indexOf("asl_id=")) > 0){
			//asl
		}
		/*Map map = (Map) message.getPayload();
    	map.put("timefrom1", Calendar.getInstance().getTimeInMillis());    
    	message.setPayload(map);*/
		message.setPayload(url);
		System.out.println("PLASH Platform Transformer End:\t"+ Calendar.getInstance().getTimeInMillis());
		return super.transform(message, encoding);
	}

}
