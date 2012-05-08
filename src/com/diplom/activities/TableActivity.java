package com.diplom.activities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.diplom.basics.Instrument;

public class TableActivity extends BaseActivity{
	private static final int ADD_QUOTE_CODE=1;
	private static List<TableRow> rows=new ArrayList<TableRow>();
	private TableLayout table;
	public TableActivity() {		
	}	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (TableRow row : rows)
			table.removeView(row);
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.table_content, (ViewGroup)findViewById(R.id.ContentLayout));
		inflater.inflate(R.layout.table_bottom, (ViewGroup)findViewById(R.id.BottomLayout));		
		table=(TableLayout)findViewById(R.id.Table);
		if(rows.size()>0){
			for (TableRow row : rows){				
				table.addView(row);
				View view=new View(getApplicationContext());
				view.setBackgroundColor(0xFF909090);
				table.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
			}
		}		
		findViewById(R.id.AddButton).setOnClickListener(new View.OnClickListener(){			
			public void onClick(View v){
				Intent intent = new Intent(TableActivity.this, AddQuotActivity.class);
	            startActivityForResult(intent, ADD_QUOTE_CODE);
			}
		});		
		findViewById(R.id.CancelAddButton).setOnClickListener(new View.OnClickListener(){			
			public void onClick(View v){
				for (TableRow row : rows)
					table.removeView(row);
			}
		});		
	}
	private Handler updateHandler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			Instrument instr=(Instrument)msg.obj;
			updateRow(instr.getCode(), instr.getValue());
		}
	};	
	public void updateRow(String code, double value){
		try{
			for (TableRow row : rows){
				Instrument instr=(Instrument)row.getTag();
				if(instr.getCode().equals(code)){				
					TextView valueField=(TextView)row.findViewById(R.id.RowValue);
					DecimalFormat df=new DecimalFormat("0.00");
					double oldValue=Double.parseDouble(valueField.getText().toString().replace(",", "."));
					valueField.setText(df.format(value));
					
					TextView changeField=(TextView)row.findViewById(R.id.RowChange);				
					double change=0;
					if(oldValue>0)
						change=(value-oldValue)/oldValue*100.0;
					String changeStr=df.format(change);
					if(change>0)		
						changeStr="+"+changeStr;
					if(change<0)
						changeField.setBackgroundColor(Color.RED);
					else changeField.setBackgroundColor(Color.GREEN);
					changeField.setText(changeStr);
					
					ImageView direction=(ImageView)row.findViewById(R.id.RowDirection);
					if(change<0)
						direction.setImageResource(R.drawable.down);
					else direction.setImageResource(R.drawable.up);
					return;
				}
			}
		}catch(Exception ex){
			Log.e("parsing", ex.toString());
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK&&requestCode==ADD_QUOTE_CODE){
			String instrumentCode=data.getStringExtra("instrumentCode");
			int exchangeId=data.getIntExtra("exchangeId", 0);
			
			addTableRow(exchangeId, instrumentCode, 0, 0);
			MainActivity.querer.addTask(new Instrument(exchangeId, "", instrumentCode, "", 0), updateHandler);
		}
	}	
	public void addTableRow(int exchangeId, String instrumentCode, double value, double change){		
		String exchangeName="пря";
		if(exchangeId==Instrument.MICEX)
			exchangeName="ллба";
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		TableRow tr=(TableRow)li.inflate(R.layout.table_row, null);
		
		TextView exchange=(TextView)tr.findViewById(R.id.RowExchange);
		exchange.setText(exchangeName);
		
		TextView instrument=(TextView)tr.findViewById(R.id.RowInstrument);
		instrument.setText(instrumentCode);
		
		TextView valueField=(TextView)tr.findViewById(R.id.RowValue);
		valueField.setText(new DecimalFormat("0.00").format(value));
		
		TextView changeField=(TextView)tr.findViewById(R.id.RowChange);
		String changeStr=new DecimalFormat("0.00").format(change);
		if(change>0)		
			changeStr="+"+changeStr;
		if(change<0)
			changeField.setBackgroundColor(Color.RED);
		else changeField.setBackgroundColor(Color.GREEN);
		changeField.setText(changeStr);
		
		ImageView direction=(ImageView)tr.findViewById(R.id.RowDirection);
		if(change<0)
			direction.setImageResource(R.drawable.down);
		else direction.setImageResource(R.drawable.up);
		
		tr.setTag(new Instrument(exchangeId, "", instrumentCode, "", 0));
		tr.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				table.removeView(v);				
				rows.remove(v);				
				MainActivity.querer.removeTask((Instrument)v.getTag());
				table.invalidate();
			}
		});		
		table.addView(tr);		
		rows.add(tr);
		View view=new View(getApplicationContext());
		view.setBackgroundColor(0xFF909090);
		table.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
	}	
}
