package com.diplom.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;

import com.diplom.activities.MainActivity;
import com.diplom.basics.Instrument;
import com.diplom.basics.Quotation;
import com.diplom.basics.Quotation.QuotationType;
import com.diplom.loaders.FileParser;
import com.diplom.loaders.MICEX_Loader;
import com.diplom.loaders.RTS_Loader;
import com.diplom.settings.Settings;

public class Chart extends SurfaceView
{
	public final int MAX_SCALE=60, MIN_SCALE=10;
    public final int SCALE_DELTA=5, MOVE_DELTA=2;
    
    public int PADDING=40;
    
    public enum Modes{
    	CURVES(0), CANDLES(1), BARS(2);
    	public final int id;
    	Modes(int id){
    		this.id=id;
    	}
    	public static Modes getById(int id)
    	{
    		for (Modes mode : values()) {
				if(mode.id==id)
					return mode;
			}
    		return null;
    	}
    }; 
    
    boolean firstRun;
    List<Quotation> qList;
    private int curScaleX=MIN_SCALE;
    private int curScaleY=(int)(0.4*MIN_SCALE);    
    private int curPos;
    double maxValue, minValue;
    List<Quotation> points;
    private String info;    
    Modes mode;
    QuotationType bid;
    
    Paint white=new Paint();        
    Paint gray=new Paint();        
    Paint blue=new Paint();    
    Paint black=new Paint();
    Paint red=new Paint();    
    //drag flags
    boolean isDragging;
    Point startPoint;
    int moveUpCounter;
    int moveDownCounter;
    int moveLeftCounter;
    int moveRightCounter;    
    boolean isFullscreen;    
    AnalyseChart analyseChart;    
    
