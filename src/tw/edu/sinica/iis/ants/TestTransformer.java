package tw.edu.sinica.iis.ants;

import java.util.Map;
import java.util.Stack;

import javax.jms.JMSException;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;

import org.mule.transformer.simple.ObjectToString;
import org.mule.transport.ConnectException;
import org.mule.transport.jms.JmsMessageUtils;
import org.mule.transport.jms.transformers.AbstractJmsTransformer;

public class TestTransformer extends AbstractJmsTransformer {


    public TestTransformer()   {
        super();
        
        this.registerSourceType(MuleMessage.class);
       
        this.setReturnClass(javax.jms.Message.class);
    }//end constructor
    
	public Object transform(MuleMessage message, String outputEncoding)	throws TransformerException {
    	System.out.println("This is test transformer, encoding: " + outputEncoding);


    	try {
			return JmsMessageUtils.toMessage(message, this.getSession()  );
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}//end try catch
    //	System.out.println("gone here, fuck this");				
		
    	



    }//end method


}//end class
