/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.diplom.loaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.os.Environment;

import com.diplom.basics.Quotation;

/**
 *
 * @author Malyanov Dmitry
 */
public class FileParser
{
    public List<Quotation> data=new ArrayList<Quotation>();
    public String[] header;
    public FileParser()
    {        
    }
    public List<Quotation> readFile()
    {
        data.clear();
        File SDCardRoot = Environment.getExternalStorageDirectory();
        File file = new File(SDCardRoot, "data.txt");        
        Scanner sc=null;
        try {
			sc = new Scanner(file);		
	        String h=sc.nextLine(), buf, strdate="";
	        String[] splitbuf;
	        double open=0, close=0, high=0, low=0;
	        Date qdate=new Date(0, 0, 0, 0, 0, 0);
	        h=h.substring(1, h.length()-1);
	        header=h.split(">,<");
	        while (sc.hasNext())
	        {
	            buf=sc.nextLine();
	            splitbuf=buf.split(",");
	            for(int i=0;i<header.length;i++)
	            {
	                if(header[i].equals("OPEN")) open=Double.parseDouble(splitbuf[i]);
	                if(header[i].equals("CLOSE")) close=Double.parseDouble(splitbuf[i]);
	                if(header[i].equals("HIGH")) high=Double.parseDouble(splitbuf[i]);
	                if(header[i].equals("LOW")) low=Double.parseDouble(splitbuf[i]);
	                if(header[i].equals("DATE")) strdate=splitbuf[i];
	                if(header[i].equals("TIME"))
	                {
	                 strdate+=splitbuf[i];
	                 SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	                    try {
	                        qdate = formatter.parse(strdate);
	                    } catch (ParseException ex) {
	                        Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
	                    }
	                }                
	            }
	            data.add(new Quotation(open, close, low, high, qdate, Quotation.QuotationType.Hour_Bid));
	        }
        } catch (Exception e) {			
			e.printStackTrace();
		}
        return data;
    }
}
