/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.diplom.basics;

import java.util.Date;

/**
 *
 * @author Malyanov Dmitry
 */
public class Quotation
{   
	public enum QuotationType {Hour_Bid(0),Day_Bid(1);
		public int id;
		QuotationType(int id){
			this.id=id;
		}
		public static QuotationType getById(int id)
		{
			for (QuotationType type : values()) {
				if(type.id==id)
					return type;
			}
			return null;
		}
	};
    public double openValue,closeValue,highValue,lowValue,currentValue=0;
    public Date dateTime;
    public QuotationType type;
    boolean isCurrent;

    public Quotation(double open, double close, double low, double high, Date qdate, QuotationType qtype)
    {
        openValue=open;
        closeValue=close;
        lowValue=low;
        highValue=high;
        dateTime=qdate;
        type=qtype;
        isCurrent=false;
    }
    public Quotation(double value, Date qdate, QuotationType qtype)
    {
        openValue=closeValue=lowValue=highValue=value;
        dateTime=qdate;
        type=qtype;
        isCurrent=true;
    }
    public void changeCurrentValue(double value)
    {
        if(isCurrent)
        {
            currentValue=value;
            if(value>highValue)highValue=value;
            if(value<lowValue)lowValue=value;
        }
    }
    public void Close()
    {
        isCurrent=false;
    }

}
