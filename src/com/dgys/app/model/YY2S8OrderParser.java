package com.dgys.app.model;

import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.HashSet;

import com.dgys.app.dao.OrderDao;
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
	private String temp = "";
	private String[] tempArray = null;
	//private boolean readedNO = false;

	public YY2S8OrderParser() {
		/*orderDetail = new OrderDetail();
		orderItems = new HashSet<>();
		
		orderDetail.setRecDt(new Timestamp(System.currentTimeMillis()));*/
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
				
				if(line.matches("[\\s\\S]+\\sNO:\\S+\\s+\\S+\\s+\\S+"))
				{
					temp = line.substring(line.indexOf("NO:"), line.length());
					tempArray = temp.split("\\s+");
					orderDetail.setPoNumber(tempArray[1]);
				}

				if(line.matches("AR\\d{8}\\s+\\d{2}")){
					tempArray = line.split("\\s+");
					
					if(tempArray.length > 1)
						orderDetail.setPoNumber(tempArray[0]);
				}
				
				if(line.matches("S\\d{7}\\s+S\\d{8,}\\s+\\d{2}")){
					tempArray = line.split("\\s+");
					
					if(tempArray.length > 1)
						orderDetail.setPoNumber(tempArray[1]);
				}
				
				if(line.matches("S\\d{7}\\s+S\\d{8,}\\s+\\d{2}") || line.matches("A\\d{7}\\s+AR\\d{8,}\\s+\\d{2}")){
					tempArray = line.split("\\s+");
					
					if(tempArray.length > 1)
						orderDetail.setPoNumber(tempArray[1]);
				}
				
				if (line.matches("[\\S\\s]*A\\d{7}\\s+AR\\d{8,}\\s+\\d{2}")) {
					temp = line.replaceFirst("A\\d{7}\\s+AR\\d{8,}\\s+\\d{2}", "");
					temp = line.replace(temp, "");
					
					tempArray = temp.split("\\s+");
					
					if(tempArray.length > 1)
						orderDetail.setPoNumber(tempArray[1]);
					
					//readedNO = false;
				}

				//if (line.startsWith("NO:") || line.matches("[\\S\\s]+\\s+NO:[\\S]+"))
				//	readedNO = true;

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

					OrderDao.saveOrder(orderDetail);
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

}
