package com.diplom.loaders;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.Handler;
import android.os.Message;

import com.diplom.basics.Instrument;
import com.diplom.basics.Quotation;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.net.HttpClient;
import com.diplom.settings.Settings;

public class MICEX_Loader {
	public static int[] EmitentIds={6,74744,74745,17137,20737,66644,74584,81040,39,40,22843,17564,19915,16452,20702,29,22797,15914,35242,35243,20706,20719,19632,17257,15965,17068,17067,16456,16457,17474,20958,16842,35220,20708,18564,19724,16825,17919,17920,556,35332,15547,9,15544,22525,20891,20709,20030,18310,16695,20890,20710,510,15518,20912,20913,19916,522,35285,19736,8,22094,21004,542,31,19737,16782,16917,20309,20412,20402,20107,20235,20286,20346,80621,74562,74563,21018,80745,16359,16804,16933,21005,74549,20100,20101,80728,16615,16616,15843,15844,20711,22891,18654,20895,16909,20894,80818,22806,74779,16610,35248,17273,7,15,16783,16784,74718,66893,20712,35238,20892,16080,21007,22788,74540,3,23,17921,17922,16136,21166,19651,15723,15722,20087,20088,4,18382,18176,17597,18189,17282,17618,18441,74628,74629,16265,825,826,21002,16797,19897,74561,1012,16805,16517,19623,19717,20972,19095,21000,21001,20999,19960,16712,16713,20125,20321,15522,80316};
	public static short EmitentMarket=1;
	public static String[] EmitentNames={"+МосЭнерго","iДонскЗР","iДонскЗР п","iИСКЧ ао","iО2ТВ-ао","iРНТ","iФармсинтз","iЮтинет.Ру","АВТОВАЗ ао","АВТОВАЗ ап","АвиастК ао","Акрон","Арсагера","АстрЭнСб","АшинскийМЗ","Аэрофлот","БСП ап","БанкМосквы","БашИнСв ао","БашИнСв ап","БашЭнрг ао","ВМЗ ао","ВТГК","ВХЗ-ао","Верхнесалд","Возрожд-ао","Возрожд-п","ВолгЭнСб","ВолгЭнСб-п","ВологСб","ВостРАО ао","ГАЗПРОМ ао","ГПНХСал ао","ДВМП ао","ДИКСИ Гр.","ДЭК ао","ДагСб ао","Дорогбж ао","Дорогбж ап","ЗМЗ-ао","ЗолЯкутии","ИРКУТ-3","ИркЭнерго","КАМАЗ","КМЗ","КНОС-ао","КазанскВЗ","КамчатЭ ао","Квадра","КировЭС ап","КолЭС ао","КоршГОК ао","КрасОкт-ао","КраснГЭС","Красэсб ао","Красэсб ап","КубаньЭнСб","Кубанэнр","КузбТК ао","ЛСР ао","ЛУКОЙЛ","Лензол. ап","Лензолото","Ленэнерг-п","Ленэнерго","М.видео","ММК","МОЭСК","МРСК СЗ","МРСК СК","МРСК Ур","МРСК ЦП","МРСК Центр","МРСКВол","МРСКСиб","МФБ ао","МагадЭн ао","МагадЭн ап","Мечел ао","Мечел ап","МордЭнСб","МосТСК ао","МосЭС ао","МоскНПЗ ао","Мостотрест","НКНХ ао","НКНХ ап","НОМОС-Б ао","НижгорСб","НижгорСб-п","ОМЗ-ао","ОМЗ-ап","ОПИН ао","Омскшина","ПИК ао","ПермМот ао","ПермьЭнС-п","ПетербСК","Приморье","ПроектИ ао","РБК ао","Разгуляй","РегБР ао","Роснефть","Ростел -ао","Ростел -ап","РостовЭС","РостовЭС-п","Русал рдр","Русгрэйн","Русполимет","РуссМоре","СМЗ-ао","СОЛЛЕРС","СУМЗ ао","СУЭК-Красн","СЭМЗ ао","Сбербанк","Сбербанк-п","СвердЭС ао","СвердЭС ап","СевСт-ао","Селестра","Синерг. ао","Слав-ЯНОСп","Славн-ЯНОС","СтаврЭнСб","СтаврЭнСбп","Сургнфгз","ТГК-1","ТГК-14","ТГК-2","ТГК-2 ап","ТГК-5","ТГК-6","ТМК ао","ТНК-ВР ао","ТНК-ВР ап","ТамбЭнСб","Татнфт 3ао","Татнфт 3ап","Телеграф","ТомскРП ао","ТрКред ао","ТрансК ао","Транснф ап","УАЗ ао","УдмуртЭС","Уркалий-ао","Фармстанд","ХолМРСК ап","ЦМТ ао","ЧКПЗ ао","ЧМК ао","ЧТПЗ ао","ЧЦЗ ао","ЧелябЭС ао","ЧелябЭС ап","ЧеркизГ-ао","ЭнергияРКК","ЮТэйр ао","ЯрШинЗ ао"};
	public static String[] EmitentCodes={"MSNG","DZRD","DZRDP","ISKJ","ODVA","RNAV","LIFE","UTII","AVAZ","AVAZP","UNAC","AKRN","ARSA","ASSB","AMEZ","AFLT","BSPBP","MMBM","BISV","BISVP","BEGY","VSMZ","VTGK","VLHZ","VSMO","VZRZ","VZRZP","VGSB","VGSBP","VOSB","VRAO","GAZP","SNOS","FESH","DIXY","DVEC","DASB","DGBZ","DGBZP","ZMZN","ZOYA","IRKT","IRGZ","KMAZ","KMEZ","KUOS","KHEL","KCHE","TGKD","KISBP","KOSB","KOGK","KROT","KRSG","KRSB","KRSBP","KBSB","KUBE","KBTK","LSRG","LKOH","LNZLP","LNZL","LSNGP","LSNG","MVID","MAGN","MSRS","MRKZ","MRKK","MRKU","MRKP","MRKC","MRKV","MRKS","MFBA","MAGE","MAGEP","MTLR","MTLRP","MRSB","MSSV","MSSB","MNPZ","MSTT","NKNC","NKNCP","NMOS","NNSB","NNSBP","OMZZ","OMZZP","OPIN","OMSH","PIKK","PMOT","PMSBP","PBSB","PRMB","PRIN","RBCM","GRAZ","REBR","ROSN","RTKM","RTKMP","RTSB","RTSBP","RUALR","RUGR","RUSP","RSEA","MGNZ","SVAV","SUMZ","SKRN","SEMZ","SBER","SBERP","SVSB","SVSBP","CHMF","SELL","SYNG","JNOSP","JNOS","STSB","STSBP","SNGS","TGKA","TGKN","TGKB","TGKBP","TGKE","TGKF","TRMK","TNBP","TNBPP","TASB","TATN","TATNP","CNTL","TORS","TCBN","TRCN","TRNFP","UAZA","UDSB","URKA","PHST","MRKHP","WTCM","CHKZ","CHMK","CHEP","CHZN","CLSB","CLSBP","GCHE","RKKE","UTAR","YASH"};
	
	public MICEX_Loader() {		
	}
	//------------------------------------------------Main Functions-------------------------------------------------------------
	public void getDataForChart(final String instrumentCode, final Date start, final QuotationType bidType, final Handler handler){
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
	public void getCurrentValueAsync(final String board, final String instrumentCode, final QuotationType bidType, final Handler handler){
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
	public double getCurrentValue(String board, String instrumentCode){		
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
	public Instrument getInstrumentByCode(final String code){
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
	public Integer getInstrumentId(String instrumentCode){        
	  for(int i=0;i<EmitentIds.length;i++)
	  {
	     if(EmitentCodes[i].equals(instrumentCode))
	           return EmitentIds[i];
	  }
	  return 0;
	}
	private Document getXmlDocument(String str){				
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
