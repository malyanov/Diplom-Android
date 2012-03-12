package com.diplom.basics;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

import com.diplom.loaders.*;

public class InstrumentUpdateTask extends TimerTask{
	private Instrument instrument;
	private Handler handler;
	
	public InstrumentUpdateTask(Instrument instrument, Handler handler) {
		this.instrument=instrument;
		this.handler=handler;
	}
	@Override
	public void run() {
		if(instrument.getExchangeId()==Instrument.RTS)
		{			
			Double value=(new RTS_Loader()).getCurrentValue(instrument.getCode());
			instrument.setValue(value);
			Message message = handler.obtainMessage(1, instrument);
            handler.sendMessage(message);
		}
		else if(instrument.getExchangeId()==Instrument.MICEX)
		{
			Double value=(new MICEX_Loader()).getCurrentValue(instrument.getBoard(), instrument.getCode());
			instrument.setValue(value);
			Message message = handler.obtainMessage(1, instrument);
            handler.sendMessage(message);
		}
	}
	public Instrument getInstrument() {
		return instrument;
	}
}
