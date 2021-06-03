package com.dgys.app.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static final ThreadLocal<Session> localSession = new ThreadLocal<Session>();
	
	private static SessionFactory buildSessionFactory() {
		SessionFactory sessionFactory = null;
				
		// Create the SessionFactory from hibernate.cfg.xml
	    Configuration configuration = new Configuration();
	    configuration.configure("hibernate.cfg.xml");
	    	
	    sessionFactory = configuration.buildSessionFactory();
		    	
        return sessionFactory;
        
    }
	
	private static SessionFactory getSessionFactory() throws Exception{
		if(sessionFactory == null) sessionFactory = buildSessionFactory();
        return sessionFactory;
    }
	
	public static Session getCurrentSession() throws Exception{
		Session s = (Session) localSession.get();
		if(s == null){
			s = getSessionFactory().openSession();
			localSession.set(s);
		}
		
		return s;
	}
	
	public static void closeSession(){
		Session s = (Session) localSession.get();
		
		if(s != null)
			s.close();
		
		localSession.set(null);
	}
}
