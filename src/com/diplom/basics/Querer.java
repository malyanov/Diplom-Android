package com.diplom.basics;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.os.Handler;

public class Querer{
	private List<InstrumentUpdateTask> tasks=new ArrayList<InstrumentUpdateTask>();
	private static final long UPDATE_TIME=60000;
	private Timer timer;
	
	public Querer()
	{	
		timer=new Timer();		
	}
	
	public void stopTimer()
	{
		timer.cancel();
	}
	
	public void addTask(Instrument instrument, Handler handler)
	{
		InstrumentUpdateTask task=new InstrumentUpdateTask(instrument, handler);
		timer.schedule(task, 0, UPDATE_TIME);
		tasks.add(task);
	}
	
	public void removeTask(Instrument instrument)
	{
		for (int i = 0; i < tasks.size(); i++) {
			Instrument instr=tasks.get(i).getInstrument();
			if(instrument.getCode().equals(instr.getCode())&&instrument.getExchangeId()==instr.getExchangeId())
			{
				tasks.get(i).cancel();
				tasks.remove(i);
				return;
			}
		}
	}
}
