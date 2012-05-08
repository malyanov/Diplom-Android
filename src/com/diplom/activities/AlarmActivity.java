package com.diplom.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.diplom.basics.Instrument;

public class AlarmActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);		
	}
	public void setInfo(int exchangeId, String instr)
    {
    	TextView exchange=(TextView)findViewById(R.id.ExchText);
    	TextView instrument=(TextView)findViewById(R.id.InstrText);
    	ImageView logo=(ImageView)findViewById(R.id.logo);
    	instrument.setText(instr);
    	if(exchangeId==Instrument.RTS)
    	{
    		exchange.setText("пря");
    		logo.setImageResource(R.drawable.rts);
    	}
    	else
    	{
    		exchange.setText("ллба");
    		logo.setImageResource(R.drawable.mmvb);
    	}
    }
}
