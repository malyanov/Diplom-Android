package com.diplom.activities;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.diplom.basics.Instrument;
import com.diplom.basics.Querer;
import com.diplom.dbcache.DBHelper;
import com.diplom.loaders.FileParser;
import com.diplom.loaders.MICEX_Loader;
import com.diplom.loaders.RTS_Loader;

public class BaseActivity extends Activity {
//	Common Variables
	public static Instrument curInstrument=null;
	public static DBHelper db;
	public static Querer querer;	
	private static BaseActivity instance;
	private static double oldChangeValue=0;
	//Constants
	private static final long ERROR_WINDOW_DELAY=10000l; 
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
		instance=this;
	}
	@Override
	protected void onResume() {		
		super.onResume();
		instance=this;
	}
	
	public static BaseActivity getInstance() {
		return instance;
	}

	protected void setChange(double value){
    	TextView valueField=(TextView)findViewById(R.id.value);
    	TextView changeField=(TextView)findViewById(R.id.change);
    	ImageView changeDir=(ImageView)findViewById(R.id.trend);    	
    	if(oldChangeValue<=value){//&&oldChangeValue!=0){    		
    		changeDir.setImageResource(R.drawable.up);
    		valueField.setBackgroundColor(Color.GREEN);
    	}
    	else if(oldChangeValue>value&&oldChangeValue!=0){    		
    		changeDir.setImageResource(R.drawable.down);
    		valueField.setBackgroundColor(Color.RED);
    	}
//    	else{
//    		changeDir.setImageDrawable(null);
//    		valueField.setBackgroundColor(Color.GRAY);
//    	}
    	valueField.setText(String.valueOf(value));
    	double changeVal=0;
    	if(oldChangeValue>0)
    		changeVal=(value-oldChangeValue)/oldChangeValue*100.0;
    	changeField.setText(new DecimalFormat("0.00").format(changeVal)+"%");
    	if(changeVal>=0)
    		changeField.setTextColor(Color.GREEN);
    	else /*if(changeVal>0)*/ changeField.setTextColor(Color.RED);
    	//else changeField.setTextColor(Color.GRAY);
    	oldChangeValue=value;
    }
    protected void setInfo(int exchangeId, String instr){
    	TextView exchange=(TextView)findViewById(R.id.ExchText);
    	TextView instrument=(TextView)findViewById(R.id.InstrText);
    	ImageView logo=(ImageView)findViewById(R.id.logo);
    	instrument.setText(instr);
    	if(exchangeId==Instrument.RTS){
    		exchange.setText("���");
    		logo.setImageResource(R.drawable.rts);
    	}
    	else{
    		exchange.setText("����");
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
    public static void resetOldChangeValue(){
    	oldChangeValue=0;
    }
    public static void showError(String caption, String info){
    	final PopupWindow errorPopup=new PopupWindow(BaseActivity.getInstance());		
		LayoutInflater inflater = (LayoutInflater) BaseActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		View popupView = inflater.inflate(R.layout.error_popup, (ViewGroup)BaseActivity.getInstance().findViewById(R.id.baseBack), false);
		TextView captionView = (TextView) popupView.findViewById(R.id.ErrorCapture);		
		captionView.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				errorPopup.dismiss();
			}
		});
		final Timer timer=new Timer();
		timer.schedule(new TimerTask() {			
			@Override
			public void run() {
				errorPopup.dismiss();
				timer.cancel();
				timer.purge();
			}
		}, ERROR_WINDOW_DELAY);
		captionView.setText(caption);
		TextView textView = (TextView) popupView.findViewById(R.id.ErrorText);		
		textView.setText(info);		
		errorPopup.setContentView(popupView);
		errorPopup.showAtLocation((ViewGroup)BaseActivity.getInstance().findViewById(R.id.baseBack), Gravity.CENTER, 0, 0);
		errorPopup.update(400, 170);
		MediaPlayer mp=MediaPlayer.create(getInstance(), R.raw.alert);		
        mp.start();
	}
    public static void showAlert(String info){    	
    	final PopupWindow alertPopup=new PopupWindow(BaseActivity.getInstance());
    	final Vibrator vibro = (Vibrator) getInstance().getSystemService(Context.VIBRATOR_SERVICE);
		LayoutInflater inflater = (LayoutInflater) BaseActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		View popupView = inflater.inflate(R.layout.alert_popup, (ViewGroup)BaseActivity.getInstance().findViewById(R.id.baseBack), false);
		TextView captionView = (TextView) popupView.findViewById(R.id.AlertCapture);		
		captionView.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				alertPopup.dismiss();
				vibro.cancel();
			}
		});		
		TextView textView = (TextView) popupView.findViewById(R.id.AlertText);		
		textView.setText(info);		
		alertPopup.setContentView(popupView);
		alertPopup.showAtLocation((ViewGroup)BaseActivity.getInstance().findViewById(R.id.baseBack), Gravity.CENTER, 0, 0);
		alertPopup.update(400, 170);		
		MediaPlayer.create(getInstance(), R.raw.alert).start();		        
        vibro.vibrate(new long[]{250, 500}, 0);
    }
}
