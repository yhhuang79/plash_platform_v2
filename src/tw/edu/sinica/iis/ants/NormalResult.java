package tw.edu.sinica.iis.ants;

public class NormalResult extends ExecutionResultStatus {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1435958631276106452L;
	
	/**
	 * Additional message
	 */
	public String msg;
	
	/**
	 * Constructor
	 * @param caller The reference to the caller
	 * @param msg The string message to be read
	 */
	public NormalResult(Object caller, String msg) {
		super(caller);
		this.msg = msg;
	};//end constructor
	
}//end class
