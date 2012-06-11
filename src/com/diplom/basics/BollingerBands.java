package com.diplom.basics;

public class BollingerBands {
	private double ml, tl, bl;

	public BollingerBands(double ml, double tl, double bl) {
		super();
		this.ml = ml;
		this.tl = tl;
		this.bl = bl;
	}

	public double getMl() {
		return ml;
	}

	public void setMl(double ml) {
		this.ml = ml;
	}

	public double getTl() {
		return tl;
	}

	public void setTl(double tl) {
		this.tl = tl;
	}

	public double getBl() {
		return bl;
	}

	public void setBl(double bl) {
		this.bl = bl;
	}	
}
