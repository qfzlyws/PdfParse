package com.dgys.app.model;

import java.util.Set;

public class OrderItem {
	private int orderItemId;
	private OrderDetail orderDetail;
	private String lineNum;
	private String itemNumber;
	private String dateRequired;
	private String unit;
	private String odrQty;
	private String unitCost;
	private String amount;
	private Set<OrderItemRef> itemRefs;
	
	public int getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(int orderItemId) {
		this.orderItemId = orderItemId;
	}
	
	public OrderDetail getOrderDetail() {
		return orderDetail;
	}
	public void setOrderDetail(OrderDetail orderDetail) {
		this.orderDetail = orderDetail;
	}
	public String getLineNum() {
		return lineNum;
	}
	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	public String getDateRequired() {
		return dateRequired;
	}
	public void setDateRequired(String dateRequired) {
		this.dateRequired = dateRequired;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getOdrQty() {
		return odrQty;
	}
	public void setOdrQty(String odrQty) {
		this.odrQty = odrQty;
	}
	public String getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(String unitCost) {
		this.unitCost = unitCost;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public Set<OrderItemRef> getItemRefs() {
		return itemRefs;
	}
	public void setItemRefs(Set<OrderItemRef> itemRefs) {
		this.itemRefs = itemRefs;
	}
	
	
}
