package com.diplom.loaders;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.Handler;
import android.os.Message;

import com.diplom.basics.Quotation;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.net.HttpClient;

public class RTS_Loader {
	public static String serverAddress="http://www.rts.ru/export/xml/issues.aspx";	
	public static int[] EmitentIds={17350,972,973,15010,156,299,18577,19600,20514,237,17351,416,319,332,19634,17072,20915,20259,20345,20105,16810,20780,80734,292,17962,294,18348,363,364,16081,19645,74635,74632,1047,20945,159};
    public static int[] EmitentMarkets={3,3,3,3,3,3,3,3,3,3,3,10,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3};    
    public static String[] EmitentNames={"AKRN","BANE","BANEP","BRZL","CHGZ","CHMF","DIXY","DVEC","FEES","FESH","GCHE","GSPBEX","IRGZ","LKOH","LSRG","MGNT","MOTZ","MRKC","MRKK","MRKP","MSSB","MSTT","MTLRP","NKNC","NKNCP","NKSH","PHST","SBER","SBERP","SVAV","SYNG","TNBP","TNBPP","TRNFP","VRAO","VSMO"};
    public static String[] EmitentCodes={"RTS.AKRN","RTS.BANE","RTS.BANEP","RTS.BRZL","RTS.CHGZ","RTS.CHMF","RTS.DIXY","RTS.DVEC","RTS.FEES","RTS.FESH","RTS.GCHE","GSPBEX","RTS.IRGZ","RTS.LKOH","RTS.LSRG","RTS.MGNT","RTS.MOTZ","RTS.MRKC","RTS.MRKK","RTS.MRKP","RTS.MSSB","RTS.MSTT","RTS.MTLRP","RTS.NKNC","RTS.NKNCP","RTS.NKSH","RTS.PHST","RTS.SBER","RTS.SBERP","RTS.SVAV","RTS.SYNG","RTS.TNBP","RTS.TNBPP","RTS.TRNFP","RTS.VRAO","RTS.VSMO"};    
    
	public RTS_Loader() {		
	}
	//------------------------------------------------Main Functions-------------------------------------------------------------
	public void getDataForChart(final String instrumentCode, final Date start, final QuotationType bidType, final Handler handler)
	{
		Thread thread=new Thread()
		{
			@Override
			public void run() {				
				HistoryLoader loader=new HistoryLoader();
				loader.load(getMarketId(instrumentCode), getInstrumentId(instrumentCode), instrumentCode, start, bidType);
				getCurrentValueAsync(instrumentCode, handler);
			}
		};
		thread.start();
	}
	public void getCurrentValueAsync(final String instrumentCode, final Handler handler)
	{
		Thread thread=new Thread(){
			@Override
			public void run() {
				try {
					String query=serverAddress+"?code="+instrumentCode.replace("RTS.", "");
					Document d=getXmlDocument(HttpClient.SendHttpGet(query, null));
					String price=((Element)d.getElementsByTagName("issue").item(0)).getAttribute("trade_price");
					String date=((Element)d.getElementsByTagName("issue").item(0)).getAttribute("trade_moment");					
					SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dateObj=null;
					if(date.equals(""))
						dateObj=new Date(); 
					else dateObj=curFormater.parse(date);
					if(price.equals(""))
						price=((Element)d.getElementsByTagName("issue").item(0)).getAttribute("best_ask");					
					Quotation q=new Quotation(Double.parseDouble(price), dateObj, QuotationType.Hour_Bid);
					Message message = handler.obtainMessage(1, q);
	                handler.sendMessage(message);
				} catch (Exception e) {					
					e.printStackTrace();
					Message message = handler.obtainMessage(1, new Quotation(0, new Date(), QuotationType.Hour_Bid));
	                handler.sendMessage(message);
				}
			}
		};		
		thread.start();
	}
	public double getCurrentValue(String instrumentCode)
	{		
		instrumentCode=instrumentCode.replace("RTS.", "");
		String query=serverAddress+"?code="+instrumentCode;
		Document d=getXmlDocument(HttpClient.SendHttpGet(query, null));
		String price=((Element)d.getElementsByTagName("issue").item(0)).getAttribute("trade_price");
		if(price.equals(""))
			price=((Element)d.getElementsByTagName("issue").item(0)).getAttribute("best_ask");
		return Double.parseDouble(price);				
	}
	//--------------------------------------Help Functions-------------------------------------------------------
	private Document getXmlDocument(String str)
	{				
		StringReader sr = new StringReader(str);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;		
		try {
			builder = factory.newDocumentBuilder();
			return builder.parse(is);
		} catch (ParserConfigurationException e) {			
			e.printStackTrace();
		} catch (SAXException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		return null;		
	}
	public Integer getInstrumentId(String instrumentCode)
	{        
	  for(int i=0;i<EmitentIds.length;i++)
	  {
	     if(EmitentCodes[i].equals(instrumentCode))
	           return EmitentIds[i];
	  }
	  return 0;
	}
	public Integer getMarketId(String instrumentCode)
	{        
	  for(int i=0;i<EmitentCodes.length;i++)
	  {
	     if(EmitentCodes[i].equals(instrumentCode))
	           return EmitentMarkets[i];
	  }
	  return 0;
	}
}
