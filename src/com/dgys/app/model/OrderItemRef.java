package com.dgys.app.model;

public class OrderItemRef {
	private int itemRefId;
	private OrderItem orderItem;
	private String refValue;
	public int getItemRefId() {
		return itemRefId;
	}
	public void setItemRefId(int itemRefId) {
		this.itemRefId = itemRefId;
	}
	
	public OrderItem getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}
	public String getRefValue() {
		return refValue;
	}
	public void setRefValue(String refValue) {
		this.refValue = refValue;
	}
	
}
