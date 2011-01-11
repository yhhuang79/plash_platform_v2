package tw.edu.sinica.iis.ants;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class sendMail {

	String userName = "plash.iis.sinica.edu.tw@gmail.com";
	String passWord = "plash@sinica";
	String host = "smtp.gmail.com";
	String port = "465";
	String starttls = "ture";
	String auth = "true";
	boolean debug = true;
	String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
	String fallback = "false";
	
	String[] cc={""};
    String[] bcc={""};

    
	public synchronized boolean sendActivationCode(String[] to, String passcode, String preactivateLink, String activateLink){
		
    	String subject = "Activation Code from Plash Project";
        
        String preBody = "Thank you for register with Plash Project.\n\n" +
						"Please keep this email for your records, as it contains an\n" +
						"important activation code that you may need should you ever\n" +
						"encounter problems or forget your password.\n" +
						"\n" +
						"You can activate your account at \n" +
						activateLink +
						"\n\n"+
						"Or enter your acivation code \""+passcode+"\" by login at:\n"+
						preactivateLink+"\n\n";
        
        String postBody = "\n\n\n" +
        				"Enjoy!\n\n" +
        				"The Plash Team";
        
        //String body = preBody.concat(text.concat(postBody));
        String body = preBody.concat(postBody);
    	
    	 	Properties props = new Properties();
            //Properties props=System.getProperties();
	        props.put("mail.smtp.user", userName);
	        props.put("mail.smtp.host", host);
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.port", port);
	                
	        if(!"".equals(starttls))
	        	props.put("mail.smtp.starttls.enable",starttls);
	        
	        props.put("mail.smtp.auth", auth);
	        
	        if(debug){
	        	props.put("mail.smtp.debug", "true");
	        }else{
	        	props.put("mail.smtp.debug", "false");         
	        }
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.socketFactory.port", port);
	               
	        if(!"".equals(socketFactoryClass))
	        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
	        
	        if(!"".equals(fallback))
	        	props.put("mail.smtp.socketFactory.fallback", fallback);
    
	        try
	        {
                Session session = Session.getDefaultInstance(props, null);
	            session.setDebug(debug);
	            MimeMessage msg = new MimeMessage(session);
	            msg.setText(body);
	            msg.setSubject(subject);
	            msg.setFrom(new InternetAddress("plash.iis.sinica.edu.tw@gmail.com"));
                
	            for(int i=0;i<to.length;i++){
                	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
        
//	            for(int i=0;i<cc.length;i++){
//                	msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
//                }
//        
//	            for(int i=0;i<bcc.length;i++){
//                	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
//                }
        
	            msg.saveChanges();
                Transport transport = session.getTransport("smtp");
                transport.connect(host, userName, passWord);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
                return true;
            }
            catch (Exception mex)
            {
            	mex.printStackTrace();
            	return false;
    
            }
            
     }

	public synchronized boolean sendFriendRequest(String[] to, String personalname, String friendname, String message, String confirmLink){
    	
        String subject = personalname+" added you as a friend on Plash Project!";
        
        String preBody = "Hi "+friendname+",\n\n"+
        				personalname+" added you as a friend on Plash Project - Map'nTrackFriends.\n"+ 
        				"We need to confirm that you know "+personalname+ " in order for you to be friend on Map'nTrackFriends.\n\n"+
        				"To confirm this friend request, follow the link below:\n" +
						confirmLink +
						"\n";
        
        String postBody = "\n\n\n" +
        				"Thank you!\n\n" +
        				"The Plash Team";
        
        //String body = preBody.concat(text.concat(postBody));
        String body = preBody.concat(postBody);
    	
    	 	Properties props = new Properties();
            //Properties props=System.getProperties();
	        props.put("mail.smtp.user", userName);
	        props.put("mail.smtp.host", host);
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.port", port);
	                
	        if(!"".equals(starttls))
	        	props.put("mail.smtp.starttls.enable",starttls);
	        
	        props.put("mail.smtp.auth", auth);
	        
	        if(debug){
	        	props.put("mail.smtp.debug", "true");
	        }else{
	        	props.put("mail.smtp.debug", "false");         
	        }
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.socketFactory.port", port);
	               
	        if(!"".equals(socketFactoryClass))
	        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
	        
	        if(!"".equals(fallback))
	        	props.put("mail.smtp.socketFactory.fallback", fallback);
    
	        try
	        {
                Session session = Session.getDefaultInstance(props, null);
	            session.setDebug(debug);
	            MimeMessage msg = new MimeMessage(session);
	            msg.setText(body);
	            msg.setSubject(subject);
	            msg.setFrom(new InternetAddress("plash.iis.sinica.edu.tw@gmail.com"));
                
	            for(int i=0;i<to.length;i++){
                	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
        
//	            for(int i=0;i<cc.length;i++){
//                	msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
//                }
//        
//	            for(int i=0;i<bcc.length;i++){
//                	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
//                }
        
	            msg.saveChanges();
                Transport transport = session.getTransport("smtp");
                transport.connect(host, userName, passWord);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
                return true;
            }
            catch (Exception mex)
            {
            	mex.printStackTrace();
            	return false;
    
            }
     }

	public synchronized boolean sendInvitation(String[] to, String personalname, String friendname, String message, String inviteLink){
    	
        String subject = personalname+" invite you to join and become a friend on Plash Project!";
        
        String preBody = "Hi "+friendname+",\n\n"+
        				personalname+" invite you to join and become a friend on Plash Project - Map'nTrackFriends.\n"+ 
        				"We need to confirm that you would like to join Plash Project and become a friend with " + personalname+" on Map'nTrackFriends.\n\n"+
        				"To comfirm this request, follow the link below:\n" +
						inviteLink +
						"\n";
        
        String postBody = "\n\n\n" +
        				"Thank you!\n\n" +
        				"The Plash Team";
        
        //String body = preBody.concat(text.concat(postBody));
        String body = preBody.concat(postBody);
    	
    	 	Properties props = new Properties();
            //Properties props=System.getProperties();
	        props.put("mail.smtp.user", userName);
	        props.put("mail.smtp.host", host);
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.port", port);
	                
	        if(!"".equals(starttls))
	        	props.put("mail.smtp.starttls.enable",starttls);
	        
	        props.put("mail.smtp.auth", auth);
	        
	        if(debug){
	        	props.put("mail.smtp.debug", "true");
	        }else{
	        	props.put("mail.smtp.debug", "false");         
	        }
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.socketFactory.port", port);
	               
	        if(!"".equals(socketFactoryClass))
	        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
	        
	        if(!"".equals(fallback))
	        	props.put("mail.smtp.socketFactory.fallback", fallback);
    
	        try
	        {
                Session session = Session.getDefaultInstance(props, null);
	            session.setDebug(debug);
	            MimeMessage msg = new MimeMessage(session);
	            msg.setText(body);
	            msg.setSubject(subject);
	            msg.setFrom(new InternetAddress("plash.iis.sinica.edu.tw@gmail.com"));
                
	            for(int i=0;i<to.length;i++){
                	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
        
//	            for(int i=0;i<cc.length;i++){
//                	msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
//                }
//        
//	            for(int i=0;i<bcc.length;i++){
//                	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
//                }
        
	            msg.saveChanges();
                Transport transport = session.getTransport("smtp");
                transport.connect(host, userName, passWord);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
                return true;
            }
            catch (Exception mex)
            {
            	mex.printStackTrace();
            	return false;
    
            }
     }

	public synchronized boolean sendAppInvitation(String[] to, String personalname, String friendname, String appName, String apkLink){
    	
        String subject = personalname+" invite you to join and become a friend on Plash Project's" +appName;
        
        String preBody = "Hi "+friendname+",\n\n"+
        				"Your friend "+personalname+" in Plash invite you to join and to try a new application - "+appName+".\n"+ 
        				"If you would like to try the new application "+appName+ "with " + personalname+ ".\n"+
        				"Use your phone, you can download and install the " +appName+ ".\n\n"+
        				"To download "+appName+", follow the link below:\n" +
						apkLink +
						"\n";
        
        String postBody = "\n\n\n" +
        				"Thank you!\n\n" +
        				"The Plash Team";
        
        //String body = preBody.concat(text.concat(postBody));
        String body = preBody.concat(postBody);
    	
    	 	Properties props = new Properties();
            //Properties props=System.getProperties();
	        props.put("mail.smtp.user", userName);
	        props.put("mail.smtp.host", host);
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.port", port);
	                
	        if(!"".equals(starttls))
	        	props.put("mail.smtp.starttls.enable",starttls);
	        
	        props.put("mail.smtp.auth", auth);
	        
	        if(debug){
	        	props.put("mail.smtp.debug", "true");
	        }else{
	        	props.put("mail.smtp.debug", "false");         
	        }
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.socketFactory.port", port);
	               
	        if(!"".equals(socketFactoryClass))
	        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
	        
	        if(!"".equals(fallback))
	        	props.put("mail.smtp.socketFactory.fallback", fallback);
    
	        try
	        {
                Session session = Session.getDefaultInstance(props, null);
	            session.setDebug(debug);
	            MimeMessage msg = new MimeMessage(session);
	            msg.setText(body);
	            msg.setSubject(subject);
	            msg.setFrom(new InternetAddress("plash.iis.sinica.edu.tw@gmail.com"));
                
	            for(int i=0;i<to.length;i++){
                	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
        
//	            for(int i=0;i<cc.length;i++){
//                	msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
//                }
//        
//	            for(int i=0;i<bcc.length;i++){
//                	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
//                }
        
	            msg.saveChanges();
                Transport transport = session.getTransport("smtp");
                transport.connect(host, userName, passWord);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
                return true;
            }
            catch (Exception mex)
            {
            	mex.printStackTrace();
            	return false;
    
            }
     }
	
	public synchronized boolean sendConfirmFriendRequest(String[] to, String friendname, String link){
    	
        String subject = friendname+" confirmed you as a friend on Plash Project...";
        
        
        
        String preBody = friendname+" confirm you as a friend on Plash Project - Map'nTrackFriends.\n"+ 
        				"To track where is " +friendname+ ", login to your account at:\n"+
        				link +
						"\n";
        
        String postBody = "\n\n\n" +
        				"Thank you!\n\n" +
        				"The Plash Team";
        
        //String body = preBody.concat(text.concat(postBody));
        String body = preBody.concat(postBody);
    	
    	 	Properties props = new Properties();
            //Properties props=System.getProperties();
	        props.put("mail.smtp.user", userName);
	        props.put("mail.smtp.host", host);
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.port", port);
	                
	        if(!"".equals(starttls))
	        	props.put("mail.smtp.starttls.enable",starttls);
	        
	        props.put("mail.smtp.auth", auth);
	        
	        if(debug){
	        	props.put("mail.smtp.debug", "true");
	        }else{
	        	props.put("mail.smtp.debug", "false");         
	        }
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.socketFactory.port", port);
	               
	        if(!"".equals(socketFactoryClass))
	        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
	        
	        if(!"".equals(fallback))
	        	props.put("mail.smtp.socketFactory.fallback", fallback);
    
	        try
	        {
                Session session = Session.getDefaultInstance(props, null);
	            session.setDebug(debug);
	            MimeMessage msg = new MimeMessage(session);
	            msg.setText(body);
	            msg.setSubject(subject);
	            msg.setFrom(new InternetAddress("plash.iis.sinica.edu.tw@gmail.com"));
                
	            for(int i=0;i<to.length;i++){
                	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
        
//	            for(int i=0;i<cc.length;i++){
//                	msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
//                }
//        
//	            for(int i=0;i<bcc.length;i++){
//                	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
//                }
        
	            msg.saveChanges();
                Transport transport = session.getTransport("smtp");
                transport.connect(host, userName, passWord);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
                return true;
            }
            catch (Exception mex)
            {
            	mex.printStackTrace();
            	return false;
    
            }
     }

	public synchronized boolean sendPassword(String[] to, String username, String password){
		
    	String subject = "Your Account Information from Plash Project";
        
        String preBody = "Dear "+username+":\n\n" +
						"Your USER NAME and PASSWORD for your account is as folllows: \n\n" +
						"USER NAME: "+username+"\n" +
						"PASSWORD: "+password+"\n" +
						"\n" +
						"Plase note that your USER NAME and PASSWORD is case-sensitive.\n" +
						"\n\n";
						
						
        String postBody = "\n\n\n" +
        				"Enjoy!\n\n" + 
        				"The Plash Team";
        
        //String body = preBody.concat(text.concat(postBody));
        String body = preBody.concat(postBody);
    	
    	 	Properties props = new Properties();
            //Properties props=System.getProperties();
	        props.put("mail.smtp.user", userName);
	        props.put("mail.smtp.host", host);
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.port", port);
	                
	        if(!"".equals(starttls))
	        	props.put("mail.smtp.starttls.enable",starttls);
	        
	        props.put("mail.smtp.auth", auth);
	        
	        if(debug){
	        	props.put("mail.smtp.debug", "true");
	        }else{
	        	props.put("mail.smtp.debug", "false");         
	        }
	        
	        if(!"".equals(port))
	        	props.put("mail.smtp.socketFactory.port", port);
	               
	        if(!"".equals(socketFactoryClass))
	        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
	        
	        if(!"".equals(fallback))
	        	props.put("mail.smtp.socketFactory.fallback", fallback);
    
	        try
	        {
                Session session = Session.getDefaultInstance(props, null);
	            session.setDebug(debug);
	            MimeMessage msg = new MimeMessage(session);
	            msg.setText(body);
	            msg.setSubject(subject);
	            msg.setFrom(new InternetAddress("plash.iis.sinica.edu.tw@gmail.com"));
                
	            for(int i=0;i<to.length;i++){
                	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
        
//	            for(int i=0;i<cc.length;i++){
//                	msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
//                }
//        
//	            for(int i=0;i<bcc.length;i++){
//                	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
//                }
        
	            msg.saveChanges();
                Transport transport = session.getTransport("smtp");
                transport.connect(host, userName, passWord);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
                return true;
            }
            catch (Exception mex)
            {
            	mex.printStackTrace();
            	return false;
    
            }
            
     }
}
