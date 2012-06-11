package com.diplom.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;

import com.diplom.basics.BollingerBands;
import com.diplom.basics.Quotation;
import com.diplom.settings.Settings;

public class Chart extends SurfaceView{
	public final int MAX_SCALE=60, MIN_SCALE=10;
    public final int SCALE_DELTA=5, MOVE_DELTA=3;    
    public int PADDING=40;
    
    private static final SimpleDateFormat sdf=new SimpleDateFormat("HHч. dd.MM.yy");
    
    public enum Modes{
    	CURVES(0, "Ломаная"), CANDLES(1, "Свечи"), BARS(2, "Бары");
    	public final int id;    	
    	public final String locName;
    	Modes(int id, String locName){
    		this.id=id;
    		this.locName=locName;
    	}
    	public static Modes getById(int id){
    		for (Modes mode : values()) {
				if(mode.id==id)
					return mode;
			}
    		return null;
    	}
    }; 
    
    boolean firstRun;
    private List<Quotation> qList;
    private int curScaleX=MIN_SCALE;
    private int curScaleY=(int)(0.4*MIN_SCALE);    
    private int curPos;
    double maxValue, minValue;
    private List<Quotation> points;      
    private Modes mode;
    
    private Paint white=new Paint();        
    private Paint gray=new Paint();        
    private Paint blue=new Paint();    
    private Paint black=new Paint();
    private Paint red=new Paint();    
    private Paint green=new Paint();
    //drag flags
    boolean isDragging;
    private Point startPoint;        
    boolean isFullscreen;    
    private AnalyseChart analyseChart;    
    
