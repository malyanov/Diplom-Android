package com.diplom.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.diplom.activities.R;
import com.diplom.basics.News;

public class NewsAdapter extends ArrayAdapter<News> {
	private final Context context;
	private final News[] values;

	public NewsAdapter(Context context, News[] values) {
		super(context, R.layout.news_row, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.news_row, parent, false);
		rowView.setTag(values[position]);
		TextView titleView = (TextView) rowView.findViewById(R.id.NewsTitle);
		titleView.setText(values[position].getTitle());
		TextView dateView = (TextView) rowView.findViewById(R.id.NewsDate);		
		dateView.setText(News.getDateFormat().format(values[position].getDate()));		
		return rowView;
	}
}