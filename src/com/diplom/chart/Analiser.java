package com.diplom.chart;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.diplom.basics.Quotation;
import com.diplom.basics.StochasticItem;

public class Analiser {
	private List<Quotation> quotes;		
	public Analiser(List<Quotation> quotes) {
		super();
		this.quotes = quotes;
	}
	public List<Integer> RSI(){
        List<Integer> result=new ArrayList<Integer>();
        double closeValue1=0, closeValue2;
        double U, D, Up=1, Dp=1, RS, RSI;
        double Su, Sd, Sup=1, Sdp=1;
        double a=2.0/(quotes.size()+1.0);
        for(int i=0;i<quotes.size()-1;i++){
            closeValue1=quotes.get(i).closeValue;
            closeValue2=quotes.get(i+1).closeValue;
            if(closeValue2>closeValue1){
                U=closeValue2-closeValue1;
                D=0;
            }
            else{
                U=0;
                D=closeValue1-closeValue2;
            }
            Su=a*Up+(1.0-a)*Sup;
            Up=U;
            Sup=Su;
            Sd=a*Dp+(1.0-a)*Sdp;
            Dp=D;
            Sdp=Sd;
            if(Sd>0){
                RS=Su/Sd;
                RSI=100.0-100.0/(1.0+RS);
            }
            else RSI=100.0;
            result.add((int)Math.floor(RSI));
        }
        return result;
    }    
    public List<StochasticItem> stochastic(){
    	List<StochasticItem> result=new ArrayList<StochasticItem>();
        int periodsNum=4;
        double periodHigh, periodLow, closeValue;
        double a=2.0/(quotes.size()+1.0);
        double fast, slow, slowPrev=1.0;
        for(int i=0;i<quotes.size()-1;i++){
            closeValue=quotes.get(i).closeValue;
            if(i<periodsNum){
                periodHigh=getMaxClose(quotes.subList(0, periodsNum));
                periodLow=getMinClose(quotes.subList(0, periodsNum));
            }
            else{
                periodHigh=getMaxClose(quotes.subList(i-periodsNum, i));
                periodLow=getMinClose(quotes.subList(i-periodsNum, i));
            }
            fast=Math.abs(closeValue - periodLow) / (periodHigh - periodLow) * 100.0;
            if(fast<0)
            	Log.i("stochastic","fast="+fast);
            slow=a*fast+(1.0-a)*slowPrev;
            slowPrev=slow;
            result.add(new StochasticItem((int)Math.floor(slow), (int)Math.floor(fast)%100));
        }
        return result;
    }
    public List<Integer> RoC(){//Momentum
    	List<Integer> result=new ArrayList<Integer>();
    	int periodsNum=4;
    	double nowPrice, prevPrice;
        for(int i=0;i<quotes.size()-1;i++){
            nowPrice=quotes.get(i).closeValue;
            if(i<periodsNum)
            	prevPrice=quotes.get(0).closeValue;
            else prevPrice=quotes.get(i-periodsNum).closeValue;
            int value=(int)Math.ceil((nowPrice-prevPrice)/prevPrice*100.0)%100;
            result.add(value);
        }
    	return result;
    }
    public List<Integer> MA(int chartHeight){//exponetial moving average
    	List<Integer> result=new ArrayList<Integer>();
    	int periodsNum=4;
        double a=2/(periodsNum+1);
        double ema, emaPrev=getAvgClose(quotes.subList(0, periodsNum)), periodAvg;
        for(int i=1;i<quotes.size()-1;i++){
        	if(i<periodsNum)
                periodAvg=getAvgClose(quotes.subList(0, periodsNum));
            else  periodAvg=getAvgClose(quotes.subList(i-periodsNum, i));
            ema=a*periodAvg+(1.0-a)*emaPrev;
            emaPrev=ema;
            result.add((int)Math.ceil(ema)%chartHeight);
        }
        return result;
    }
    private double getAvgClose(List<Quotation> values){
        double sum=0;
        for(Quotation q:values)
            sum+=q.closeValue;
        return sum/values.size();
    }
    private double getMinClose(List<Quotation> values){
        double min=values.get(0).closeValue, v;
        for(int i=1;i<values.size();i++)
        {
            v=values.get(i).closeValue;
            if(v<min)
                min=v;
        }
        return min;
    }
    private double getMaxClose(List<Quotation> values){
        double max=values.get(0).closeValue, v;
        for(int i=1;i<values.size();i++)
        {
            v=values.get(i).closeValue;
            if(v>max)
                max=v;
        }
        return max;
    }
}
