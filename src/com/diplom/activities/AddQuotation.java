package com.diplom.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.diplom.basics.Instrument;
import com.diplom.loaders.MICEX_Loader;
import com.diplom.loaders.RTS_Loader;

public class AddQuotation extends Activity{
	private Spinner instrspinner;
	
	private int exchangeId=Instrument.MICEX;
	private String instrumentCode="GAZP";	
	
	private void loadInstruments(){
		if(exchangeId==Instrument.MICEX){
			ArrayAdapter<CharSequence> instradapter = new ArrayAdapter<CharSequence>(getApplicationContext(), 
					android.R.layout.simple_spinner_item, MICEX_Loader.EmitentCodes);
	        instradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        instrspinner.setAdapter(instradapter);
		}
		else{
			ArrayAdapter<CharSequence> instradapter = new ArrayAdapter<CharSequence>(getApplicationContext(), 
					android.R.layout.simple_spinner_item, RTS_Loader.EmitentCodes);
	        instradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        instrspinner.setAdapter(instradapter);
		}
	}
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_quote_activity);
		Spinner exchspinner = (Spinner) findViewById(R.id.SpExchange);
		exchspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {			
			public void onItemSelected(AdapterView<?> spinner, View text,	int index, long lindex) {
				String value=(String)((TextView)text).getText();
				if(value.equals("пря"))
					exchangeId=Instrument.RTS;
				else if(value.equals("ллба"))
					exchangeId=Instrument.MICEX;
				loadInstruments();
			}			
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
        ArrayAdapter<CharSequence> exchadapter = ArrayAdapter.createFromResource(
                this, R.array.exchange_items, android.R.layout.simple_spinner_item);
        exchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exchspinner.setAdapter(exchadapter);        
        instrspinner = (Spinner) findViewById(R.id.SpInstrument);
        instrspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> spinner, View text,	int index, long lindex) {
				String value=(String)((TextView)text).getText();
				instrumentCode=value;
			}			
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
        ImageButton addBtn=(ImageButton)findViewById(R.id.AddQuote);
        addBtn.setOnTouchListener(new ImageButton.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Intent data=new Intent();
				Bundle b = new Bundle();
			    b.putString("instrumentCode", instrumentCode);
			    b.putInt("exchangeId", exchangeId);
			    data.putExtras(b);
				setResult(RESULT_OK, data);
				finish();
				return false;
			}
		});
        ImageButton cancelBtn=(ImageButton)findViewById(R.id.CancelQuote);
        cancelBtn.setOnTouchListener(new ImageButton.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				setResult(RESULT_CANCELED);
				finish();
				return false;
			}
		});
	}	
}
