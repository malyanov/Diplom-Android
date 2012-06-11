package com.diplom.settings;

import android.app.Activity;
import android.content.SharedPreferences;

import com.diplom.basics.Instrument;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.chart.AnalyseChart;
import com.diplom.chart.Chart;

public class Settings {	 
	private static String PREFERENCES_NAME="informer_prefs";
	
	public static Chart.Modes chartMode=Chart.Modes.CURVES;
	public static QuotationType bidType=QuotationType.Hour_Bid;
	public static AnalyseChart.Mode analyseMode=AnalyseChart.Mode.RSI;
	public static int exchangeId=Instrument.MICEX;
	public static String instrumentCode="GAZP";
	public static String boardCode="";
	public static boolean bollingerBands=false, ma=false;
	private static Activity activity;
	
	public static boolean load(final Activity act){    
		activity=act;
	    SharedPreferences settings = activity.getSharedPreferences(PREFERENCES_NAME, 0);		
		if(settings.contains("chart_type")){
			chartMode=Chart.Modes.getById(settings.getInt("chart_type", 0));
			bidType=QuotationType.getById(settings.getInt("bid_type", 0));
			exchangeId=settings.getInt("exchange_id", 1);
			instrumentCode=settings.getString("instrument_code", "GAZP");
			boardCode=settings.getString("board_code", "");
			bollingerBands=settings.getBoolean("bollinger_bands", false);
			ma=settings.getBoolean("ma", false);
			return true;			
		}
		return false;
	}
	public static void setChartType(Chart.Modes type){
		chartMode=type;		
	}
	public static void setBidType(QuotationType type){
		bidType=type;		
	}
	public static void clear(final Activity act){
		activity=act;
		SharedPreferences settings = activity.getSharedPreferences(PREFERENCES_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.remove("chart_type");
	    editor.remove("bid_type");
	    editor.remove("exchange_id");
	    editor.remove("instrument_code");
	    editor.remove("board_code");
	    editor.remove("bollinger_bands");
	    editor.remove("ma");
	    editor.commit();		
	}
	public static void save(){
		SharedPreferences settings = activity.getSharedPreferences(PREFERENCES_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt("chart_type", chartMode.id);
	    editor.putInt("bid_type", bidType.id);
	    editor.putInt("exchange_id", exchangeId);
	    editor.putString("instrument_code", instrumentCode);
	    editor.putString("board_code", boardCode);
	    editor.putBoolean("bollinger_bands", bollingerBands);
	    editor.putBoolean("ma", ma);
	    editor.commit();		
	}
}
