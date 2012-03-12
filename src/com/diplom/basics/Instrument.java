package com.diplom.basics;

public class Instrument {
	public static final int RTS=1, MICEX=2;
	
	private String code, board;
	private int exchangeId;
	private String name;
	private double value;
	
	public Instrument(int exchangeId, String board, String code, String name, double value) {
		this.name=name;
		this.code=code;
		this.board=board;
		this.exchangeId=exchangeId;
		this.value=value;
	}
	public String getBoard() {
		return board;
	}
	public int getExchangeId() {
		return exchangeId;
	}
	public double getValue(){
		return value;
	}
	public void setValue(double value){
		this.value=value;
	}
	public String getCode()	{
		return code;
	}
	public String getName() {
		return name;
	}
}
