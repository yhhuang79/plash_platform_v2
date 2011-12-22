package tw.edu.sinica.iis.ants;

import java.io.Serializable;
import java.util.*;

import javax.jms.ObjectMessage;

/**
 * This class is the abstract  class for storing execution result status  
 * An inherited class will be an entry item in a log stack or result status stack
 * This class is intended to be used internally, hence OOP concepts are loosen
 * To save memory and processing resources, setters and getters are not used,
 * instead public variables are used
 * @author Yi-Chun Teng
 *
 */
public abstract class ExecutionResultStatus implements Serializable {

	/**
	 * serial version
	 */
	private static final long serialVersionUID = -4200143237270550354L;
	
	/**
	 * class name
	 */
	public String componentIdentifier;
	public final Long timeStamp;

	
	/**
	 * Default constructor
	 * If this constructoris used, the componentIdentifier is not specified
	 * However the caller may set them manually after the object has been initialized
	 */
	public ExecutionResultStatus() {
		componentIdentifier = "Not specified";
		timeStamp = Calendar.getInstance().getTimeInMillis();
	}//end constructor
	
	/**
	 * Constructor
	 * This constructor takes an object argument to specify the caller which is the object that uses this log item
	 * The constructor will automatically parse the class and object id
	 * @param caller This argument specifies who uses this log item
	 * 
	 */
	public ExecutionResultStatus(Object caller) {
    	StringTokenizer st = new StringTokenizer(caller.toString(),".", false);    
    	while (st.hasMoreTokens()) {
    		componentIdentifier = st.nextToken();
    	}//end while

		timeStamp = Calendar.getInstance().getTimeInMillis();

	}//end constructor	
	
	public Serializable getObject() {
		return this;
	}//end method
	
	public void setObject(Serializable object) {
		
	}//end method
	

}//end method
