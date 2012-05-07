package com.diplom.loaders;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.Handler;
import android.os.Message;

import com.diplom.basics.Instrument;
import com.diplom.basics.Quotation;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.net.HttpClient;
import com.diplom.settings.Settings;

public class MICEX_Loader {
	public static int[] EmitentIds={6,16049,19676,74744,74745,35363,17137,21167,20737,66644,74584,81040,39,40,74726,467,22843,20703,17564,35350,35351,13855,80863,19915,16452,20702,29,20066,22797,17375,17376,15914,35242,35243,20706,21078,22602,15545,17942,20719,19043,19632,17257,16352,15965,17068,17067,16456,16457,17474,20958,20959,16842,795,35220,509,2,20708,35334,18564,19724,16825,16140,16141,74461,17919,17920,22454,80915,556,603,35332,15547,21003,81036,20516,9,15544,17359,22525,20891,16284,16285,20709,15717,16329,80313,20030,20498,18310,18391,16694,16695,75094,20890,20710,511,510,15518,20912,20913,66692,19916,522,35285,20204,19736,8,22094,21004,542,31,19737,15345,12983,12984,20057,20947,22710,16782,80390,16917,20309,20412,20402,20107,20235,20286,20346,20681,80850,15523,80621,74562,74563,17086,30,51,22652,21018,80745,16359,16804,16933,21005,74549,21116,20100,20101,17046,19629,80728,585,16615,16616,17370,66694,18500,19738,18684,17204,18584,16440,19739,15843,15844,21006,20711,22555,22891,22736,16369,18654,17850,35247,20895,16909,16908,20894,18236,17123,80818,22806,20881,74779,74446,20637,19599,16610,17713,35248,66693,16866,17273,7,15,80867,80868,80869,80870,80871,80872,80873,80874,80875,80876,80877,80878,80879,80880,80881,80882,16783,16784,80941,20266,74718,66893,20712,35238,16455,22401,20892,16080,20899,21007,22788,74540,3,23,22711,17921,17922,16136,21166,20713,20898,19651,17698,19715,15723,15722,20087,20088,21105,4,13,20715,16921,74344,18382,19814,19968,18176,17597,18189,17282,17618,17502,20716,74746,18441,74628,74629,80593,74728,19012,16265,16266,825,826,18371,21002,16797,16798,19897,74561,1012,81003,81004,20718,16805,75124,80307,16517,16518,81054,81055,19623,16173,20509,19717,22603,18425,20971,20972,19095,19096,15724,21000,21001,20999,19960,16712,16713,20125,16453,16454,20321,66848,15522,20717,20523,15903,80316,15736};
	public static short EmitentMarket=1;
	public static String[] EmitentNames={"+���������","7���������","i������","i�������","i������� �","i���������","i���� ��","i���� ��","i�2��-��","i���","i���������","i������.��","������� ��","������� ��","�����-���","�������-��","������� ��","���� ��","�����","������� ��","������� ��","������36�6","������002D","��������","��������","����������","��������","��� ��","��� ��","������� ��","������� ��","����������","������� ��","������� ��","������� ��","����� ��","������� ��","��� �� ��","��������","��� ��","��� ��","����","���-��","��� 01 ��","����������","�������-��","�������-�","��������","��������-�","�������","������� ��","������� ��","������� ��","���������","������� ��","���","����������","���� ��","��� ��","����� ��.","��� ��","����� ��","���������","����������","������� ��","������� ��","������� ��","��������","������ ��","���-��","���-��","���������","�����-3","���� ��","��������2D","����������","���������","�����","���� ��","���","����-��","��� ��","��� ��","���������","������-��","���������","������� ��","������� ��","������� ��","������","������-�","������� ��","������� ��","���� ��","����� ��","������� ��","�������-1�","�������-��","��������","������� ��","������� ��","������� ��","����������","��������","������ ��","����������","��� ��","������","������. ��","���������","��������-�","���������","�.�����","����","����-4��","����-5��","��� ����-�","��������","������ ��","���","��-���� ��","�����","���� ��","���� ��","���� ��","���� ��","���� �����","�������","�������","������� ��","���-002D","���-��","��� ��","������� ��","������� ��","������ ��","������-��","������-��","��������","����� ��","����� ��","��������","������ ��","����� ��","������� ��","����������","������� ��","���� ��","���� ��","���� ��","���� ��","�����-� ��","������� ��","��������","��������-�","������� ��","������ ��","����������","���-1 ao","���-2 ��","���-3 ��","���-4 ��","���-5 ��","���-6 ��","���-��","���-��","���� ��","���� ��","������ ��","��������","������ ��","���� ��","��� ��","��� ��","������ ��","������� ��","��������-�","���������","��������","����������","����������","��������","������� ��","�������-��","��� ��","���������","����������","��� ��","��������","����������","����� ��","������ ��","������� ��","��������","������ -��","������ -��","������002D","������003D","������004D","������005D","������006D","������007D","������008D","������009D","������010D","������011D","������012D","������013D","������014D","������015D","������016D","������017D","��������","��������-�","�������39D","��������","����� ���","��������","����������","��������","�������","���������","���-��","�������","���-��","���� ��","����-�����","���� ��","��������","��������-�","������ ��","������� ��","������� ��","�����-��","��������","���������","�����-��","������. ��","��������","������� ��","����-�����","�����-����","���������","����������","������� ��","��������","��������-�","������ ��","���� ��","����������","���-1","���-11","���-13","���-14","���-2","���-2 ��","���-5","���-6","���-9","��� ��","���� ��","��� ��","���-�� ��","���-�� ��","����������","���-1 ��","���������.","��������","��������-�","������ 3��","������ 3��","������. ��","��������","������� ��","������� ��","������ ��","������ ��","������� ��","������� ��","������� ��","�-��� ��","��� ��","����� ��","����������","��������","��������-�","������� 4D","������� 5D","�������-��","��� 01","��� ��� ��","���������","������� ��","������","������� ��","������� ��","��� ��","��� ��","���-2","���� ��","��� ��","���� ��","��� ��","������� ��","������� ��","�������-��","������� ��","������� ��","����������","����������","����� ��","������. ��","��������-�","����������","������ ��","���������"};
	public static String[] EmitentCodes={"MSNG","SCOH","ARMD","DZRD","DZRDP","DIOD","ISKJ","NEKK","ODVA","RNAV","LIFE","UTII","AVAZ","AVAZP","AGRE","RU14AVAN8010","UNAC","AZKM","AKRN","APDS","APDSP","RU14APTK1007","ARMD-002D","ARSA","ASSB","AMEZ","AFLT","BSPB","BSPBP","PKBA","PKBAP","MMBM","BISV","BISVP","BEGY","BELO","BACT","WBDF","VFRM","VSMZ","VTBR","VTGK","VLHZ","VDSB","VSMO","VZRZ","VZRZP","VGSB","VGSBP","VOSB","VRAO","RAOVP","GAZP","GMKN","SNOS","RU0008913751","SIBN","FESH","DIKO","DIXY","DVEC","DASB","DLVB","DLVBP","DNKOP","DGBZ","DGBZP","ERMK","ZIRE","ZMZN","ZMZNP","ZOYA","IRKT","IKAR","IUES-002D","IUES","IRGZ","KMAZ","KZMS","KMEZ","KUOS","KTSB","KTSBP","KHEL","KLNA","KLSB","KDSK","KCHE","KCHEP","TGKD","TGKDP","KISB","KISBP","KSGR","KOSB","KOGK","RU0008913868","KROT","KRSG","KRSB","KRSBP","KUSTP","KBSB","KUBE","KBTK","KZBN","LSRG","LKOH","LNZLP","LNZL","LSNGP","LSNG","MVID","RU14MBSP7002","RU14MGTS2012","RU14MGTS5007","MDMBP","MERF","MIKR","MAGN","MNFD","MSRS","MRKZ","MRKK","MRKU","MRKP","MRKC","MRKV","MRKS","MRKA","MTSI-002D","MTSI","MFBA","MAGE","MAGEP","MGNT","RU0009011126","RU0009011134","IRSG","MTLR","MTLRP","MRSB","MSSV","MSSB","MNPZ","MSTT","MOTZ","NKNC","NKNCP","NLMK","NMTP","NMOS","RU14NZGZ2006","NNSB","NNSBP","NOTK","SXPNP","NTRI","OGK1","OGK2","OGKC","OGK4","OGKE","OGK6","OMZZ","OMZZP","OSMP","OPIN","OSFD","OMSH","OPTI","AKHA","PIKK","PRIM","PRTK","PMOT","PMSBP","PMSB","PBSB","PMTL","PLZL","PRMB","PRIN","NPE1","RBCM","RVST","ROST","RTMC","GRAZ","RASP","REBR","RODNP","ROSB","ROSN","RTKM","RTKMP","RTKM-002D","RTKM-003D","RTKM-004D","RTKM-005D","RTKM-006D","RTKM-007D","RTKM-008D","RTKM-009D","RTKM-010D","RTKM-011D","RTKM-012D","RTKM-013D","RTKM-014D","RTKM-015D","RTKM-016D","RTKM-017D","RTSB","RTSBP","HYDR-039D","HYDR","RUALR","RUGR","RUSP","RSEA","RZSB","SZPR","MGNZ","SVAV","SVTZ","SUMZ","SKRN","SEMZ","SBER","SBERP","SOVT","SVSB","SVSBP","CHMF","SELL","SILM","SNTZ","SYNG","HALS","AFKC","JNOSP","JNOS","STSB","STSBP","YKST","SNGS","SNGSP","TAMZ","TGMK","TATB","TGKA","TGKK","TGK13","TGKN","TGKB","TGKBP","TGKE","TGKF","TGKI","TUZA","TUCH","TRMK","TNBP","TNBPP","TAER","TSKA","TAVR","TASB","TASBP","TATN","TATNP","TTLK","CNTL","TORS","TORSP","TCBN","TRCN","TRNFP","TUMA","TUMAP","UUAZ","UAZA","URFD","USYN","UDSB","UDSBP","URKA-004D","URKA-005D","URKA","FLKO","FEES","PHST","FSRV","TGKJ","MRKH","MRKHP","WTCM","WTCMP","TZUM","CHKZ","CHMK","CHEP","CHZN","CLSB","CLSBP","GCHE","NGSB","NGSBP","RKKE","ERAV","UTAR","UKUZ","YKENP","YKEN","YASH","YRSL"};
	
