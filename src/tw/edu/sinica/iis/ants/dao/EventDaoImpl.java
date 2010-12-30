package tw.edu.sinica.iis.ants.dao;

import org.hibernate.SessionFactory;

public class EventDaoImpl
{
    private SessionFactory sessionFactory = null;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
