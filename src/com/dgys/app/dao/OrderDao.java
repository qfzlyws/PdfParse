package com.dgys.app.dao;

import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.dgys.app.model.OrderDetail;
import com.dgys.app.model.OrderParseMain;
import com.dgys.app.util.HibernateUtil;

public class OrderDao {
	public static void saveOrder(OrderDetail orderDetail) throws Exception
	{
		if(orderDetail != null)
		{
			SessionFactory sessionFactory = null;
			
			Session session = null;
			
			Transaction t = null;
			
			try
			{
				sessionFactory = HibernateUtil.getSessionFactory();
				
				session = sessionFactory.openSession();
				
				t = session.beginTransaction();
				
				//設置廠區編號和客戶編號屬性的值
				orderDetail.setFactNo(OrderParseMain.factNo);
				orderDetail.setCustomNo(OrderParseMain.customNo);
				
				session.save(orderDetail);
			
				t.commit();
				
			}catch(Exception ex)
			{
				if(t != null)
					t.rollback();
				
				throw new Exception(ex.getMessage() + " PO NO:" + orderDetail.getPoNo());
			}		
			finally{
				if(session != null && session.isOpen())
					session.close();
			}
		}
	}
}
