package com.diplom.elements;

import java.text.DecimalFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.diplom.activities.R;
import com.diplom.basics.Alarm;

public class AlarmsAdapter extends ArrayAdapter<Alarm> {
	private final Context context;
	private final Alarm[] values;
	private DecimalFormat df=new DecimalFormat("0.00");

	public AlarmsAdapter(Context context, Alarm[] values) {
		super(context, R.layout.alarm_row, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.alarm_row, parent, false);
		rowView.setTag(values[position]);
		TextView valueView = (TextView) rowView.findViewById(R.id.AlarmValue);
		valueView.setText(df.format(values[position].getExpectValue()));
		TextView instrView = (TextView) rowView.findViewById(R.id.AlarmInstrument);		
		instrView.setText(values[position].getInstrumentCode());		
		return rowView;
	}
}