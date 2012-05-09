package com.diplom.basics;

public class Alarm {
	
	private String instrumentCode;
	private double expectValue;
	private int exchangeId;
	
	public Alarm(String instrumentCode, double expectValue, int exchangeId){
		this.instrumentCode=instrumentCode;
		this.expectValue=expectValue;
		this.exchangeId=exchangeId;
	}
	public String getInstrumentCode() {
		return instrumentCode;
	}
	public void setInstrumentCode(String instrumentCode) {
		this.instrumentCode = instrumentCode;
	}
	public double getExpectValue() {
		return expectValue;
	}
	public void setExpectValue(double expectValue) {
		this.expectValue = expectValue;
	}
	public int getExchangeId() {
		return exchangeId;
	}
	public void setExchangeId(int exchangeId) {
		this.exchangeId = exchangeId;
	}		
}
