package com.diplom.basics;

public class Warning {
	private String instrumentName;
	private double expectValue;
	private int exchangeId, id;
	public Warning(int id, String instrumentName, double expectValue, int exchangeId){
		this.instrumentName=instrumentName;
		this.expectValue=expectValue;
		this.exchangeId=exchangeId;
	}
	public int getId() {
		return id;
	}
	public int getExchangeId() {
		return exchangeId;
	}
	public String getInstrumentName() {
		return instrumentName;
	}
	public double getExpectValue() {
		return expectValue;
	}
}
