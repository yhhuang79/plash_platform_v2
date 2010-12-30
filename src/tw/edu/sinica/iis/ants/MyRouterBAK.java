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

public class MyRouterBAK extends AbstractOutboundRouter {

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
		
		
		
		
		
		try {

			String _driverName = "org.postgresql.Driver";
			String _connectDB = "postgresql";
			String _host = "localhost";
			String _database = "postgistemplate";
			String _username = "postgres";
			String _password = "root";

			Connection GeoCoreConnection = null;

			if (GeoCoreConnection == null) {
				try {
					Class.forName(_driverName);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GeoCoreConnection = DriverManager.getConnection("jdbc:"
						+ _connectDB + "://" + _host + "/" + _database + "",
						_username, _password);
			}
			PreparedStatement s;
			ResultSet rs;
			s = GeoCoreConnection.prepareStatement("select service.endpoint_id from plash.routing as routing, plash.service as service where routing.asl_id=? and routing.step=? and service.id=routing.service_id");
			s.setInt(1, asl_id);
			s.setInt(2, step);
			rs = s.executeQuery();
			if (rs.next()) {
				endpoint_id = rs.getInt("endpoint_id");
				//result = true;
				//this.sid = rs.getInt("sid") + "";
			}
			rs.close();
			s.close();
			GeoCoreConnection.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

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
			

		System.out.println("My Router End:\t"+ Calendar.getInstance().getTimeInMillis());
		
		try {
			return send(session, message, ep);
		} catch (MuleException e) {
			throw new CouldNotRouteOutboundMessageException(message, ep, e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
