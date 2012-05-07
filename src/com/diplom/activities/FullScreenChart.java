package com.diplom.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.diplom.basics.Instrument;
import com.diplom.basics.Quotation;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.chart.AnalyseChart;
import com.diplom.chart.Chart;
import com.diplom.loaders.FileParser;
import com.diplom.loaders.MICEX_Loader;
import com.diplom.loaders.RTS_Loader;
import com.diplom.settings.Settings;

public class FullScreenChart extends Activity {
	private static Chart chart;
	private static List<Quotation> quots;
	private MICEX_Loader micex;
    private RTS_Loader rts;
    private static FileParser parser;
    private static AnalyseChart analizeGraph;
    private static Instrument curInstrument=null;
    
    private ProgressDialog progressDlg;
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	LinearLayout ll=(LinearLayout)findViewById(R.id.FullScreenContainer);
		ll.removeView(chart);
		ll.removeView(analizeGraph);    	
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		if(chart==null)
		{
			progressDlg=ProgressDialog.show(this, "", "Загрузка данных. Подождите пожалуйста...", true);
			parser=new FileParser();
			analizeGraph=new AnalyseChart(this.getApplicationContext());
			chart=new Chart(this.getApplicationContext(), analizeGraph, true);		
		}
		setContentView(R.layout.full_screen_chart);
		LinearLayout ll=(LinearLayout)findViewById(R.id.FullScreenContainer);
		ll.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT, 220));
		ll.addView(analizeGraph, new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		loadChartData();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.layout.graph_mode_menu, menu);	     
	     return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().equals("Кривая"))
			chart.changeViewMode(Chart.Modes.CURVES);
		else if(item.getTitle().equals("Свечи"))
			chart.changeViewMode(Chart.Modes.CANDLES);
		else if(item.getTitle().equals("Бары"))
			chart.changeViewMode(Chart.Modes.BARS);
		return true;
	}
	private Handler updLastQuotHandler=new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		double value=((Instrument)msg.obj).getValue();
    		chart.updateLastValue(value);    		
    	}
    };
	public void loadChartData()
    {
    	int monthsNum=1;
    	if(Settings.bidType==QuotationType.Day_Bid)
        	monthsNum=3;        
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH, -monthsNum);
        Date start=calendar.getTime();
        Handler dataHandler=new Handler(){
        	@Override
        	public void handleMessage(Message msg) {        		        	
        		quots=parser.readFile();
        		quots.add((Quotation)msg.obj);
        		chart.init(quots);
        		if(curInstrument!=null)
        			MainActivity.querer.removeTask(curInstrument);
        		curInstrument=new Instrument(Settings.exchangeId, Settings.boardCode, Settings.instrumentCode, "", 0);
        		MainActivity.querer.addTask(curInstrument, updLastQuotHandler);
        		if(progressDlg!=null)
        			progressDlg.hide();
        	}
        };        
        if(Settings.exchangeId==Instrument.RTS)
        {
        	rts=new RTS_Loader();
        	rts.getDataForChart(Settings.instrumentCode, start, Settings.bidType, dataHandler);        	
        }
        else if(Settings.exchangeId==Instrument.MICEX)
        {
        	micex=new MICEX_Loader();
        	micex.getDataForChart(Settings.instrumentCode, start, Settings.bidType, dataHandler);
        }
    }
}
