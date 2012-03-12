package com.diplom.data;

import java.util.HashMap;

public class FinamData {
	public HashMap<String, Integer> marketSections=new HashMap<String, Integer> ();
	public HashMap<String, Integer> periods=new HashMap<String, Integer> ();
	public HashMap<String, Integer> dateFormats=new HashMap<String, Integer> ();
	public HashMap<String, Integer> timeFormats=new HashMap<String, Integer> ();
	public HashMap<String, Integer> separators=new HashMap<String, Integer> ();
	public HashMap<String, Integer> digitSeparators=new HashMap<String, Integer> ();
//	public List<String> getEmitentsNames(String market)
//    {
//        List<String> result=new ArrayList<String>();
//        int marketId=marketSections.get(market);        
//        for(int i=0;i<HistoryData.EmitentIds.length;i++)
//        {
//            if(HistoryData.EmitentMarkets[i]==marketId)
//                result.add(HistoryData.EmitentNames[i]);
//        }
//        return result;
//    }
//    public List<Integer> getEmitentsIds(String market)
//    {
//        List<Integer> result=new ArrayList<Integer>();
//        int marketId=marketSections.get(market);
//        for(int i=0;i<HistoryData.EmitentIds.length;i++)
//        {
//            if(HistoryData.EmitentMarkets[i]==marketId)
//                result.add(HistoryData.EmitentIds[i]);
//        }
//        return result;
//    }
//    public Integer getEmitentId(String emitent)
//    {        
//        for(int i=0;i<HistoryData.EmitentIds.length;i++)
//        {
//            if(HistoryData.EmitentNames[i].equals(emitent))
//                return HistoryData.EmitentIds[i];
//        }
//        return 0;
//    }
//    public String getEmitentCode(String emitent)
//    {        
//    	String[] EmitentCodes=HistoryData.getEmitentCodes(); 
//        for(int i=0;i<HistoryData.EmitentIds.length;i++)
//        {
//            if(HistoryData.EmitentNames[i].equals(emitent))
//                return EmitentCodes[i];
//        }
//        return "";
//    }
    public void initSeparators()
    {     
      separators.put(",", 1);
      separators.put(".", 2);
      separators.put(";", 3);
      separators.put("  ", 4);
      separators.put(" ", 5);
    }
    public void initDigitSeparators()
    {        
      digitSeparators.put("���", 1);
      digitSeparators.put(".", 2);
      digitSeparators.put(",", 3);
      digitSeparators.put(" ", 4);
      digitSeparators.put("'", 5);
    }
    public void initPeriods()
    {    
	     periods.put("����", 1);
	     periods.put("1 ���.", 2);
	     periods.put("5 ���.", 3);
	     periods.put("10 ���.", 4);
	     periods.put("15 ���.", 5);
	     periods.put("30 ���.", 6);
	     periods.put("1 ���.", 7);
	     periods.put("1 ���. � 10:30", 11);
	     periods.put("1 ����", 8);
	     periods.put("1 ������", 9);
	     periods.put("1 �����", 10);
    }
    public void initDateFormats()
    {        
        dateFormats.put("��������", 1);
        dateFormats.put("������", 2);
        dateFormats.put("������", 3);
        dateFormats.put("��/��/��", 4);
        dateFormats.put("��/��/��", 5);
    }
    public void initTimeFormats()
    {        
        timeFormats.put("������", 1);
        timeFormats.put("����", 2);
        timeFormats.put("��:��:��", 3);
        timeFormats.put("��:��", 4);
    }
    public void initMarketSections()//market
    {
        marketSections.put("���� �����", 1);        
        marketSections.put("���", 3);
        marketSections.put("���-GAZ", 10);
        marketSections.put("���-GTS", 11);        
        marketSections.put("RTS Standard", 38);
        /*
        marketSections.put("RTS Board", 20);
        marketSections.put("���� ����", 29);
        marketSections.put("���� ���������", 2);
        marketSections.put("���� ������������ ���������", 12);
        marketSections.put("�������� �����", 14);
        marketSections.put("����� �����", 17);
        marketSections.put("���� �����", 16);
        marketSections.put("��� �����", 18);        
        marketSections.put("������", 5);
        marketSections.put("������� �������", 6);
        marketSections.put("��������", 7);
        marketSections.put("���", 8);
        marketSections.put("�����", 24);
        marketSections.put("��������������", 40);
        marketSections.put("����", 9);
        marketSections.put("����� ���", 25);
        marketSections.put("US Bonds/Notes", 26);
        marketSections.put("������� ��������� ���", 27);
        marketSections.put("ETF", 28);
        marketSections.put("������� ������� ���������", 30);*/
    }
}
