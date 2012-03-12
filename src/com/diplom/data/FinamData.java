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
      digitSeparators.put("íåò", 1);
      digitSeparators.put(".", 2);
      digitSeparators.put(",", 3);
      digitSeparators.put(" ", 4);
      digitSeparators.put("'", 5);
    }
    public void initPeriods()
    {    
	     periods.put("òèêè", 1);
	     periods.put("1 ìèí.", 2);
	     periods.put("5 ìèí.", 3);
	     periods.put("10 ìèí.", 4);
	     periods.put("15 ìèí.", 5);
	     periods.put("30 ìèí.", 6);
	     periods.put("1 ÷àñ.", 7);
	     periods.put("1 ÷àñ. ñ 10:30", 11);
	     periods.put("1 äåíü", 8);
	     periods.put("1 íåäåëÿ", 9);
	     periods.put("1 ìåñÿö", 10);
    }
    public void initDateFormats()
    {        
        dateFormats.put("ããããììää", 1);
        dateFormats.put("ããììää", 2);
        dateFormats.put("ääììãã", 3);
        dateFormats.put("ää/ìì/ãã", 4);
        dateFormats.put("ìì/ää/ãã", 5);
    }
    public void initTimeFormats()
    {        
        timeFormats.put("÷÷ììññ", 1);
        timeFormats.put("÷÷ìì", 2);
        timeFormats.put("÷÷:ìì:ññ", 3);
        timeFormats.put("÷÷:ìì", 4);
    }
    public void initMarketSections()//market
    {
        marketSections.put("ÌÌÂÁ Àêöèè", 1);        
        marketSections.put("ÐÒÑ", 3);
        marketSections.put("ÐÒÑ-GAZ", 10);
        marketSections.put("ÐÒÑ-GTS", 11);        
        marketSections.put("RTS Standard", 38);
        /*
        marketSections.put("RTS Board", 20);
        marketSections.put("ÌÌÂÁ ÏÈÔû", 29);
        marketSections.put("ÌÌÂÁ îáëèãàöèè", 2);
        marketSections.put("ÌÌÂÁ Âíåñïèñî÷íûå îáëèãàöèè", 12);
        marketSections.put("Ôüþ÷åðñû ÔÎÐÒÑ", 14);
        marketSections.put("ÔÎÐÒÑ Àðõèâ", 17);
        marketSections.put("ÌÌÂÁ Àðõèâ", 16);
        marketSections.put("ÐÒÑ Àðõèâ", 18);        
        marketSections.put("Âàëþòû", 5);
        marketSections.put("Ìèðîâûå Èíäåêñû", 6);
        marketSections.put("Ôüþ÷åðñû", 7);
        marketSections.put("ÀÄÐ", 8);
        marketSections.put("Ñûðüå", 24);
        marketSections.put("Ïðîäîâîëüñòâèå", 40);
        marketSections.put("ÑÏÔÁ", 9);
        marketSections.put("Àêöèè ÑØÀ", 25);
        marketSections.put("US Bonds/Notes", 26);
        marketSections.put("Îòðàñëè ýêîíîìèêè ÑØÀ", 27);
        marketSections.put("ETF", 28);
        marketSections.put("Èíäåêñû ìèðîâîé ýêîíîìèêè", 30);*/
    }
}
