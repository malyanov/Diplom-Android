package com.diplom.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;

import com.diplom.basics.Quotation;
import com.diplom.settings.Settings;

/**
 *
 * @author Malyanov Dmitry
 */
public class AnalyseChart extends SurfaceView{
    public enum Mode{RSI(0), Stochastic(1);
	    public final int id;
		Mode(int id){
			this.id=id;
		}
    };
    
    public List<Integer> drawPoints=new ArrayList<Integer>();    
    public HashMap<Integer, Integer> stochasticPoints=new HashMap<Integer, Integer>();
    int scaleX, horShift;
    Mode mode;
    
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
        thickBlue.setStrokeWidth(0);
        thickBlue.setAntiAlias(true);
        gray.setColor(Color.GRAY);
        red.setColor(Color.RED);      
        red.setAntiAlias(true);
        red.setFakeBoldText(true);
        green.setColor(0xff61965d);
        green.setStrokeWidth(2);
        green.setAntiAlias(true);
    }    
    public void setMode(Mode mode)
    {
        this.mode=mode;
    }
    public void setParams(int horShift, int scaleX) {        
        this.horShift=horShift;
        this.scaleX=scaleX;
    }    
    public void setInputData(List<Quotation> quotes)
    {        
        if(mode==Mode.RSI)
            drawPoints=RSI(quotes);
        else stochasticPoints=Stochastic(quotes);
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {    	
    	super.onDraw(canvas);
    	PADDING=(int)(0.08*this.getWidth());
    	double scaleFactor=this.getHeight()/100.0;
        int height=this.getHeight();        
        int y1, y2, y3, y4;
        int i=0;
        for(i=0;i<100;i++)
        {               
        	if(i%6==0)
        		canvas.drawLine(0, (int)Math.floor(i*scaleFactor), getWidth(), (int)Math.floor(i*scaleFactor), gray);          
        }
        for(i=0;i<drawPoints.size();i++)
        {
        	canvas.drawLine(horShift+i*scaleX, 0, horShift+i*scaleX, getHeight(), gray);
        }
        if(mode==Mode.RSI)
        {
            canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, thickBlue);
            canvas.drawText("RSI", 10, 20, red);
            for(i=0;i<drawPoints.size()-1;i++)
            {
                y1=(int)(Math.abs(drawPoints.get(i)*scaleFactor-height));
                y2=(int)(Math.abs(drawPoints.get(i+1)*scaleFactor-height));
                canvas.drawLine(horShift+i*scaleX, y1, horShift+(i+1)*scaleX, y2, green);
            }
        }
        else
        {
        	i=0;
            Iterator it = stochasticPoints.entrySet().iterator();
            Map.Entry pairs1, pairs2;
            canvas.drawText("Stochastic", 10, 20, red);
            canvas.drawLine(0, getHeight()/2+(int)(25*scaleFactor), getWidth(), getHeight()/2+(int)(25*scaleFactor), thickBlue);
            canvas.drawLine(0, getHeight()/2-(int)(25*scaleFactor), getWidth(), getHeight()/2-(int)(25*scaleFactor), thickBlue);
            if(it.hasNext())
                pairs1 = (Map.Entry)it.next();
            else pairs1=null;
            while (it.hasNext()) {
                pairs2=(Map.Entry)it.next();                
                y1=(int)(Math.abs((Integer)pairs1.getValue()*scaleFactor-height));                
                y2=(int)(Math.abs((Integer)pairs2.getValue()*scaleFactor-height));
                y3=(int)(Math.abs((Integer)pairs1.getKey()*scaleFactor-height));
                y4=(int)(Math.abs((Integer)pairs2.getKey()*scaleFactor-height));                
                canvas.drawLine(horShift+i*scaleX, y1, horShift+(i+1)*scaleX, y2, red);                
                canvas.drawLine(horShift+i*scaleX, y3, horShift+(i+1)*scaleX, y4, blue);
                pairs1=pairs2;
                i++;
            }
        }
        canvas.drawRect(getWidth()-PADDING, 0, getWidth(), getHeight(), black);
        canvas.drawText("90", getWidth()-PADDING+2, (int)(getHeight()*0.1)+3, white);
        canvas.drawText("70", getWidth()-PADDING+2, (int)(getHeight()*0.3)+3, white);
        canvas.drawText("50", getWidth()-PADDING+2, getHeight()/2+3, white);        
        canvas.drawText("30", getWidth()-PADDING+2, (int)(getHeight()*0.7)+3, white);
        canvas.drawText("10", getWidth()-PADDING+2, (int)(getHeight()*0.9)+3, white);
    }    
    public List<Integer> RSI(List<Quotation> quotes)
    {
        List<Integer> result=new ArrayList<Integer>();
        double closeValue1=0, closeValue2;
        double U, D, Up=1, Dp=1, RS, RSI;
        double Su, Sd, Sup=1, Sdp=1;
        double a=2.0/(quotes.size()+1.0);
        for(int i=0;i<quotes.size()-1;i++)
        {
            closeValue1=quotes.get(i).closeValue;
            closeValue2=quotes.get(i+1).closeValue;
            if(closeValue2>closeValue1)
            {
                U=closeValue2-closeValue1;
                D=0;
            }
            else
            {
                U=0;
                D=closeValue1-closeValue2;
            }
            Su=a*Up+(1.0-a)*Sup;
            Up=U;
            Sup=Su;
            Sd=a*Dp+(1.0-a)*Sdp;
            Dp=D;
            Sdp=Sd;
            if(Sd>0)
            {
                RS=Su/Sd;
                RSI=100.0-100.0/(1.0+RS);
            }
            else RSI=100.0;
            result.add((int)Math.floor(RSI));
        }
        return result;
    }    
    public HashMap<Integer, Integer> Stochastic(List<Quotation> quotes)
    {
        HashMap<Integer, Integer> result=new HashMap<Integer, Integer>();
        int periodsNum=4;
        double periodHigh, periodLow, closeValue;
        double a=2.0/(quotes.size()+1.0);
        double fast, slow, slowPrev=1.0;
        for(int i=0;i<quotes.size()-1;i++)
        {
            closeValue=quotes.get(i).closeValue;
            if(i<periodsNum)
            {
                periodHigh=getMaxClose(quotes.subList(0, periodsNum));
                periodLow=getMinClose(quotes.subList(0, periodsNum));
            }
            else
            {
                periodHigh=getMaxClose(quotes.subList(i-periodsNum, i));
                periodLow=getMinClose(quotes.subList(i-periodsNum, i));
            }
            fast=(closeValue - periodLow) / (periodHigh - periodLow) * 100.0;
            slow=a*fast+(1.0-a)*slowPrev;
            slowPrev=slow;
            result.put((int)Math.floor(fast), (int)Math.floor(slow));
        }
        return result;
    }
    private double getMinClose(List<Quotation> values)
    {
        double min=values.get(0).closeValue, v;
        for(int i=1;i<values.size();i++)
        {
            v=values.get(i).closeValue;
            if(v<min)
                min=v;
        }
        return min;
    }
    private double getMaxClose(List<Quotation> values)
    {
        double max=values.get(0).closeValue, v;
        for(int i=1;i<values.size();i++)
        {
            v=values.get(i).closeValue;
            if(v>max)
                max=v;
        }
        return max;
    }
}

