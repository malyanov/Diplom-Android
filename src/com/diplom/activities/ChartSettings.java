package com.diplom.activities;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.diplom.basics.Instrument;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.chart.AnalyseChart;
import com.diplom.chart.Chart;
import com.diplom.loaders.MICEX_Loader;
import com.diplom.loaders.RTS_Loader;
import com.diplom.settings.Settings;


public class ChartSettings extends Activity {
	
	private Spinner instrspinner;
	
	private int exchangeId=Instrument.MICEX;
	private String instrumentCode="GAZP";
	private AnalyseChart.Mode analyseMode=AnalyseChart.Mode.RSI;
	private Chart.Modes chartMode=Chart.Modes.CURVES;
	private QuotationType bidType=QuotationType.Hour_Bid; 
	
	private void loadInstruments()
	{
		if(exchangeId==Instrument.MICEX)
		{
			ArrayAdapter<CharSequence> instradapter = new ArrayAdapter<CharSequence>(getApplicationContext(), 
					android.R.layout.simple_spinner_item, MICEX_Loader.EmitentCodes);
	        instradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        instrspinner.setAdapter(instradapter);
		}
		else
		{
			ArrayAdapter<CharSequence> instradapter = new ArrayAdapter<CharSequence>(getApplicationContext(), 
					android.R.layout.simple_spinner_item, RTS_Loader.EmitentCodes);
	        instradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        instrspinner.setAdapter(instradapter);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart_settings);
		
		Spinner exchspinner = (Spinner) findViewById(R.id.SpinnerExchange);
		exchspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> spinner, View text,	int index, long lindex) {
				String value=(String)((TextView)text).getText();
				if(value.equals("РТС"))
					exchangeId=Instrument.RTS;
				else if(value.equals("ММВБ"))
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
        
        instrspinner = (Spinner) findViewById(R.id.SpinnerInstrument);
        instrspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> spinner, View text,	int index, long lindex) {
				String value=(String)((TextView)text).getText();
				instrumentCode=value;
			}
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});        
        
        Spinner analysespinner = (Spinner) findViewById(R.id.AnalyseSpinner);
        analysespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> spinner, View text, int index, long lindex) {				
				String value=(String)((TextView)text).getText();
				if(value.equals("RSI"))
					analyseMode=AnalyseChart.Mode.RSI;
				else if(value.equals("Stochastic"))
					analyseMode=AnalyseChart.Mode.Stochastic;
			}
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
        ArrayAdapter<CharSequence> analyseadapter = ArrayAdapter.createFromResource(
                this, R.array.analyse_items, android.R.layout.simple_spinner_item);
        analyseadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        analysespinner.setAdapter(analyseadapter);
        
        RadioGroup graphType=(RadioGroup)findViewById(R.id.GraphType);        
        graphType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb=(RadioButton)group.findViewById(checkedId);
				String type=(String) rb.getText();
				if(type.equals("Ломаная"))				
					chartMode=Chart.Modes.CURVES;				
				else if(type.equals("Бары"))
					chartMode=Chart.Modes.BARS;
				else if(type.equals("Свечи"))
					chartMode=Chart.Modes.CANDLES;
			}
		});
        RadioGroup bidTypeGroup=(RadioGroup)findViewById(R.id.BidType);        
        bidTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb=(RadioButton)group.findViewById(checkedId);
				String type=(String) rb.getText();
				if(type.equals("По часам"))
					bidType=QuotationType.Hour_Bid;
				else if(type.equals("По дням"))
					bidType=QuotationType.Day_Bid;
			}
		});
        ImageButton saveBtn=(ImageButton)findViewById(R.id.SaveSettings);
        saveBtn.setOnTouchListener(new ImageButton.OnTouchListener() {			
			public boolean onTouch(View v, MotionEvent event) {
				Settings.instrumentCode=instrumentCode;
				Settings.bidType=bidType;
				Settings.chartMode=chartMode;
				Settings.exchangeId=exchangeId;
				Settings.analyseMode=analyseMode;
				Settings.save();
				stopActivity();
				return false;
			}
		});
        ImageButton cancelBtn=(ImageButton)findViewById(R.id.CancelSettings);
        cancelBtn.setOnTouchListener(new ImageButton.OnTouchListener() {			
			public boolean onTouch(View v, MotionEvent event) {
				setResult(RESULT_CANCELED);
				stopActivity();
				return false;
			}
		});
	}
	private void stopActivity()
	{
		setResult(RESULT_OK);
		finish();
	}
}