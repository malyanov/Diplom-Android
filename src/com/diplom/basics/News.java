package com.diplom.basics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class News {
	private Date date;
	private String title, description, link;
	private static final SimpleDateFormat sdf=new SimpleDateFormat("dd MMM  HH:mm", Locale.getDefault());
	private static final String newsBasePath="http://rts.micex.ru";
	public News(Date date, String title, String description, String guid) {
		super();
		this.date = date;
		this.title = title;
		this.description = description;
		this.link=newsBasePath+guid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
	@Override
	public String toString() {		
		return sdf.format(date)+" - "+title;
	}
	public static SimpleDateFormat getDateFormat() {
		return sdf;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}	
}
