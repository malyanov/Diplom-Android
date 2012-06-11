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


public class ChartSettingsActivity extends Activity {
	
	private Spinner instrspinner;
	
	private int exchangeId=Instrument.MICEX;
	private String instrumentCode="GAZP";
	private AnalyseChart.Mode analyseMode=AnalyseChart.Mode.RSI;
	private Chart.Modes chartMode=Chart.Modes.CURVES;
	private QuotationType bidType=QuotationType.Hour_Bid;
	private boolean firstLoad=true;
	
	private void loadInstruments(){
		ArrayAdapter<CharSequence> instradapter;
		if(exchangeId==Instrument.MICEX)		
			instradapter = new ArrayAdapter<CharSequence>(getApplicationContext(), 
					android.R.layout.simple_spinner_item, MICEX_Loader.EmitentCodes);
		else instradapter = new ArrayAdapter<CharSequence>(getApplicationContext(),
					android.R.layout.simple_spinner_item, RTS_Loader.EmitentCodes);
		instradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		instrspinner.setAdapter(instradapter);		
		if(firstLoad){
			int index=instradapter.getPosition(instrumentCode);
			instrspinner.setSelection(index);
			firstLoad=false;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart_settings);		
		
		instrumentCode=Settings.instrumentCode;
		exchangeId=Settings.exchangeId;
		analyseMode=Settings.analyseMode;
		bidType=Settings.bidType;
		chartMode=Settings.chartMode;
		
		instrspinner = (Spinner) findViewById(R.id.SpinnerInstrument);        
		
		Spinner exchspinner = (Spinner) findViewById(R.id.SpinnerExchange);
		ArrayAdapter<CharSequence> exchadapter = ArrayAdapter.createFromResource(
                this, R.array.exchange_items, android.R.layout.simple_spinner_item);
        exchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exchspinner.setAdapter(exchadapter);
        exchspinner.setSelection(exchangeId);
        
        Spinner analysespinner = (Spinner) findViewById(R.id.AnalyseSpinner);
        ArrayAdapter<CharSequence> analyseadapter = ArrayAdapter.createFromResource(
                this, R.array.analyse_items, android.R.layout.simple_spinner_item);
        analyseadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        analysespinner.setAdapter(analyseadapter);
        int index=0;
        switch(analyseMode){
	        case RSI:
	        	index=0;
	        	break;
	        case Stochastic:
	        	index=1;
	        	break;
	        case Momentum:
	        	index=2;
        }
        analysespinner.setSelection(index);              
        
        RadioGroup graphType=(RadioGroup)findViewById(R.id.GraphType);
        for(int i=0;i<graphType.getChildCount();i++){
        	RadioButton r=(RadioButton)graphType.getChildAt(i);
        	if(r.getText().equals(chartMode.locName))
        		r.setChecked(true);
        }
        
        RadioGroup bidTypeGroup=(RadioGroup)findViewById(R.id.BidType);
        for(int i=0;i<bidTypeGroup.getChildCount();i++){
        	RadioButton r=(RadioButton)bidTypeGroup.getChildAt(i);
        	if(r.getText().equals(bidType.locName))
        		r.setChecked(true);
        }
              
        ImageButton saveBtn=(ImageButton)findViewById(R.id.SaveSettings);
        saveBtn.setOnTouchListener(new ImageButton.OnTouchListener() {			
			public boolean onTouch(View v, MotionEvent event) {
				Settings.instrumentCode=instrumentCode;
				Settings.bidType=bidType;
				Settings.chartMode=chartMode;
				Settings.exchangeId=exchangeId;
				Settings.analyseMode=analyseMode;
				BaseActivity.resetOldChangeValue();
				Settings.save();
				stopActivity(RESULT_OK);
				return false;
			}
		});
        ImageButton cancelBtn=(ImageButton)findViewById(R.id.CancelSettings);
        cancelBtn.setOnTouchListener(new ImageButton.OnTouchListener() {			
			public boolean onTouch(View v, MotionEvent event) {				
				stopActivity(RESULT_CANCELED);
				return false;
			}
		});        
        instrspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> spinner, View text,	int index, long lindex) {
				if(text==null)
					return;
				String value=(String)((TextView)text).getText();
				instrumentCode=value;
			}
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
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
        analysespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> spinner, View text, int index, long lindex) {				
				String value=(String)((TextView)text).getText();
				if(value.equals("RSI"))
					analyseMode=AnalyseChart.Mode.RSI;
				else if(value.equals("Stochastic"))
					analyseMode=AnalyseChart.Mode.Stochastic;
				else if(value.equals("Momentum"))
					analyseMode=AnalyseChart.Mode.Momentum;
			}
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
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
	}
	private void stopActivity(int result){
		setResult(result);
		finish();
	}
}