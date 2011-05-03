package tw.edu.sinica.iis.ants.components;



import java.util.*;


import javax.mail.*;
import javax.mail.internet.*;


import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;


import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.xml.sax.helpers.DefaultHandler;



import tw.edu.sinica.iis.ants.DB.*;


/**
 * Send out e-mails. E-mail contents are predefined
 * 
 * @author	Yi-Chun Teng 
 * @version 
 * @param   a map that contains the following keys: "RecipientAddr", "MailXMLPath","Var1","Var2","Var3","Var4","Var5"
 * 			map parameter - RecipientAddr : recipient's address (for example orz@iis.sinica.edu.tw)
 * 			map parameter - MailXMLPath : E-mail template's path  
 * @return  map containing result status
 * @example	      
 * @note	
 */
public class SendMailComponent {
	
	String userName = "plash.iis.sinica.edu.tw@gmail.com";
	String passWord = "plash@sinica";
	String host = "smtp.gmail.com";
	String port = "465";
	String starttls = "ture";
	String auth = "true";
	boolean debug = true;
	String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
	String fallback = "false";
	

	private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }//end method

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }//end method

    public SendMailComponent() {

    }//end method

    public Object greet(Map argMap) {
    	
       	

    	MXTSHandler handler =  new MXTSHandler(argMap);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(true);
        try {
          SAXParser parser = spf.newSAXParser();
          parser.parse((String)argMap.get("MailXMLPath"), handler);
          handler.sendMail((String)argMap.get("recipientAddr"));
        } catch(Exception e) {
          String errorMessage = "Error parsing " + (String)argMap.get("MailXMLPath") + ": " + e;
          System.err.println(errorMessage);
          argMap.put("SendMail",false); //result flag, flag name to be unified, para_failed as appeared in excel file
          return argMap;
        }//end try
        
        
        System.out.println("SendEMailComponent successful end:\t"+ Calendar.getInstance().getTimeInMillis());
        argMap.put("SendMail",true);
        return argMap;			
        

		


	} //close Object greet

	
	/** 
	 * Mail XML template SAX Handler
	 * A SAX handler that handles E-mail XMLs
	 */
	public class MXTSHandler extends DefaultHandler  {
		
		private String subject, body, signature, tmpString, var1, var2, var3, var4, var5;
		private State currentState;
	
		/*
		 * Constructor
		 */
		public MXTSHandler(Map map) {
			super();
			try {
				var1 = (String) map.get("Var1");
				var2 = (String) map.get("Var2");
				var3 = (String) map.get("Var3");
				var4 = (String) map.get("Var4");
				var5 = (String) map.get("Var5");
			} catch (Exception e) {
								
			}//end catch
		

			
		}//end constructor



		/** 
		 * Receive notification of the beginning of an element.
		 */
		@Override
		public void startElement(String uri,String localName,String qName,Attributes attr) throws SAXException {
			try {
				if (qName.matches("Subject")) {
					tmpString = new String();
					currentState = State.SUBJECT;
				} else if (qName.matches("Body")) {
					tmpString = new String();				
					currentState = State.BODY;
				} else if (qName.matches("Signature")) {
					tmpString = new String();				
					currentState = State.SIGNATURE;				
				} else if (qName.matches("Var1")) {
					tmpString = tmpString.concat(var1);				
				} else if (qName.matches("Var2")) {	
					tmpString = tmpString.concat(var2);	
				} else if (qName.matches("Var3")) {	
					tmpString = tmpString.concat(var3);	
				} else if (qName.matches("Var4")) {	
					tmpString = tmpString.concat(var4);	
				} else if (qName.matches("Var5")) {	
					tmpString = tmpString.concat(var5);	
				}//fi
			} catch (Exception e) {
				System.out.println("Error: " + e.toString());
			}
			
		}//end method
	  
	  /** 
	   * End element handler
	   * 
	   */	  
	  public void endElement(String uri,String localName,String qName)throws SAXException {			
			if (qName.matches("Subject")) {
				this.subject = this.tmpString;	
				this.subject = this.subject.replace('\n', ' ').replace('\t',' ').trim();				
			} else if (qName.matches("Body")) {
				this.body = this.tmpString;				
			} else if (qName.matches("Signature")) {
				this.signature = this.tmpString;				
			}//fi			
	  }//end method

	  /** Print out the first word of each tag body. */
	  
	  public void characters(char[] chars,int startIndex,int endIndex) {
	    String data = new String(chars, startIndex, endIndex);
	    tmpString = tmpString.concat(data);

	  }//end method

	  
	  @Override
	  public void warning(SAXParseException e) throws SAXException {
	      System.out.println("Warning: " + e.getMessage());
	  }//end method

	  @Override
	  public void error(SAXParseException e) throws SAXException {
	      System.out.println("Error: " + e.getMessage());
	  }//end method

	  @Override
	  public void fatalError(SAXParseException e) throws SAXException {
	      System.out.println("Fatal Error: " + e.getMessage());
	  }//end method

	  /*
	   * Send out e-mail content
	   */
	  public synchronized boolean sendMail(String recipientAddr){

		  

	        Properties props = new Properties();

	        props.put("mail.smtp.user", userName);
	        props.put("mail.smtp.host", host);
	        
	        if(!"".equals(port)) {
	        	props.put("mail.smtp.port", port);
	        }//fi
	                
	        if(!"".equals(starttls)) {
	        	props.put("mail.smtp.starttls.enable",starttls);
	        }//fi
	        
	        props.put("mail.smtp.auth", auth);
	        
	        if(debug){
	        	props.put("mail.smtp.debug", "true");
	        }else{
	        	props.put("mail.smtp.debug", "false");         
	        }
	        
	        if(!"".equals(port)) {
	        	props.put("mail.smtp.socketFactory.port", port);
	        }//fi
	               
	        if(!"".equals(socketFactoryClass)) {
	        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
	        }//fi
	        
	        if(!"".equals(fallback)) {
	        	props.put("mail.smtp.socketFactory.fallback", fallback);
	        }//fi

	        try {
	            javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
	            session.setDebug(debug);
	            MimeMessage msg = new MimeMessage(session);
	            if (signature != null ) {
	            	msg.setText(body+"\n"+signature);
	            } else {
	            	msg.setText(body);
	            }//fi
	            msg.setSubject(subject);
	            msg.setFrom(new InternetAddress("plash.iis.sinica.edu.tw@gmail.com"));
	            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddr));             
	            msg.saveChanges();
	            Transport transport = session.getTransport("smtp");
	            transport.connect(host, userName, passWord);
	            transport.sendMessage(msg, msg.getAllRecipients());
	            transport.close();
	            return true;
	        } catch (Exception mex)   {
	        	mex.printStackTrace();
	        	return false;

	        }//end try //*/
	            
	  }//end method
		
	}//end class
	
	
	/*
	 * State enumeration
	 */
	enum State {
		SUBJECT,BODY,SIGNATURE,VAR1,VAR2,VAR3,VAR4,VAR5 		
	}//end enumeration

    
} //end class inputGpsLocationComponent
