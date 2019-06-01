package com.dgys.app.model;

import java.sql.Timestamp;
import java.util.Set;

public class OrderDetail {
	private int orderId;
	private String factNo;
	private String poNumber;
	private String orderDate;
	private String resalePermit;
	private String shipVia;
	private String terms;
	private Timestamp recDt;
	private Set<OrderItem> orderItems;
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getResalePermit() {
		return resalePermit;
	}
	public void setResalePermit(String resalePermit) {
		this.resalePermit = resalePermit;
	}
	public String getShipVia() {
		return shipVia;
	}
	public void setShipVia(String shipVia) {
		this.shipVia = shipVia;
	}
	public String getTerms() {
		return terms;
	}
	public void setTerms(String terms) {
		this.terms = terms;
	}
	public Set<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(Set<OrderItem> items) {
		this.orderItems = items;
	}
	public String getFactNo() {
		return factNo;
	}
	public void setFactNo(String factNo) {
		this.factNo = factNo;
	}
	public Timestamp getRecDt() {
		return recDt;
	}
	public void setRecDt(Timestamp recDt) {
		this.recDt = recDt;
	}
	
}
