package tw.edu.sinica.iis.ants.components;

import java.util.Map;
import org.hibernate.SessionFactory;

public class PLASHComponent {


	protected SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public PLASHComponent() {

    }

    public Object theMainLogic(Map map) {
		return map;
	}
    
}