    private float mScaleFactor = 1.f;
	private ScaleGestureDetector mScaleDetector;
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        mScaleFactor *= detector.getScaleFactor();
	        mScaleFactor = Math.max(1.f, Math.min(mScaleFactor, 6.0f));
	        curScaleX=(int) (10*mScaleFactor);
            curScaleY=(int) (0.4 * curScaleX);
	        invalidate();
	        return true;
	    }
	}
    
    public Chart(Context context, AnalyseChart analyseChart, boolean isFullscreen) {
		super(context);
		this.setBackgroundColor(Color.WHITE);		
        //init();    
        setOnTouchListener(onTouch);        
        black.setColor(Color.BLACK);
        black.setStrokeWidth(2);
        white.setColor(Color.WHITE);
        white.setAntiAlias(true);        
        blue.setColor(Color.BLUE);
        blue.setStrokeWidth(2);
        blue.setAntiAlias(true);        
        gray.setColor(Color.GRAY);
        red.setColor(Color.RED);      
        red.setAntiAlias(true);        
        red.setFakeBoldText(true);
        this.isFullscreen=isFullscreen;
        this.analyseChart=analyseChart;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
    
    public void updateLastValue(double value)
    {
    	qList.get(qList.size()-1).currentValue=value;
    	invalidate();
    }
    public void init(List<Quotation> list)
    {
    	firstRun=true;        
        curPos=0;
        maxValue=0;
        minValue=0;
        points=new ArrayList<Quotation>();
        info="";        
        mode=Settings.chartMode;
        bid=Settings.bidType;
        //drag flags
        isDragging=false;
        startPoint=new Point();
        moveUpCounter=0;
        moveDownCounter=0;
        moveLeftCounter=0;
        moveRightCounter=0;        
        isFullscreen=true;        
        qList=list;
        invalidate();
    }    
    public void increaseScale()
    {
        if(curScaleX+SCALE_DELTA<MAX_SCALE)
        {
            curScaleX+=SCALE_DELTA;
            curScaleY=(int) (0.4 * curScaleX);
            invalidate();
        }
    }
    public void moveLeft()
    {
        if(curPos-MOVE_DELTA>=0)
        {
            curPos-=MOVE_DELTA;            
            invalidate();
        }
    }
    public void moveRight()
    {
        int vertTicks=(this.getWidth()-PADDING)/curScaleX;
        if(curPos+MOVE_DELTA+vertTicks<qList.size())
        {
            curPos+=MOVE_DELTA;            
            invalidate();
        }
    }
    public void changeViewMode(Modes mode)
    {
        this.mode=mode;
        invalidate();
    }
    public void setBidType(QuotationType type)
    {
    	bid=type;
    	invalidate();
    }
    public void decreaseScale()
    {
        if(curScaleX-SCALE_DELTA>MIN_SCALE)
        {
            curScaleX-=SCALE_DELTA; 
            curScaleY=(int) (0.4 * curScaleX);
            invalidate();
        }
    }  
    private OnTouchListener onTouch=new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			mScaleDetector.onTouchEvent(event);
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				isDragging=true;
				startPoint.x=(int)event.getX();
				startPoint.y=(int)event.getY();
				Log.i("down", "action down");
			}
			else if(event.getAction()==MotionEvent.ACTION_MOVE)
			{
				if(isDragging)
				{
					if(Math.abs(startPoint.x-(int)event.getX())>Math.abs(startPoint.y-(int)event.getY()))
					{
						if(startPoint.x>event.getX())
							moveRight();
						else moveLeft();
					}
					else
					{
						if(startPoint.y<event.getY())
							decreaseScale();
						else increaseScale();
					}
					Log.i("move", "action move x="+event.getX()+"; y="+event.getY());
				}
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				isDragging=false;
				Log.i("up", "action up");
			}
			return isFullscreen;
		}
	};
	public static String getDateString(Date date)
	{
		int hour=date.getHours();		 
		int day=date.getDate();
		int month=date.getMonth();
		int year=date.getYear()%100;
		String timeStr=""+hour+"÷. ";
		String result=""+day+"."+month+"."+year;
		//if()
		result=timeStr+result;
		return  result;
	}
    public void drawGrid(Canvas g)
    {      
      if(firstRun)
      {
          firstRun=false;
          PADDING=(int)(0.08*this.getWidth());          
      }
      int graphWidth=getWidth()-PADDING;
      int graphHeight=getHeight()-PADDING;
      int vertTicks=graphWidth/curScaleX,          
          horTicks=graphHeight/curScaleY;      
      //      
      g.drawRect(0, graphHeight, this.getWidth(), this.getHeight(), black);
      g.drawRect(graphWidth, 0, this.getWidth(), this.getHeight(),black);      
      //
      if(qList==null||qList.size()==0)
    	  return;
      prepare(vertTicks+2);
      double valuesHeight=maxValue-minValue;
      double scaleFactor=graphHeight/valuesHeight;
      double horTickValue=valuesHeight/horTicks;
      int horShift=vertTicks*curScaleX-graphWidth;
      int vertShift=graphHeight-horTicks*curScaleY;
      int x,y;      
      analyseChart.setParams(horShift, curScaleX);
      analyseChart.setInputData(points);
      int tail=0;
      for(int i=0;i<points.size();i++)
      {
          x=horShift+i*curScaleX;
          if(i%(int)(MAX_SCALE/curScaleX+2)==0)
          {
            g.drawText(getDateString(points.get(i).dateTime), x-50, graphHeight+PADDING/2+3, white);
            tail=5;
          }
          else tail=0;
          g.drawLine(x, 0, x, graphHeight+tail, gray);          
      }    
      tail=0;
      String strValue;
      for(int j=0;j<horTicks;j++)
      {
          y=vertShift+j*curScaleY;
          if(j%10==0)
          {            
            tail=5;
            strValue=String.valueOf(maxValue-j*horTickValue);
            if(strValue.length()>7)
                strValue=strValue.substring(0, 7);
            g.drawText(strValue, graphWidth+(int)(0.2*PADDING), y, white);
          }
          else tail=0;
          g.drawLine(0, y, graphWidth+tail, y, gray);          
      }
      double openValue=0, closeValue=0, highValue=0, lowValue=0;      
      switch(mode)
      {
          case CURVES:              
              for(int i=0;i<points.size()-1;i++)
              {                  
                  g.drawLine(horShift+i*curScaleX, (int)Math.abs((points.get(i).closeValue-minValue)*scaleFactor-graphHeight)+vertShift, 
                               horShift+(i+1)*curScaleX, (int)Math.abs((points.get(i+1).closeValue-minValue)*scaleFactor-graphHeight)+vertShift, black);
              }
              break;
          case CANDLES:
              int cX,cY, cWidth, cHeight;
              for(int i=0;i<points.size();i++)
              {
                  g.drawLine(horShift+i*curScaleX, (int)Math.abs((points.get(i).highValue-minValue)*scaleFactor-graphHeight)+vertShift, 
                          horShift+i*curScaleX, (int)Math.abs((points.get(i).lowValue-minValue)*scaleFactor-graphHeight)+vertShift, black);
                  openValue=points.get(i).openValue;
                  closeValue=points.get(i).closeValue;
                  cX=horShift+i*curScaleX-2;
                  Paint candlePaint;
                  if(openValue>closeValue)
                  {           
                	  candlePaint=black;
                      cY=(int)Math.ceil(Math.abs((openValue-minValue)*scaleFactor-graphHeight))+vertShift;
                  }
                  else
                  {
                      candlePaint=white;
                      cY=(int)Math.ceil(Math.abs((closeValue-minValue)*scaleFactor-graphHeight))+vertShift;
                  }                  
                  cWidth=4;
                  cHeight=Math.abs((int)((openValue-closeValue)*scaleFactor));
                  g.drawRect(cX, cY, cX+cWidth, cY+cHeight, candlePaint);
                  black.setStyle(Style.STROKE);
                  g.drawRect(cX, cY, cX+cWidth, cY+cHeight, black);
                  black.setStyle(Style.FILL_AND_STROKE);
              }
              break;
          case BARS:
              int vpos=0;
              int barLevelWidth=curScaleX/2-2;
              for(int i=0;i<points.size();i++)
              {                  
                  openValue=points.get(i).openValue;
                  closeValue=points.get(i).closeValue;
                  lowValue=points.get(i).lowValue;
                  highValue=points.get(i).highValue;
                  Paint barPaint;
                  if(openValue<closeValue)
                      barPaint=blue;
                  else barPaint=black;
                  g.drawLine(horShift+i*curScaleX, (int)Math.abs((highValue-minValue)*scaleFactor-graphHeight)+vertShift, 
                          horShift+i*curScaleX, (int)Math.abs((lowValue-minValue)*scaleFactor-graphHeight)+vertShift, barPaint);
                  vpos=(int)Math.abs((openValue-minValue)*scaleFactor-graphHeight)+vertShift;
                  g.drawLine(horShift+i*curScaleX-barLevelWidth,vpos , horShift+i*curScaleX, vpos, barPaint);
                  vpos=(int)Math.abs((closeValue-minValue)*scaleFactor-graphHeight)+vertShift;
                  g.drawLine(horShift+i*curScaleX,vpos , horShift+i*curScaleX+barLevelWidth, vpos, barPaint);
              }
              break;
      }      
      g.drawText(Settings.instrumentCode, 20, 20, red);
      int curValue=(int)Math.abs((qList.get(qList.size()-1).closeValue-minValue)*scaleFactor-graphHeight)+vertShift;//change  ot curent      
      g.drawLine(0, curValue, this.getWidth(), curValue, red);      
      g.drawRect(graphWidth, curValue, this.getWidth(), curValue+15, white);      
      g.drawText(String.valueOf(qList.get(qList.size()-1).closeValue), this.getWidth()-PADDING+5, curValue+12, red);
      
    }
    private void prepare(int num){
        Quotation q=qList.get(curPos);
        maxValue=minValue=q.lowValue;
        points.clear();        
        if(curPos+num>qList.size())
            num=qList.size()-curPos;
        for(int i=curPos;i<curPos+num;i++)
        {
            q=qList.get(i);
            if(q.lowValue>maxValue) maxValue=q.lowValue;
            if(q.lowValue<minValue) minValue=q.lowValue;
            points.add(q);
        }        
        //info="max="+maxValue+"; min="+minValue;        
    }        
    public void setInfo(String info){
    	this.info=info;
    }
    @Override
    protected void onDraw(Canvas canvas) {    	
    	super.onDraw(canvas);
    	drawGrid(canvas);
    }  
}
