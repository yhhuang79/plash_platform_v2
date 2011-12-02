package tw.edu.sinica.iis.ants;

/**
 * This class is a simply POJO that encapsulates the abnormal return status messages.
 * An AbnormalResult object should gives the following information:
 * 
 * @author Yi-Chun Teng
 *
 */
public class AbnormalResult extends ExecutionResultStatus {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4200143237270550320L;
	
	/**
	 * Type of error:
	 * E - error
	 * W - warning
	 * I - interrupted
	 */
	public final char type;
	
	/**
	 * Error code
	 */
	public short errorCode;
	
	/**
	 * log exception stack
	 * dump exception stack 
	 */	
	public String exceptionStack;
	
	/**
	 * Explain the error, if available
	 */
	public String explaination;
	
	/**
	 * Additional message
	 */
	public String msg;
	

	
	/**
	 * Constructor, error type must be specified
	 * Type of error:
	 * E - error
	 * W - warning
	 * I - interrupted
	 * @param errType Indicates error type
	 */
	public AbnormalResult (Object caller, char errType) {
		super(caller);
		type = errType;		
	}//end method
	
}//end class
