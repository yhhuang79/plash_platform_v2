package tw.edu.sinica.iis.ants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.mule.api.MessagingException;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.api.transformer.TransformerException;
import org.mule.routing.outbound.AbstractOutboundRouter;

import tw.edu.sinica.iis.ants.DB.T_Login;

public class MyRouter extends AbstractOutboundRouter {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public boolean isMatch(MuleMessage message) throws MessagingException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public MuleMessage route(MuleMessage message, MuleSession session)
			throws MessagingException {

		System.out.println("My Router Start:\t"+ Calendar.getInstance().getTimeInMillis());
		

		int endpoint_id = -1;
		Map map = null;
		int step = -1;
		try {
			map	= (Map) message.getPayload(Map.class);
		} catch (TransformerException e2) {
			e2.printStackTrace();
		}
		step = Integer.valueOf(map.get("step").toString());
		int asl_id = Integer.valueOf(map.get("asl_id").toString());
		
		
		
		

		Session sessionH = sessionFactory.openSession(); 
		SQLQuery query = sessionH.createSQLQuery("select service.endpoint_id from plash.routing as routing, plash.service as service where routing.asl_id=? and routing.step=? and service.id=routing.service_id");
		query.setInteger(0, asl_id);
		query.setInteger(1, step);		
		if(query.uniqueResult() != null)
			endpoint_id = Integer.parseInt(query.uniqueResult().toString());
		else
			endpoint_id = -1;
 
		sessionH.close();
		
		
		
		if(map.containsKey("tostep") && Integer.parseInt(map.get("tostep").toString()) == step){
			endpoint_id = -1;
		}
		step++;
		map.put("step", step);
		message.setPayload(map);
		OutboundEndpoint ep=null;
		
		if(endpoint_id != -1)
			ep = (OutboundEndpoint) getEndpoints().get(endpoint_id);
		else
			ep = (OutboundEndpoint) getEndpoints().get(4);
			
		
		
		try {
			if(endpoint_id == -1 || endpoint_id == 4){
				send(session, message, (OutboundEndpoint) getEndpoints().get(5));
				System.out.println("BBBBBBBB");
			}
			
			

			System.out.println("My Router End:\t"+ Calendar.getInstance().getTimeInMillis());
			return send(session, message, ep);
		} catch (MuleException e) {
			throw new CouldNotRouteOutboundMessageException(message, ep, e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
