package tw.edu.sinica.iis.ants.components;

import java.util.Map;
import org.hibernate.SessionFactory;

/**
 * The general abstract class for PLASH component 
 * @author Yi-Chun Teng
 *
 */
public abstract class PLASHComponent {


	protected SessionFactory sessionFactory;

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
     * Dummy constructor
     */
    public PLASHComponent() {

    }//end constructor
    
    /**
     * The main method that performs the service of the component
     * @param map Contains all necessary parameters and data
     * @return Object Return type should be an instance of HashMap. <br>
     * 			This map contains results as well as parameters and data for next component 
     */
    public abstract Object serviceMain(Map map);
    
}//end class
