package com.diplom.activities;

import java.text.DecimalFormat;

import com.diplom.basics.Instrument;
import com.diplom.basics.Querer;
import com.diplom.dbcache.DBHelper;
import com.diplom.loaders.FileParser;
import com.diplom.loaders.MICEX_Loader;
import com.diplom.loaders.RTS_Loader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseActivity extends Activity {
//	Common Variables
	public static Instrument curInstrument=null;
	public static DBHelper db;
	public static Querer querer;	
//
//	Help Objects
	private static double oldChangeValue=0;
//
//	Loaders and parsers
	protected static MICEX_Loader micex;
    protected static RTS_Loader rts;
    protected static  FileParser parser;
    
	protected static ProgressDialog progressDlg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity);
		if(db==null)
			db=new DBHelper(getApplicationContext());
		if(querer==null)
			querer=new Querer();
		if(curInstrument!=null){
			setInfo(curInstrument.getExchangeId(), curInstrument.getCode());
			setChange(curInstrument.getValue());
		}
	}
	protected void setChange(double value){
    	TextView valueField=(TextView)findViewById(R.id.value);
    	TextView changeField=(TextView)findViewById(R.id.change);
    	ImageView changeDir=(ImageView)findViewById(R.id.trend);    	
    	if(oldChangeValue<value){    		
    		changeDir.setImageResource(R.drawable.up);
    		valueField.setBackgroundColor(Color.GREEN);
    	}
    	else if(oldChangeValue>value){    		
    		changeDir.setImageResource(R.drawable.down);
    		valueField.setBackgroundColor(Color.RED);
    	}
    	else{
    		changeDir.setImageDrawable(null);
    		valueField.setBackgroundColor(Color.GRAY);
    	}
    	valueField.setText(String.valueOf(value));
    	double changeVal=0;
    	if(oldChangeValue>0)
    		changeVal=(value-oldChangeValue)/oldChangeValue*100.0;
    	changeField.setText(new DecimalFormat("0.00").format(changeVal)+"%");
    	if(changeVal>0)
    		changeField.setTextColor(Color.GREEN);
    	else if(changeVal>0) changeField.setTextColor(Color.RED);
    	else changeField.setTextColor(Color.GRAY);
    	oldChangeValue=value;
    }
    protected void setInfo(int exchangeId, String instr){
    	TextView exchange=(TextView)findViewById(R.id.ExchText);
    	TextView instrument=(TextView)findViewById(R.id.InstrText);
    	ImageView logo=(ImageView)findViewById(R.id.logo);
    	instrument.setText(instr);
    	if(exchangeId==Instrument.RTS){
    		exchange.setText("пря");
    		logo.setImageResource(R.drawable.rts);
    	}
    	else{
    		exchange.setText("ллба");
    		logo.setImageResource(R.drawable.mmvb);
    	}
    }
    protected Handler updLastQuotHandler=new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		double value=((Instrument)msg.obj).getValue();
    		onQuotationUpdate(value);
    	}
    };
    protected void onQuotationUpdate(double value){		
		setChange(value);
		curInstrument.setValue(value);
    }
}
