package com.dgys.app.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	
	private static SessionFactory buildSessionFactory() throws Exception {
		SessionFactory sessionFactory = null;
		
		try{
			// Create the SessionFactory from hibernate.cfg.xml
	    	Configuration configuration = new Configuration();
	    	configuration.configure("hibernate.cfg.xml");
	    	
	    	sessionFactory = configuration.buildSessionFactory();
		}catch(HibernateException ex)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			
			Exception newEx = new Exception(sw.toString()); 
			throw newEx;
		}
    	
        return sessionFactory;
        
    }
	
	public static SessionFactory getSessionFactory() throws Exception{
		if(sessionFactory == null) sessionFactory = buildSessionFactory();
        return sessionFactory;
    }
}
