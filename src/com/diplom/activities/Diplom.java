package com.diplom.activities;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diplom.basics.Instrument;
import com.diplom.basics.Querer;
import com.diplom.basics.Quotation;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.chart.AnalyseChart;
import com.diplom.chart.Chart;
import com.diplom.dbcache.DBHelper;
import com.diplom.loaders.FileParser;
import com.diplom.loaders.MICEX_Loader;
import com.diplom.loaders.RTS_Loader;
import com.diplom.settings.Settings;

public class Diplom extends Activity {	
	private double oldChangeValue=0;
	public static Chart chart;
	public static AnalyseChart analizeGraph;
	public static DBHelper db;
	public static Querer querer;
	
	private static final int SETTINGS_ACTIVITY_CODE=1;
	
	public static Context context;
	
	private List<Quotation> quots;
	private MICEX_Loader micex;
    private RTS_Loader rts;
    private FileParser parser;
    private Instrument curInstrument=null;
    
    private ProgressDialog progressDlg;
	
	private OnClickListener ChartClick=new OnClickListener() {		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Diplom.this, ChartSettings.class);
            startActivityForResult(intent, SETTINGS_ACTIVITY_CODE);            
		}
	};	
	private OnClickListener TableClick=new OnClickListener() {		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Diplom.this, InfoTable.class);
            startActivity(intent);			
		}
	};
	private OnClickListener AlarmClick=new OnClickListener() {		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Diplom.this, Alarm.class);
            startActivity(intent);
		}
	};
	private OnClickListener SettingsClick=new OnClickListener() {		
		@Override
		public void onClick(View v) {	
			Intent intent = new Intent(Diplom.this, SettingsActivity.class);
            startActivity(intent);	            			
		}
	};	
	private OnClickListener FullChartClick=new OnClickListener() {		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Diplom.this, FullScreenChart.class);			
            startActivity(intent);
		}
	};
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDlg=ProgressDialog.show(this, "", "Загрузка данных. Подождите пожалуйста...", true);
        setContentView(R.layout.main);
        context=getApplicationContext();
        parser=new FileParser();
        //debug
        Settings.clear(this);
        //
        if(!Settings.load(this))//first app start        
        	Settings.save();        
        db=new DBHelper(getApplicationContext());
        querer=new Querer();
        setInfo(Settings.exchangeId, Settings.instrumentCode);
        setChange(0);
        analizeGraph=new AnalyseChart(context);        
        ((LinearLayout)findViewById(R.id.analizeGraphContainer)).addView(analizeGraph);
        
        chart=new Chart(context, analizeGraph, false);        
        ((LinearLayout)findViewById(R.id.graphContainer)).addView(chart);
        
        findViewById(R.id.ChartButton).setOnClickListener(ChartClick);
        findViewById(R.id.TableButton).setOnClickListener(TableClick);
        findViewById(R.id.AlarmButton).setOnClickListener(AlarmClick);
        findViewById(R.id.SettingsButton).setOnClickListener(SettingsClick);
        findViewById(R.id.FullScreenMode).setOnClickListener(FullChartClick);
        
        loadChartData();
    }
    //------------------------------------Chart Data Functions-----------------------------------------
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if(resultCode==RESULT_OK&&requestCode==SETTINGS_ACTIVITY_CODE)
		{
			progressDlg=ProgressDialog.show(this, "", "Загрузка данных. Подождите пожалуйста...", true);
			setInfo(Settings.exchangeId, Settings.instrumentCode);
			loadChartData();
			analizeGraph.setMode(Settings.analyseMode);
		}
	};
    private Handler updLastQuotHandler=new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		double value=((Instrument)msg.obj).getValue();
    		chart.updateLastValue(value);
    		setChange(value);
    	}
    };
    public void loadChartData()
    {
    	int monthsNum=1;
    	if(Settings.bidType==QuotationType.Day_Bid)
        	monthsNum=3;
        Date now=new Date();
        Date start=new Date(now.getYear(), now.getMonth()-monthsNum, now.getDate());
        Handler dataHandler=new Handler(){
        	@Override
        	public void handleMessage(Message msg) {        		        	
        		quots=parser.readFile();
        		quots.add((Quotation)msg.obj);
        		chart.init(quots);
        		if(curInstrument!=null)
        			querer.removeTask(curInstrument);
        		curInstrument=new Instrument(Settings.exchangeId, Settings.boardCode, Settings.instrumentCode, "", 0);
        		querer.addTask(curInstrument, updLastQuotHandler);
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
    //----------------------------------------------------------------------------------------------------
    public void setChange(double value)
    {
    	TextView valueField=(TextView)findViewById(R.id.value);
    	TextView changeField=(TextView)findViewById(R.id.change);
    	ImageView changeDir=(ImageView)findViewById(R.id.trend);    	
    	if(oldChangeValue<value)
    	{
    		changeDir.setImageResource(R.drawable.up);
    		valueField.setBackgroundColor(Color.GREEN);
    	}
    	else if(oldChangeValue>value)
    	{
    		changeDir.setImageResource(R.drawable.down);
    		valueField.setBackgroundColor(Color.RED);
    	}
    	valueField.setText(String.valueOf(value));
    	double changeVal=0;
    	if(oldChangeValue>0)
    		changeVal=(value-oldChangeValue)/oldChangeValue*100.0;
    	changeField.setText(new DecimalFormat("0.00").format(changeVal)+"%");
    	if(changeVal>=0)
    		changeField.setTextColor(Color.GREEN);
    	else changeField.setTextColor(Color.RED);
    	oldChangeValue=value;
    }
    public void setInfo(int exchangeId, String instr)
    {
    	TextView exchange=(TextView)findViewById(R.id.ExchText);
    	TextView instrument=(TextView)findViewById(R.id.InstrText);
    	ImageView logo=(ImageView)findViewById(R.id.logo);
    	instrument.setText(instr);
    	if(exchangeId==Instrument.RTS)
    	{
    		exchange.setText("РТС");
    		logo.setImageResource(R.drawable.rts);
    	}
    	else
    	{
    		exchange.setText("ММВБ");
    		logo.setImageResource(R.drawable.mmvb);
    	}
    }
    @Override
	protected void onDestroy() {		
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}