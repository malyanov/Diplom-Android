package com.diplom.basics;

import java.io.Serializable;

public class Instrument implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int MICEX=0, RTS=1;
	
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public int getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(int exchangeId) {
		this.exchangeId = exchangeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}	
}
