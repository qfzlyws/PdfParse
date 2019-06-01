package com.dgys.app.dao;

import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.dgys.app.model.OrderDetail;
import com.dgys.app.util.HibernateUtil;

public class OrderDao {
	public static void saveOrder(OrderDetail orderDetail)
	{
		if(orderDetail != null)
		{			
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
			
			Session session = sessionFactory.openSession();
			
			Transaction t = session.beginTransaction();
			
			try
			{
				session.save(orderDetail);
			
				t.commit();
				
			}catch(Exception ex)
			{
				t.rollback();
				throw ex;
			}
			finally{
				session.close();
			}
		}
	}
}
