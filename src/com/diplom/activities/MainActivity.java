package com.diplom.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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

public class MainActivity extends BaseActivity{
	private static final int SETTINGS_ACTIVITY_CODE=1;
//Charts
	private static Chart chart;
	private static AnalyseChart analizeGraph;
//
//	Help Objects		
	private List<Quotation> quots;	
//  
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDlg=ProgressDialog.show(this, "", "Загрузка данных. Подождите пожалуйста...", true);
        
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_content, (ViewGroup)findViewById(R.id.ContentLayout));
		inflater.inflate(R.layout.main_bottom, (ViewGroup)findViewById(R.id.BottomLayout));
        
        parser=new FileParser();
        //debug
        Settings.clear(this);
        //
        if(!Settings.load(this))//first app start        
        	Settings.save();        
        setInfo(Settings.exchangeId, Settings.instrumentCode);
        setChange(0);
        
        analizeGraph=new AnalyseChart(this);        
        ((LinearLayout)findViewById(R.id.analizeGraphContainer)).addView(analizeGraph);
        
        chart=new Chart(this, analizeGraph, false);        
        ((LinearLayout)findViewById(R.id.graphContainer)).addView(chart);
        
        findViewById(R.id.ChartButton).setOnClickListener(new OnClickListener() {		
    		public void onClick(View v) {
    			Intent intent = new Intent(MainActivity.this, ChartSettingsActivity.class);
                startActivityForResult(intent, SETTINGS_ACTIVITY_CODE);            
    		}
    	});
        findViewById(R.id.TableButton).setOnClickListener(new OnClickListener() {		
    		public void onClick(View v) {
    			Intent intent = new Intent(MainActivity.this, TableActivity.class);
                startActivity(intent);			
    		}
    	});
        findViewById(R.id.AlarmButton).setOnClickListener(new OnClickListener() {		
    		public void onClick(View v) {
    			Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
    		}
    	});
        findViewById(R.id.NewsButton).setOnClickListener(new OnClickListener() {		
    		public void onClick(View v) {	
    			Intent intent = new Intent(MainActivity.this, NewsActivity.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);	            			
    		}
    	});
        findViewById(R.id.FullScreenMode).setOnClickListener(new OnClickListener(){		
    		public void onClick(View v){
    			Intent intent = new Intent(MainActivity.this, FSChartActivity.class);			
                startActivity(intent);    			
    		}
    	});
        
        loadChartData();
    }
    //------------------------------------Chart Data Functions-----------------------------------------
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if(resultCode==RESULT_OK&&requestCode==SETTINGS_ACTIVITY_CODE){
			progressDlg=ProgressDialog.show(this, "", "Загрузка данных. Подождите пожалуйста...", true);
			setInfo(Settings.exchangeId, Settings.instrumentCode);			
			loadChartData();			
			analizeGraph.setMode(Settings.analyseMode);
		}
	};
	@Override
	protected void onQuotationUpdate(double value) {		
		super.onQuotationUpdate(value);
		chart.updateLastValue(value);
	}
    public void loadChartData(){
    	int monthsNum=1;
    	if(Settings.bidType==QuotationType.Day_Bid)
        	monthsNum=3;        
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH, -monthsNum);
        Date start=calendar.getTime();
        Handler dataHandler=new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        		try{
        			quots=parser.readFile();
	        	}catch(Exception ex){
	    			showError("Ошибка!", "Неверный формат данных истории котировок");
	    		}
        		quots.add((Quotation)msg.obj);
        		chart.init(quots);
        		if(curInstrument!=null)
        			querer.removeTask(curInstrument.getExchangeId(), curInstrument.getCode());
        		curInstrument=new Instrument(Settings.exchangeId, Settings.boardCode, Settings.instrumentCode, "", 0);
        		querer.addTask(curInstrument, updLastQuotHandler);
        		if(progressDlg!=null)
        			progressDlg.hide();
        	}
        };     
        try{
	        if(Settings.exchangeId==Instrument.RTS){
	        	rts=new RTS_Loader();
	        	rts.getDataForChart(Settings.instrumentCode, start, Settings.bidType, dataHandler);        	
	        }
	        else if(Settings.exchangeId==Instrument.MICEX){
	        	micex=new MICEX_Loader();
	        	micex.getDataForChart(Settings.instrumentCode, start, Settings.bidType, dataHandler);
	        }
        }catch(Exception ex){
			showError("Ошибка!", "Потеряно соединение с Интернетом");
		}
    }
    //----------------------------------------------------------------------------------------------------    
    @Override
	protected void onDestroy() {		
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}