package com.dgys.app.model;

import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.HashSet;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.dgys.app.dao.OrderDao;
import com.dgys.app.util.HibernateUtil;
import com.dgys.app.util.StringUtil;

public class YY2S8OrderParser implements IOrderParser {
	private RandomAccessFile file = null;
	private OrderDetail orderDetail = null;
	private HashSet<OrderItem> orderItems = null;
	private OrderItem orderItem = null;
	private boolean readedSizes = false;
	private String[] sizeQtys = null;
	private String[] sizeNos = null;
	private boolean readedUniqueNo = false;
	private boolean readedPoNumber = false;
	private String temp = "";
	private String[] tempArray = null;

	public YY2S8OrderParser() {
	}

	@Override
	public void ParseOrderText(String orderFilePath) throws Exception {
		try {
			file = new RandomAccessFile(orderFilePath, "r");
			String line = StringUtil.convertToUTF8(file.readLine());

			while (line != null) {
				if (line.indexOf("訂購日期:") != -1)
				{
					orderDetail = new OrderDetail();
					orderItems = new HashSet<>();
					readedPoNumber = false;
					
					orderDetail.setRecDt(new Timestamp(System.currentTimeMillis()));
					
					orderDetail.setColpadNo(line.replace("訂購日期:", "").trim()); // 色卡編號
				}

				if(line.endsWith("NO:"))
					orderDetail.setPoNo(line.replace("NO:",""));
				
				if (sizeQtys != null)
					sizeNos = line.split("\\s+");

				if (readedSizes) {
					sizeQtys = line.split("\\s+");
					readedSizes = false;
				}

				if (line.startsWith("SIZE明細:"))
					readedSizes = true;

				if (readedUniqueNo) {
					temp = line.substring(0, line.length() - 10);
					orderDetail.setOrderDate(line.replace(temp, "").trim());
					readedUniqueNo = false;
				}

				if (line.indexOf("統一編號:") != -1)
					readedUniqueNo = true;
				
				if(!readedPoNumber && line.matches("[\\s\\S]+\\sNO:\\S+\\s+\\S+\\s+\\S+"))
				{
					temp = line.substring(line.indexOf("NO:"), line.length());
					tempArray = temp.split("\\s+");
					orderDetail.setPoNumber(tempArray[1]);
					readedPoNumber = true;
				}
				
				if(!readedPoNumber && line.matches("[\\s\\S]+\\sNO:\\S+\\s+\\S+\\s+\\S+[\\s\\S]+"))
				{
					temp = line.substring(line.indexOf("NO:"), line.length());
					tempArray = temp.split("\\s+");
					orderDetail.setPoNumber(tempArray[2]);
					readedPoNumber = true;
				}

				if(!readedPoNumber && line.matches("AR\\d{8}\\s+\\d{2}")){
					tempArray = line.split("\\s+");
					
					if(tempArray.length > 1)
					{
						orderDetail.setPoNumber(tempArray[0]);
						readedPoNumber = true;
					}
				}
				
				if(!readedPoNumber && line.matches("S\\d{7}\\s+S\\d{8,}\\s+\\d{2}")){
					tempArray = line.split("\\s+");
					
					if(tempArray.length > 1)
					{
						orderDetail.setPoNumber(tempArray[1]);
						readedPoNumber = true;
					}
				}
				
				if(!readedPoNumber && line.matches("S\\d{7}\\s+S\\d{8,}\\s+\\d{2}") || line.matches("A\\d{7}\\s+AR\\d{8,}\\s+\\d{2}")){
					tempArray = line.split("\\s+");
					
					if(tempArray.length > 1)
					{
						orderDetail.setPoNumber(tempArray[1]);
						readedPoNumber = true;
					}
				}
				
				if (!readedPoNumber && line.matches("[\\S\\s]*A\\d{7}\\s+AR\\d{8,}\\s+\\d{2}")) {
					temp = line.replaceFirst("A\\d{7}\\s+AR\\d{8,}\\s+\\d{2}", "");
					temp = line.replace(temp, "");
					
					tempArray = temp.split("\\s+");
					
					if(tempArray.length > 1)
					{
						orderDetail.setPoNumber(tempArray[1]);
						readedPoNumber = true;
					}
				}

				if (sizeNos != null && sizeQtys != null) {
					parseOrderItem();
					sizeNos = null;
					sizeQtys = null;
				}
				
				if(line.indexOf("合計:") != -1)
				{
					tempArray = line.substring(line.indexOf("合計:")).split("\\s");
					
					if(tempArray.length > 2)
						orderDetail.setUnitPrice(Float.parseFloat(tempArray[1]));
					
					orderDetail.setOrderItems(orderItems);

					saveOrder(orderDetail);
				}

				line = StringUtil.convertToUTF8(file.readLine());
			}

		} catch (Exception ex) {
			throw ex;
		} finally {
			if (file != null)
				file.close();
		}
	}

	private void parseOrderItem() {

		for (int i = 0; i < sizeNos.length; i++) {
			orderItem = new OrderItem();
			orderItem.setOrderDetail(orderDetail);
			orderItem.setItemNumber(sizeNos[i]);
			orderItem.setOdrQty(sizeQtys[i]);

			orderItems.add(orderItem);
		}
	}
	
	private void saveOrder(OrderDetail orderDetail) throws Exception{
		Session session = null;
		Transaction t = null;
		
		try{
			session = HibernateUtil.getCurrentSession();
			t = session.beginTransaction();
			
			OrderDao orderDao = new OrderDao();
			orderDao.saveOrder(orderDetail);
			
			t.commit();
		}catch(Exception ex){
			if(t != null)
				t.rollback();
			
			throw ex;
		}finally{
			HibernateUtil.closeSession();
		}
	}

}
