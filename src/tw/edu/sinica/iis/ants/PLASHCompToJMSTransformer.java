package tw.edu.sinica.iis.ants;

import java.io.Serializable;
import java.util.*;

import javax.jms.JMSException;

import org.apache.activemq.command.*;
import org.hibernate.*;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class PLASHCompToJMSTransformer extends AbstractTransformer {

    private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}//end method
		
	@Override
	protected Object doTransform(Object src, String encoding)	throws TransformerException {

		Map haha = (Map)src;
		System.out.println("hahahaha, gone here hahahaha");
		ActiveMQObjectMessage om = new ActiveMQObjectMessage();
		ActiveMQMapMessage mm = new  ActiveMQMapMessage();
		try {
			//om.setObject((Serializable) haha.remove("teststatus"));
			mm.setString("hahaha", "kekeke");
		} catch (JMSException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return mm;
	}//end method

}//end class
