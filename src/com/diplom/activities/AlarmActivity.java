package com.diplom.activities;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.diplom.basics.Alarm;

public class AlarmActivity extends BaseActivity{
	private double value, startValue;
	private DecimalFormat df=new DecimalFormat("0.00");
	private boolean programInput=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.alarm_content, (ViewGroup)findViewById(R.id.ContentLayout));
		inflater.inflate(R.layout.alarm_bottom, (ViewGroup)findViewById(R.id.BottomLayout));		
		final EditText valueField=(EditText)findViewById(R.id.AlarmValue);		
		value=curInstrument.getValue();
		valueField.setText(df.format(value));
		//Content
		((TextView)findViewById(R.id.WarningText)).setText(String.format("Предупредить меня, когда %s достигнет:", curInstrument.getCode()));
		final SeekBar valueBar=(SeekBar)findViewById(R.id.AlarmValueBar);
		valueBar.setMax(100);
		valueBar.setProgress(50);
		startValue=curInstrument.getValue();
		valueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {			
			public void onStopTrackingTouch(SeekBar seekBar) {				
			}			
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				programInput=true;
				value=(startValue+(progress-50)/3.0);
				valueField.setText(df.format(value));
			}
		});
		valueField.setOnKeyListener(new View.OnKeyListener() {			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				programInput=false;
				return false;
			}
		});
		valueField.addTextChangedListener(new TextWatcher() {			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!s.toString().trim().equals("")&&!programInput){
					startValue=Double.parseDouble(s.toString().replace(",", "."));
					valueBar.setProgress(50);
				}
				programInput=false;
			}			
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {				
			}			
			public void afterTextChanged(Editable s) {
			}
		});
		findViewById(R.id.IncrAlarmValue).setOnClickListener(new View.OnClickListener(){			
			public void onClick(View v) {				
				valueBar.setProgress(valueBar.getProgress()+2);
			}
		});
		findViewById(R.id.DecrAlarmValue).setOnClickListener(new View.OnClickListener(){			
			public void onClick(View v) {
				valueBar.setProgress(valueBar.getProgress()-2);
			}
		});
		//Bottom
		findViewById(R.id.SetAlarm).setOnClickListener(new View.OnClickListener(){			
			public void onClick(View v) {
				double value=Double.parseDouble(valueField.getText().toString().replace(",","."));
				AlarmsListActivity.addAlarm(new Alarm(curInstrument.getCode(), value, curInstrument.getExchangeId()));
				showPopup(curInstrument.getCode(), value);
			}
		});
		findViewById(R.id.CloseAlarm).setOnClickListener(new View.OnClickListener(){			
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.AlarmsList).setOnClickListener(new View.OnClickListener(){			
			public void onClick(View v) {
				Intent intent = new Intent(AlarmActivity.this, AlarmsListActivity.class);
                startActivity(intent);
			}
		});
	}
	private void showPopup(String instrument, double value){
		final PopupWindow popup=new PopupWindow(getInstanse());
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		View popupView = inflater.inflate(R.layout.popup, (ViewGroup)findViewById(R.id.WarningText).getRootView(), false);
		TextView captureView = (TextView) popupView.findViewById(R.id.PopupCapture);
		captureView.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				popup.dismiss();
			}
		});
		captureView.setText("Информация");
		TextView textView = (TextView) popupView.findViewById(R.id.PopupText);		
		textView.setText(String.format("Установлено предупреждение для %s на значение %.3f", instrument, value));		
		popup.setContentView(popupView);		
		popup.showAtLocation(findViewById(R.id.WarningText).getRootView(), Gravity.CENTER, 0, 0);
		popup.update(300, 200);
	}
	private AlarmActivity getInstanse(){
		return this;
	}
}
