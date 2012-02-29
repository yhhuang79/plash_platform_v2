package tw.edu.sinica.iis.ants.componentbase;

import java.io.*;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import tw.edu.sinica.iis.ants.*;

/**
 * The general abstract class for PLASH component 
 * It implements interfaces for debug log and 
 * @author Yi-Chun Teng
 *
 */
public abstract class PLASHComponent {

	public static boolean globalDebugMode = false;

	
	protected SessionFactory sessionFactory;
	protected Session tskSession; //task session
	
	//Debug related variables
	protected BufferedWriter debugFileLogger;
	protected boolean debugMode;
	protected long debugTimer;
	protected long tmpTimer;

	/**
	 * This is a bean property getter that obtains associated session factory object
	 * @return sessionFactory The associated session factory object
	 */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }//end method
    
    /**
     * This is a bean property setter that sets session factory object
     * @param sessionFactory The associated session factory to be set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }//end method

    /**
     * Constructor
     */
    public PLASHComponent() {
    	if (globalDebugMode) {
    		enableDebugLog();
    	}//fi
    	
    }//end constructor
    
    /**
     * Enable debug log mode
     */
    protected void enableDebugLog() {
    	if (debugFileLogger != null) { 
    		return;
    	}//fi
    	debugMode = true;
    	String fileURI = this.toString();
    	StringTokenizer st = new StringTokenizer(fileURI,".", false);
    	while (st.hasMoreTokens()) {
    		fileURI = st.nextToken();  
    	}//end while

    	
    	fileURI = "//tmp/" + fileURI + ".log";
    	
		try {			
			debugFileLogger = new BufferedWriter(new FileWriter(fileURI));
		} catch (IOException e) {				
			System.out.println("Warning: Unable to generate error log file " + fileURI);
			debugMode = false;
		};//end try    	    	
    }//end method
    
    /**
     * This method records log message and put it into default debug file log stream
     * @param msg String message to be recorded 
     */
    protected void log(String msg){
    	if (debugMode) {
			try {
				debugFileLogger.write(msg);
				debugFileLogger.write('\n');
				debugFileLogger.flush();
			} catch (IOException e) {
				System.out.println("Error: Debug logger encountered error hence stopped logging service for the component " + this.toString() + e.toString());
				debugMode = false;
			}//end try
		}//fi
    
    }//end method
    
    /**
     * This method triggers a time measurement
     * Call this to set the initial time of a measurement
     */
    protected void markTime() {
    	debugTimer = Calendar.getInstance().getTimeInMillis();    	
    }//end method

    /**
     * This method measures current time and returns time elapsed since last measurement
     * @return long elapsed time in milliseconds
     */
    protected long getElapsed() {
    	return Calendar.getInstance().getTimeInMillis() - debugTimer;    	
    }//end method
    
    /**
     * End log writer explicitly
     */
    protected void endLog() {
    	try {
			debugFileLogger.close();
		} catch (IOException e) {
			System.out.println("Error: Debug logger encountered error when writing log file hence stopped logging service for the component " + this.toString() + e.toString());
		}//end try
    }//end method
    
    /**
     * This method adds a "normal return status" to the execution result status stack in the map to indicate a successful execution of the component <br>
     * If the execution result status stack (key: resultStatus) is not present in the map, this method will create one
     * @param processedMap The map processed by this component and is just to be returned
     * @return process map along with success status pushed into the execution result stack. 
     */
	protected Map returnSuccess(Map processedMap) {
    	if (!processedMap.containsKey("resultstatus")) {    
    		processedMap.put("resultstatus",new Stack<ExecutionResultStatus>());
    	}//fi
    	((Stack<ExecutionResultStatus>)processedMap.get("resultstatus")).push(new NormalResult(this,null));
    	return processedMap;
    }//end method
    
    /**
     * This method adds a "normal return status" to the execution result status stack in the map to indicate a successful execution of the component <br>
     * If the execution result status stack (key: resultStatus) is not present in the map, this method will create one
     * @param processedMap The map processed by this component and is just to be returned
     * @param result Normal result object to be pushed to the result status stack. If you want to set your own message, use this method instead.
     * @return process map along with success status pushed into the execution result stack. 
     */
    protected Map returnSuccess(Map processedMap, NormalResult result) {
    	if (!processedMap.containsKey("resultstatus")) {    
    		processedMap.put("resultstatus",new Stack<ExecutionResultStatus>());
    	}//fi
    	((Stack<ExecutionResultStatus>)processedMap.get("resultstatus")).push(result);
    	return processedMap;
    }//end method

    
    /**
     * This method adds a "abnormal return status" to the execution result status stack in the map to indicate a erroneous execution of the component <br>
     * If the execution result status stack (key: resultStatus) is not present in the map, this method will create one
     * @param processedMap The map processed by this component and is just to be returned
     * @param result Abnormal result object
     * @return process map along with success status pushed into the execution result stack. 
     */
	protected Map returnUnsuccess(Map processedMap, AbnormalResult result) {
    	if (!processedMap.containsKey("resultstatus")) {    
    		processedMap.put("resultstatus",new Stack<ExecutionResultStatus>());
    	}//fi
    	((Stack<ExecutionResultStatus>)processedMap.get("resultstatus")).push(result);
    	return processedMap;
    }//end method
    
    
    /**
     * The main method that performs the service of the component
     * @param map Contains all necessary parameters and data
     * @return Object Return type should be an instance of HashMap. <br>
     * 			This map contains results as well as parameters and data for next component 
     */
    public abstract Object serviceMain(Map map);
    
}//end class