    private float mScaleFactor = 1.f;
	private ScaleGestureDetector mScaleDetector;	
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
	    @Override
	    public boolean onScale(ScaleGestureDetector detector){
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
        setOnTouchListener(onTouch);        
        black.setColor(Color.BLACK);
        black.setStrokeWidth(2);
        white.setColor(Color.WHITE);
        white.setAntiAlias(true);        
        blue.setColor(Color.BLUE);
        blue.setStrokeWidth(2);
        blue.setAntiAlias(true);        
        gray.setColor(Color.GRAY);
        gray.setAlpha(70);
        green.setColor(Color.GREEN);
        red.setColor(Color.RED);      
        red.setAntiAlias(true);        
        red.setFakeBoldText(true);
        red.setTextSize(15);
        this.isFullscreen=isFullscreen;
        this.analyseChart=analyseChart;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}    
    public void updateLastValue(double value){
    	qList.get(qList.size()-1).currentValue=value;
    	invalidate();
    }
    public void init(List<Quotation> list){
    	firstRun=true;        
        curPos=0;
        maxValue=0;
        minValue=0;
        points=new ArrayList<Quotation>();        
        mode=Settings.chartMode;        
        //drag flags
        isDragging=false;
        startPoint=new Point();                
        isFullscreen=true;        
        qList=list;
        invalidate();
    }    
    public void moveLeft(){
        if(curPos-MOVE_DELTA>=0){
            curPos-=MOVE_DELTA;            
            invalidate();
        }
    }
    public void moveRight(){
        int vertTicks=(this.getWidth()-PADDING)/curScaleX;
        if(curPos+MOVE_DELTA+vertTicks<qList.size()){
            curPos+=MOVE_DELTA;            
            invalidate();
        }
    }
    public void changeViewMode(Modes mode){
        this.mode=mode;
        invalidate();
    }    
    private OnTouchListener onTouch=new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			mScaleDetector.onTouchEvent(event);
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				isDragging=true;
				startPoint.x=(int)event.getX();
				startPoint.y=(int)event.getY();				
			}
			if(event.getAction()==MotionEvent.ACTION_MOVE){
				if(isDragging){
					if(Math.abs(startPoint.x-(int)event.getX())>Math.abs(startPoint.y-(int)event.getY())){
						if(startPoint.x>event.getX())
							moveRight();
						else moveLeft();
					}					
				}
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
				isDragging=false;
			return true;
		}
	};	
    public void drawGrid(Canvas g){      
      if(firstRun){
          firstRun=false;
          PADDING=(int)(0.08*this.getWidth());          
      }
      int chartWidth=getWidth()-PADDING;
      int chartHeight=getHeight()-PADDING;
      int vertTicks=chartWidth/curScaleX,          
          horTicks=chartHeight/curScaleY;
      if(qList==null||qList.size()==0)
    	  return;
      prepare(vertTicks+2);
      double valuesHeight=maxValue-minValue;
      double scaleFactor=chartHeight/valuesHeight;
      double horTickValue=valuesHeight/horTicks;
      int horShift=vertTicks*curScaleX-chartWidth;
      int vertShift=chartHeight-horTicks*curScaleY;
      int x,y;      
      analyseChart.setParams(horShift, curScaleX);
      analyseChart.setInputData(points);      
      for(int i=0;i<points.size();i++){
          x=horShift+i*curScaleX;          
          if(x<=chartWidth)
        	  g.drawLine(x, 0, x, chartHeight, gray);          
      }
      for(int j=0;j<horTicks;j++){
          y=vertShift+j*curScaleY;                    
          g.drawLine(0, y, chartWidth, y, gray);          
      }
      double openValue=0, closeValue=0, highValue=0, lowValue=0;
      if(Settings.bollingerBands)
    	  drawBollingerBands(g, horShift, vertShift, chartHeight, scaleFactor);
      if(Settings.ma)
    	  drawMA(g, horShift, vertShift, chartHeight, scaleFactor);
      switch(mode){
          case CURVES:
              for(int i=0;i<points.size()-1;i++){                  
                  g.drawLine(horShift+i*curScaleX, (int)Math.abs((points.get(i).closeValue-minValue)*scaleFactor-chartHeight)+vertShift, 
                               horShift+(i+1)*curScaleX, (int)Math.abs((points.get(i+1).closeValue-minValue)*scaleFactor-chartHeight)+vertShift, black);
              }              
              break;
          case CANDLES:
              int cX,cY, cWidth, cHeight;
              for(int i=0;i<points.size();i++){
                  g.drawLine(horShift+i*curScaleX, (int)Math.abs((points.get(i).highValue-minValue)*scaleFactor-chartHeight)+vertShift, 
                          horShift+i*curScaleX, (int)Math.abs((points.get(i).lowValue-minValue)*scaleFactor-chartHeight)+vertShift, black);
                  openValue=points.get(i).openValue;
                  closeValue=points.get(i).closeValue;
                  cX=horShift+i*curScaleX-2;
                  Paint candlePaint;
                  if(openValue>closeValue){           
                	  candlePaint=black;
                      cY=(int)Math.ceil(Math.abs((openValue-minValue)*scaleFactor-chartHeight))+vertShift;
                  }
                  else{
                      candlePaint=white;
                      cY=(int)Math.ceil(Math.abs((closeValue-minValue)*scaleFactor-chartHeight))+vertShift;
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
              for(int i=0;i<points.size();i++){                  
                  openValue=points.get(i).openValue;
                  closeValue=points.get(i).closeValue;
                  lowValue=points.get(i).lowValue;
                  highValue=points.get(i).highValue;
                  Paint barPaint;
                  if(openValue<closeValue)
                      barPaint=blue;
                  else barPaint=black;
                  g.drawLine(horShift+i*curScaleX, (int)Math.abs((highValue-minValue)*scaleFactor-chartHeight)+vertShift, 
                          horShift+i*curScaleX, (int)Math.abs((lowValue-minValue)*scaleFactor-chartHeight)+vertShift, barPaint);
                  vpos=(int)Math.abs((openValue-minValue)*scaleFactor-chartHeight)+vertShift;
                  g.drawLine(horShift+i*curScaleX-barLevelWidth,vpos , horShift+i*curScaleX, vpos, barPaint);
                  vpos=(int)Math.abs((closeValue-minValue)*scaleFactor-chartHeight)+vertShift;
                  g.drawLine(horShift+i*curScaleX,vpos , horShift+i*curScaleX+barLevelWidth, vpos, barPaint);
              }
              break;
      }
	  //    
	  g.drawRect(0, chartHeight, this.getWidth(), this.getHeight(), black);
	  g.drawRect(chartWidth, 0, this.getWidth(), this.getHeight(),black);      
	  //	  
      for(int i=0;i<points.size();i++){
          x=horShift+i*curScaleX;
          if(i%(int)(MAX_SCALE/curScaleX+2)==0){
            g.drawText(sdf.format(points.get(i).dateTime), x-50, chartHeight+PADDING/2+3, white);
            g.drawLine(x, chartHeight, x, chartHeight+5, green);
          }
      }   
      String strValue;
      for(int j=0;j<horTicks;j++){
          y=vertShift+j*curScaleY;
          if(j%10==0){
            strValue=String.valueOf(maxValue-j*horTickValue);
            if(strValue.length()>7)
                strValue=strValue.substring(0, 7);
            g.drawText(strValue, chartWidth+(int)(0.2*PADDING), y, white);
            g.drawLine(chartWidth, y, chartWidth+5, y, green);
          }          
      }
      g.drawText(Settings.instrumentCode, 20, 20, red);
      int curValue=(int)Math.abs((qList.get(qList.size()-1).closeValue-minValue)*scaleFactor-chartHeight)+vertShift;//change  ot curent      
      g.drawLine(0, curValue, this.getWidth(), curValue, red);      
      g.drawRect(chartWidth-20, curValue, this.getWidth(), curValue+20, white);
      red.setStyle(Style.STROKE);
      g.drawRect(chartWidth-20, curValue, this.getWidth(), curValue+20, red);
      red.setStyle(Style.FILL_AND_STROKE);
      g.drawText(String.valueOf(qList.get(qList.size()-1).closeValue), chartWidth-15, curValue+15, red);      
    }
    public void drawBollingerBands(Canvas g, int horShift, int vertShift, int chartHeight, double scaleFactor){    	
        List<BollingerBands> list=(new Analiser(points)).BollingerBands(15);
        int x1, x2, y1, y2;
        for(int i=0;i<list.size()-1;i++){
        	BollingerBands p1=list.get(i), p2=list.get(i+1);
        	x1=horShift+i*curScaleX;
        	x2=horShift+(i+1)*curScaleX;
        	y1=(int)Math.abs((p1.getMl()-minValue)*scaleFactor-chartHeight)+vertShift;
        	y2=(int)Math.abs((p2.getMl()-minValue)*scaleFactor-chartHeight)+vertShift;
        	g.drawLine(x1, y1, x2, y2, green);
        	
        	g.drawLine(x1, (int)(y1+p1.getBl()), x2, (int)(y2+p2.getBl()), blue);
        	g.drawLine(x1, (int)(y1+p1.getTl()), x2, (int)(y2+p2.getTl()), blue);
        }
    }
    public void drawMA(Canvas g, int horShift, int vertShift, int chartHeight, double scaleFactor){    	
        List<Integer> list=(new Analiser(points)).MA();
        int y1,y2;
        for(int i=0;i<list.size()-1;i++){        	
        	y1=(int)Math.abs((list.get(i)-minValue)*scaleFactor-chartHeight)+vertShift;
        	y2=(int)Math.abs((list.get(i+1)-minValue)*scaleFactor-chartHeight)+vertShift;
        	g.drawLine(horShift+i*curScaleX, y1, horShift+(i+1)*curScaleX, y2, red);        	
        }
    }
    private void prepare(int num){
        Quotation q=qList.get(curPos);
        maxValue=minValue=q.lowValue;
        points.clear();        
        if(curPos+num>qList.size())
            num=qList.size()-curPos;
        for(int i=curPos;i<curPos+num;i++){
            q=qList.get(i);
            if(q.lowValue>maxValue) maxValue=q.lowValue;
            if(q.lowValue<minValue) minValue=q.lowValue;
            points.add(q);
        }        
    }
    @Override
    protected void onDraw(Canvas canvas) {    	
    	super.onDraw(canvas);
    	drawGrid(canvas);
    }  
}
