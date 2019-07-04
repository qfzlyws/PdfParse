package com.dgys.app.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
	private String[] tempStrArray = null;
	
	public AmfitOrderParser(){
		orderDetail = new OrderDetail();
		orderItems = new HashSet<>();
		itemRefs = new HashSet<>();
		
		orderDetail.setRecDt(new Timestamp(System.currentTimeMillis()));
	}
	
	@Override
	public void ParseOrderText(String orderFilePath) throws Exception{
		try{
			
			file = new RandomAccessFile(orderFilePath,"rw");
			
			preProcess(file);
			
			file.seek(0);
			
			String line = new String(file.readLine().getBytes("ISO-8859-1"),"UTF-8");
			while(line != null && !line.equals("")){
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
				if(line != null)
					line = new String(line.getBytes("ISO-8859-1"),"UTF-8");
			}
			
			orderDetail.setOrderItems(orderItems);
			
			/*System.out.println(orderDetail);
			for(OrderItem item:orderDetail.getOrderItems()){
				System.out.println(item);
				
				for(OrderItemRef ref:item.getItemRefs()){
					System.out.println(ref);
				}
			}*/
			
			OrderDao.saveOrder(orderDetail);
			
		}catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if(file != null)
				file.close();
		}
	}
	
	private void parseOrderItem(String itemText,String itemIndicator)
	{		
		if(itemIndicator.equals("LINEITEM"))
		{
			tempStrArray = itemText.split("\\s");
			
			orderItem.setLineNum(tempStrArray[0]);
			orderItem.setItemNumber(tempStrArray[1]);
			
			itemText = itemText.replaceFirst(tempStrArray[0], "").trim();
			itemText = itemText.replace(tempStrArray[1], "").trim();
			
			orderItem.setDateRequired(itemText);			
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
	
	private void preProcess(RandomAccessFile file) throws IOException{
		String tempLine = null;
		StringBuffer temp = new StringBuffer();
		String[] tempArray = null;
		List<String> detail = new ArrayList<String>(); 
		String text = null;
		boolean pairFlag = false;
		
		while((text = file.readLine()) != null){
			text = new String(text.getBytes("ISO-8859-1"),"UTF-8");
			
			if(text.startsWith("Page") || text.startsWith("Continued"))
				continue;
			
			if(text.startsWith("PAIR")){ //讀到明細資料
				pairFlag = true;
				
				tempArray = text.split("\\s");
				if(tempArray.length > 4){ //PAIR行和LINENumber行在同行
					String lineNum = tempArray[3].substring(tempArray[3].length() - 3);
					String amount = tempArray[3].replace(lineNum, "");
					
					tempLine = text;
					text = "";
					
					for(int i=0;i<3;i++){
						tempLine = tempLine.replace(tempArray[i], "").trim();
						text = text + tempArray[i] + " ";
					}
					
					tempLine = tempLine.replace(amount, "");
					text = text + amount;		
				}else{
					if(tempLine != null){
						temp.append(tempLine + "\n");
						tempLine = null;
					}
				}
			}
			
			if(!detail.contains(text))
				temp.append(text + "\n");
			
			if(!pairFlag)
				detail.add(text);
			
		}
		
		file.setLength(0);
		file.write(temp.toString().getBytes("UTF-8"));
	}

}