	public MICEX_Loader() {		
	}
	//------------------------------------------------Main Functions-------------------------------------------------------------
	public void getDataForChart(final String instrumentCode, final Date start, final QuotationType bidType, final Handler handler)
	{
		Thread thread=new Thread()
		{
			@Override
			public void run() {
				Instrument instrument=getInstrumentByCode(instrumentCode);
				HistoryLoader loader=new HistoryLoader();
				Settings.boardCode=instrument.getBoard();
				loader.load(EmitentMarket, getInstrumentId(instrumentCode), instrumentCode, start, bidType);
				getCurrentValueAsync(instrument.getBoard(), instrumentCode, bidType, handler);
			}
		};
		thread.start();
	}
	public void getCurrentValueAsync(final String board, final String instrumentCode, final QuotationType bidType, final Handler handler)
	{
		Thread thread=new Thread(){
			@Override
			public void run() {
				String query="http://www.micex.ru/iss/engines/stock/markets/shares/boards/"+board+"/securities/"+instrumentCode+".xml?"+
						"iss.meta=off&iss.only=marketdata&marketdata.columns=LAST,TIME";//,SECID,HIGH,LOW,OPEN,LASTCHANGE,CLOSEPRICE,SYSTIME,SEQNUM";
				Document d=getXmlDocument(HttpClient.SendHttpGet(query, null));
				String price=((Element)d.getElementsByTagName("row").item(0)).getAttribute("LAST");
				String date=((Element)d.getElementsByTagName("row").item(0)).getAttribute("TIME");
				SimpleDateFormat curFormater = new SimpleDateFormat("HH:mm:ss");
				Date dateObj=null;
				try {
					dateObj = curFormater.parse(date);
				} catch (ParseException e) {					
					e.printStackTrace();
				}
				Quotation q=new Quotation(Double.parseDouble(price), dateObj, bidType);
				Message message = handler.obtainMessage(1, q);
                handler.sendMessage(message);
			}
		};		
		thread.start();
	}
	public double getCurrentValue(String board, String instrumentCode)
	{		
		if(board.equals(""))
		{
			Instrument instr=getInstrumentByCode(instrumentCode);
			board=instr.getBoard();
		}
		String query="http://www.micex.ru/iss/engines/stock/markets/shares/boards/"+board+"/securities/"+instrumentCode+".xml?"+
				"iss.meta=off&iss.only=marketdata&marketdata.columns=LAST";//TIME,SECID,HIGH,LOW,OPEN,LASTCHANGE,CLOSEPRICE,SYSTIME,SEQNUM";
		Document d=getXmlDocument(HttpClient.SendHttpGet(query, null));
		String price=((Element)d.getElementsByTagName("row").item(0)).getAttribute("LAST");				
		return Double.parseDouble(price);				
	}
	//--------------------------------------Help Functions-------------------------------------------------------
	public Instrument getInstrumentByCode(final String code)
	{
		XPathExpression expr=null;
		Instrument instrument=null;							
	    Document doc=getXmlDocument(HttpClient.SendHttpGet("http://www.micex.ru/iss/securities/"+code+".xml?iss.meta=off&iss.only=boards&boards.columns=secid,boardid,title", null));			
		try {						
		    	XPath xpath = XPathFactory.newInstance().newXPath();
		    	expr = xpath.compile("//row");
		    	NodeList nodes=(NodeList)expr.evaluate(doc, XPathConstants.NODESET);				
		    	Element el=(Element)nodes.item(0);														
				instrument=new Instrument(Instrument.MICEX, el.getAttribute("boardid"), el.getAttribute("secid"), el.getAttribute("title"), 0);
		} catch (XPathExpressionException e) {						
			e.printStackTrace();
		}
		return instrument;
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
	private Document getXmlDocument(String str)
	{				
		StringReader sr = new StringReader(str);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;		
		try {
			builder = factory.newDocumentBuilder();
			return builder.parse(is);
		} catch (Exception ex) {			
			ex.printStackTrace();
		}
		return null;		
	}	
}
