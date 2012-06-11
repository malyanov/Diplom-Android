package com.diplom.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;

import com.diplom.basics.Quotation;
import com.diplom.basics.StochasticItem;
import com.diplom.settings.Settings;

/**
 *
 * @author Malyanov Dmitry
 */
public class AnalyseChart extends SurfaceView{
    public enum Mode{RSI(0), Stochastic(1), Momentum(2);
	    public final int id;
		Mode(int id){
			this.id=id;
		}
    };
    
    public List<Integer> RSIPoints=new ArrayList<Integer>();
    private List<Double> momentumPoints=new ArrayList<Double>();    
    public List<StochasticItem> stochasticPoints=null;
    private int scaleX, horShift;
    private Mode mode;
    private static DecimalFormat df=new DecimalFormat("0.00");
    
    public int PADDING=40;
    
    Paint white=new Paint();        
    Paint gray=new Paint();        
    Paint blue=new Paint();        
    Paint black=new Paint();
    Paint red=new Paint();
    Paint green=new Paint();
    Paint thickBlue=new Paint();
    
    public AnalyseChart(Context context) {
    	super(context);
    	setBackgroundColor(Color.WHITE);        
        mode=Settings.analyseMode;
        black.setColor(Color.BLACK);
        black.setStrokeWidth(2);
        white.setColor(Color.WHITE);
        white.setAntiAlias(true);        
        blue.setColor(Color.BLUE);
        blue.setAntiAlias(true);
        blue.setStrokeWidth(2);
        thickBlue.setColor(Color.BLUE);
        thickBlue.setAlpha(100);
        thickBlue.setStrokeWidth(0);
        thickBlue.setAntiAlias(true);
        gray.setColor(Color.GRAY);
        gray.setAlpha(70);
        red.setColor(Color.RED);      
        red.setAntiAlias(true);
        red.setFakeBoldText(true);
        red.setTextSize(20);
        green.setColor(0xff61965d);
        green.setStrokeWidth(2);
        green.setAntiAlias(true);
    }    
    public void setMode(Mode mode){
        this.mode=mode;
    }
    public void setParams(int horShift, int scaleX) {        
        this.horShift=horShift;
        this.scaleX=scaleX;
    }    
    public void setInputData(List<Quotation> quotes){
    	Analiser analiser=new Analiser(quotes);
        if(mode==Mode.RSI)
            RSIPoints=analiser.RSI();
        else if(mode==Mode.Stochastic) 
        	stochasticPoints=analiser.stochastic();
        else momentumPoints=analiser.momentum();
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {    	
    	super.onDraw(canvas);
    	PADDING=(int)(0.08*this.getWidth());
        
        String label="RSI";
        if(mode==Mode.RSI)
        	drawRSI(canvas);
        else if(mode==Mode.Stochastic){            
            drawStochastic(canvas);
            label="Stochastic";
        }else{
        	drawMomentum(canvas);
        	label="Momentum";
        }        
        canvas.drawText(label, 10, 20, red);
    }
    private void drawRSI(Canvas canvas){
    	int y1, y2, i;
    	double scaleFactor=this.getHeight()/100.0;    	
        for(i=0;i<100;i++){               
        	if(i%6==0)
        		canvas.drawLine(0, (int)Math.floor(i*scaleFactor), getWidth(), (int)Math.floor(i*scaleFactor), gray);          
        }
        for(i=0;i<RSIPoints.size();i++)
        	canvas.drawLine(horShift+i*scaleX, 0, horShift+i*scaleX, getHeight(), gray);
    	canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, thickBlue);        
        for(i=0;i<RSIPoints.size()-1;i++){
            y1=(int)(Math.abs(RSIPoints.get(i)*scaleFactor-this.getHeight()));
            y2=(int)(Math.abs(RSIPoints.get(i+1)*scaleFactor-this.getHeight()));
            canvas.drawLine(horShift+i*scaleX, y1, horShift+(i+1)*scaleX, y2, green);
        }
        canvas.drawRect(getWidth()-PADDING, 0, getWidth(), getHeight(), black);
        canvas.drawText("90", getWidth()-PADDING+2, (int)(getHeight()*0.1)+3, white);
        canvas.drawText("70", getWidth()-PADDING+2, (int)(getHeight()*0.3)+3, white);
        canvas.drawText("50", getWidth()-PADDING+2, getHeight()/2+3, white);        
        canvas.drawText("30", getWidth()-PADDING+2, (int)(getHeight()*0.7)+3, white);
        canvas.drawText("10", getWidth()-PADDING+2, (int)(getHeight()*0.9)+3, white);
    }
    private void drawMomentum(Canvas canvas){
    	int y1, y2, i;    	    	
//        for(i=0;i<10;i++){
//        	canvas.drawLine(0, (int)Math.floor(i*scaleFactor), getWidth(), (int)Math.floor(i*scaleFactor), gray);          
//        }
        double max, min;
        max=min=momentumPoints.get(0);
        for(i=0;i<momentumPoints.size();i++){
        	if(momentumPoints.get(i)>max)
        		max=momentumPoints.get(i);
        	if(momentumPoints.get(i)<min)
        		min=momentumPoints.get(i);
        	canvas.drawLine(horShift+i*scaleX, 0, horShift+i*scaleX, getHeight(), gray);
        }
        double delta=max-min;
        double scaleFactor=getHeight()/delta;
        for(i=0;i<momentumPoints.size()-1;i++){
            y1=(int)(Math.abs(momentumPoints.get(i)*scaleFactor-this.getHeight()/2));
            y2=(int)(Math.abs(momentumPoints.get(i+1)*scaleFactor-this.getHeight()/2));
            canvas.drawLine(horShift+i*scaleX, y1, horShift+(i+1)*scaleX, y2, green);
        }
        canvas.drawRect(getWidth()-PADDING, 0, getWidth(), getHeight(), black);
        int x=getWidth()-PADDING+2, y;        
        for(i=0;i<10;i++){
        	y=(int)(getHeight()*0.1*i)+10;
        	canvas.drawText(df.format(max-delta/10.0*i), x, y, white);
        	canvas.drawLine(0, y, getWidth()-PADDING, y, gray);
        }        
    }
    private void drawStochastic(Canvas canvas){
    	int y1, y2, y3, y4, height=this.getHeight(), i;
    	double scaleFactor=this.getHeight()/100.0;
    	for(i=0;i<100;i++){               
        	if(i%6==0)
        		canvas.drawLine(0, (int)Math.floor(i*scaleFactor), getWidth(), (int)Math.floor(i*scaleFactor), gray);          
        }
    	canvas.drawLine(0, getHeight()/2+(int)(25*scaleFactor), getWidth(), getHeight()/2+(int)(25*scaleFactor), thickBlue);
        canvas.drawLine(0, getHeight()/2-(int)(25*scaleFactor), getWidth(), getHeight()/2-(int)(25*scaleFactor), thickBlue);            
        for(i=0;i<stochasticPoints.size()-1;i++){
        	StochasticItem first=stochasticPoints.get(i), second=stochasticPoints.get(i+1);                                
            y1=(int)(Math.abs((Integer)first.getSlow()*scaleFactor-height));                
            y2=(int)(Math.abs((Integer)second.getSlow()*scaleFactor-height));
            y3=(int)(Math.abs((Integer)first.getFast()*scaleFactor-height));
            y4=(int)(Math.abs((Integer)second.getFast()*scaleFactor-height));                
            canvas.drawLine(horShift+i*scaleX, y1, horShift+(i+1)*scaleX, y2, red);                
            canvas.drawLine(horShift+i*scaleX, y3, horShift+(i+1)*scaleX, y4, blue);
        }
        canvas.drawRect(getWidth()-PADDING, 0, getWidth(), getHeight(), black);
        canvas.drawText("90", getWidth()-PADDING+2, (int)(getHeight()*0.1)+3, white);
        canvas.drawText("70", getWidth()-PADDING+2, (int)(getHeight()*0.3)+3, white);
        canvas.drawText("50", getWidth()-PADDING+2, getHeight()/2+3, white);        
        canvas.drawText("30", getWidth()-PADDING+2, (int)(getHeight()*0.7)+3, white);
        canvas.drawText("10", getWidth()-PADDING+2, (int)(getHeight()*0.9)+3, white);
    }
}

