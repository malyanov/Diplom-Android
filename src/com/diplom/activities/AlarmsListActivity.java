package com.diplom.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.diplom.basics.Alarm;
import com.diplom.basics.Instrument;
import com.diplom.elements.AlarmsAdapter;

public class AlarmsListActivity extends BaseActivity{
	private static List<Alarm> alarms=new ArrayList<Alarm>();
	private static HashMap<String, Handler> handlers=new HashMap<String, Handler>();
	private ListView list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_list);
		list=(ListView)findViewById(R.id.AlarmsListView);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				Alarm alarm=(Alarm)view.getTag();
				new AlertDialog.Builder(getInstance())
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("��������������")
		        .setMessage(String.format("�� ������������� ������ ������� �������������� ��� %s �� �������� %.3f", alarm.getInstrumentCode(), alarm.getExpectValue()))
		        .setPositiveButton("��", new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int which) {
		        		Alarm alarm=alarms.get(position);
		        		querer.removeTask(alarm.getExchangeId(), alarm.getInstrumentCode());
		        		alarms.remove(position);
		        		showAlarms();
		        	}
		        })
		        .setNegativeButton("������", null)
		        .show();				
			}			
		});
		findViewById(R.id.ClearAlarmsBtn).setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				for (Alarm alarm : alarms)
					querer.removeTask(alarm.getExchangeId(), alarm.getInstrumentCode());
				alarms.clear();				
				showAlarms();
			}
		});
		findViewById(R.id.CloseAlarmsBtn).setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				finish();
			}
		});
		showAlarms();
	}
	private AlarmsListActivity getInstance(){
		return this;
	}
	public static void addAlarm(Alarm warn){
		alarms.add(warn);
		String key=warn.getInstrumentCode()+warn.getExchangeId();
		if(!handlers.containsKey(key)){
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg) {				
					Instrument instr=((Instrument)msg.obj);
					for (Alarm w : alarms) {
						if(w.getExchangeId()==instr.getExchangeId()&&w.getInstrumentCode().equals(instr.getCode())&&instr.getValue()>=w.getExpectValue()){
							Log.i("expect value", "alert");
						}
					}
				}
			};
			handlers.put(key, handler);
			querer.addTask(new Instrument(warn.getExchangeId(), curInstrument.getBoard(), curInstrument.getCode(), curInstrument.getName(), warn.getExpectValue()), handler);
		}
	}
	private void showAlarms(){
		list.setAdapter(new AlarmsAdapter(getApplication(), alarms.toArray(new Alarm[alarms.size()])));
	}
}
