package com.dgys.app.dao;

import org.hibernate.Session;
import com.dgys.app.model.OrderDetail;
import com.dgys.app.model.OrderParseMain;
import com.dgys.app.util.HibernateUtil;

public class OrderDao {
	
	public void saveOrder(OrderDetail orderDetail) throws Exception
	{
		if(orderDetail != null)
		{				
			//設置廠區編號和客戶編號屬性的值
			orderDetail.setFactNo(OrderParseMain.factNo);
			orderDetail.setCustomNo(OrderParseMain.customNo);
			
			Session session = HibernateUtil.getCurrentSession();
			session.save(orderDetail);
		}
	}
}
