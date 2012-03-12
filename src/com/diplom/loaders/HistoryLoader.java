/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diplom.loaders;

import java.util.Date;

import com.diplom.basics.Quotation.QuotationType;
import com.diplom.net.HttpClient;

/**
 *
 * @author Malyanov Dmitry
 */
public class HistoryLoader {
	
	private static final int HOUR_BID_ID=7, DAY_BID_ID=8;
	
    private String serverAddress="http://195.128.78.52/";    
    
    public HistoryLoader() {                
    }
    private String genQuery(int marketId, int emitentId, String emitentCode, Date startDate, Date finishDate, int period)
    {
        String result="";
        String filename="";
        result+="?d=d";
        result+="&market="+marketId;        
        result+="&em="+emitentId;                
        result+="&df="+startDate.getDate();
        result+="&mf="+startDate.getMonth();
        result+="&yf="+(startDate.getYear()+1900);
        result+="&dt="+finishDate.getDate();
        result+="&mt="+finishDate.getMonth();
        result+="&yt="+(finishDate.getYear()+1900);
        
        result+="&p="+period;
        filename=getFileName(emitentCode, startDate, finishDate);
        result+="&f="+filename;
        result+="&e=.txt";
        result+="&cn="+emitentCode;
        result+="&dtf=1";
        result+="&tmf=1";
        result+="&MSOR=0";
        result+="&sep=1";
        result+="&sep2=1";
        result+="&datf=2";
        result+="&at=1";
        result+="&fps=1";
        result=serverAddress+filename+".txt"+result;
        return result;        
    }
    private String getFileName(String emitentCode, Date start, Date finish)
    {
        String result="";
        result+=emitentCode;
        
        result+=("_"+start.getYear()%100);
        if(start.getMonth()+1<10)
            result+=("0"+(start.getMonth()+1));
        else result+=(start.getMonth()+1);
        if(start.getDate()<10)
            result+=("0"+start.getDate());
        else result+=start.getDate();
        
        result+=("_"+finish.getYear()%100);        
        if(finish.getMonth()+1<10)
            result+=("0"+(finish.getMonth()+1));
        else result+=(finish.getMonth()+1);
        if(finish.getDate()<10)
            result+=("0"+finish.getDate());
        else result+=finish.getDate();
        return result;
    }
    public void load(int marketId, int emitentId, String emitentCode, Date start, QuotationType bidType)
    {
    	int period=HOUR_BID_ID;
    	if(bidType==QuotationType.Day_Bid)
    		period=DAY_BID_ID;
        HttpClient.DownloadFromUrl(genQuery(marketId, emitentId, emitentCode, start, new Date(), period), "data.txt");    	
    }        
}
