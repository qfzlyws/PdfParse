package com.dgys.app.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.HashSet;

import com.dgys.app.dao.OrderDao;

public class AmfitOrderParser implements IOrderParser {
	private RandomAccessFile file = null;
	private OrderDetail orderDetail = null;
	private HashSet<OrderItem> orderItems = null;
	private HashSet<OrderItemRef> itemRefs = null;
	private OrderItem orderItem = null;
	private OrderItemRef itemRef = null;
	private boolean readedTerms = false;
	private boolean readedConfirmTo = false;
	private boolean readedPAIR = false;
	private boolean readedContinued = false;
	
	public AmfitOrderParser(){
		orderDetail = new OrderDetail();
		orderItems = new HashSet<>();
		itemRefs = new HashSet<>();
		
		orderDetail.setRecDt(new Timestamp(System.currentTimeMillis()));
	}
	
	@Override
	public void ParseOrderText(String orderFilePath) throws Exception{
		try{
			
			file = new RandomAccessFile(orderFilePath,"r");
			
			String line = file.readLine();
			while(line != null){
				if(line.equals("Continued"))
					readedContinued = true;
				
				if(!readedContinued)
				{
					if(line.startsWith("Order Date:"))
						orderDetail.setOrderDate(line.replace("Order Date:", ""));
					
					if(line.endsWith("Amfit Resale Permit:"))
						orderDetail.setResalePermit(line.replace("Amfit Resale Permit:", ""));
					
					if(line.matches("\\d{7}"))
						orderDetail.setPoNumber(line);
					
					if(readedTerms)
					{
						orderDetail.setTerms(line);
						readedTerms = false;
					}
					
					if(readedConfirmTo)
					{
						orderDetail.setShipVia(line);
						readedConfirmTo = false;
					}
						
					if(line.indexOf("Terms") != -1 )
						readedTerms = true;
					
					if(line.indexOf("Confirm To:") != -1)
						readedConfirmTo = true;
				}
				
				if(line.matches("\\d{3}\\s[\\s\\S]+"))
				{
					parseOrderItem(line,"LINEITEM");
					readedPAIR = false;
				}
				
				if(readedPAIR)
				{
					itemRef = new OrderItemRef();
					itemRef.setRefValue(line);
					itemRef.setOrderItem(orderItem);
					itemRefs.add(itemRef);
				}
				
				if(line.startsWith("PAIR"))
				{	
					readedPAIR = true;
					orderItem = new OrderItem();
					orderItem.setOrderDetail(orderDetail);
					parseOrderItem(line,"PAIR");
				}
				
				line = file.readLine();
			}
			
			orderDetail.setOrderItems(orderItems);
			
			OrderDao.saveOrder(orderDetail);
			
		}catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if(file != null)
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	private void parseOrderItem(String itemText,String itemIndicator)
	{		
		if(itemIndicator.equals("LINEITEM"))
		{
			orderItem.setLineNum(itemText.substring(0, 3));
			itemText = itemText.replace(orderItem.getLineNum(), "").trim();
			orderItem.setItemNumber(itemText.substring(0,7));
			orderItem.setDateRequired(itemText.replace(orderItem.getItemNumber(), "").trim());
			
			orderItem.setItemRefs(itemRefs);
			orderItems.add(orderItem);
			itemRefs = new HashSet<OrderItemRef>();
		}
		
		if(itemIndicator.equals("PAIR"))
		{
			String[] tmp = itemText.split("\\s");			
			orderItem.setUnit(tmp[0]);
			orderItem.setOdrQty(tmp[1]);
			orderItem.setUnitCost(tmp[2]);
			orderItem.setAmount(tmp[3]);
		}
	}

}
