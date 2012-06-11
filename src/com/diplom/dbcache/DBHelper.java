package com.diplom.dbcache;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.diplom.basics.Quotation;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.basics.Alarm;

public class DBHelper extends SQLiteOpenHelper {
	public static final String DB_NAME="informer";
	public static final int DB_VERSION=1;
	
	public static final int RTS=1, MMVB=2;
	public static final int HOUR_BID=1, DAY_BID=2;
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);		
	}
	@Override
	public void onCreate(SQLiteDatabase db) {		
		db.execSQL("CREATE TABLE instruments(id_instrument INTEGER PRIMARY KEY AUTOINCREMENT, id_exchange int, instrument_name varchar(30))");		
		db.execSQL("CREATE TABLE warnings(id_warning INTEGER PRIMARY KEY AUTOINCREMENT, id_instrument int, expect_value real, " +
				"FOREIGN KEY(id_instrument) REFERENCES instruments(id_instrument))");		
		db.execSQL("CREATE TABLE quotations(id_quotation INTEGER PRIMARY KEY AUTOINCREMENT, id_instrument int, id_type int, quotation_time datetime, min_price real," +
				" max_price real, open_price real, close_price real, FOREIGN KEY(id_instrument) REFERENCES instruments(id_instrument))");	
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {		
	}
	public List<Quotation> getQuotations(int instrumentId, int type, Date start)
	{
		SQLiteDatabase db=this.getReadableDatabase();		
		Cursor cur=db.rawQuery("SELECT * FROM quotations WHERE id_instrument=? AND id_type=? AND quotation_time>datetime("+start.toString()+")",new String [] {String.valueOf(instrumentId), String.valueOf(type)});
		db.close();
		List<Quotation> result=new ArrayList<Quotation>();
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
		Date date=null;
		QuotationType qtype=null;
		while(cur.moveToNext())
		{			
			try {
				date = iso8601Format.parse(cur.getString(cur.getColumnIndex("quotation_time")));
			} catch (ParseException e) {				
				e.printStackTrace();
			}
			type=cur.getInt(cur.getColumnIndex("id_type"));
			if(type==1)
				qtype=QuotationType.Hour_Bid;
			else qtype=QuotationType.Day_Bid;
			result.add(new Quotation(cur.getDouble(cur.getColumnIndex("open_price")), cur.getDouble(cur.getColumnIndex("close_price")), cur.getDouble(cur.getColumnIndex("low_price")), 
					cur.getDouble(cur.getColumnIndex("high_price")), date, qtype));
		}
		return result;
	}
	public void clearQuotations()
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DELETE FROM quotations");
		db.close();
	}
	public void insertQuotations(final List<Quotation> quotes, final int instrumentId)
	{
		final SQLiteDatabase db=this.getWritableDatabase();
		Thread thread=new Thread(){
			@Override
			public void run() {
				int type=HOUR_BID;
				DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(quotes.get(0).type==QuotationType.Day_Bid)
					type=DAY_BID;
				ContentValues cv=new ContentValues();		
				for (Quotation q : quotes) {		
					cv.put("id_instrument", instrumentId);		
					cv.put("id_type", type);
					cv.put("quotation_time", iso8601Format.format(q.dateTime));
					cv.put("min_price", q.lowValue);
					cv.put("max_price", q.highValue);
					cv.put("open_price", q.openValue);
					cv.put("close_price", q.closeValue);
					db.insert("exchanges", null, cv);
					cv.clear();
				}
				db.close();
			}
		};		
		thread.start();
	}
	public void insertExchanges()
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("exchange_name", "пря");		
		db.insert("exchanges", null, cv);
		cv.clear();
		cv.put("exchange_name", "ллба");		
		db.insert("exchanges", null, cv);
		db.close();
	}
	public void insertInstrument(int exchageId, String name)
	{		
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("id_exchange", exchageId);		
		cv.put("instrument_name", name);
		db.insert("instruments", null, cv);
		db.close();
	}	
	public void clearInstruments()
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DELETE FROM instruments");
		db.close();
	}
	public void insertWarning(int instrumentId, double value)
	{		
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("id_instrument", instrumentId);		
		cv.put("expect_value", value);
		db.insert("warnings", null, cv);
		db.close();
	}
	public void clearWarnings()
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DELETE FROM warnings");
		db.close();
	}
	public void removeWarning(int warningId)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DELETE FROM warnings WHERE id_warning="+warningId);
		db.close();
	}
	public List<Alarm> getWarnings()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cur=db.rawQuery("SELECT warnings.*, instruments.id_exchange, instruments.instrument_name FROM warnings INNER JOIN instruments ON warnings.id_instrument=instruments.id_instrument",new String [] {});
		db.close();
		List<Alarm> result=new ArrayList<Alarm>();
		while(cur.moveToNext())
			result.add(new Alarm(cur.getString(cur.getColumnIndex("instrument_name")), cur.getDouble(cur.getColumnIndex("expect_value")), 
					cur.getInt(cur.getColumnIndex("id_exchange"))));
		return result;
	}	
}
