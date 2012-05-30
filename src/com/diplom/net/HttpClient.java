package com.diplom.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.ByteArrayBuffer;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class HttpClient {		
	
	public static String SendHttpGet(String URL, String encoding) {		
		String resultString="";
		 try {	  
		  DefaultHttpClient httpclient = new DefaultHttpClient();
		  httpclient.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);	  
		  
		  HttpGet httpGetRequest=new HttpGet(URL);		  
		  HttpResponse response = (HttpResponse) httpclient.execute(httpGetRequest);
		  HttpEntity entity = response.getEntity();
		
		  if (entity != null) 
		  {		   
			   InputStream instream = entity.getContent();			   
			   Header contentEncoding = response.getFirstHeader("Content-Encoding");
			   if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) 
			    instream = new GZIPInputStream(instream);
			   resultString= convertStreamToString(instream, encoding);
			   Log.i("http", resultString);
			   instream.close();			   
		  }	   	   	
		 }
		 catch (Exception e)
		 {	  
			 e.printStackTrace();
		 }
		 return resultString;
	}
	public static void SendHttpGetAsync(final String URL, final Handler handler) {       
        Thread thread = new Thread() {
            @Override
            public void run() {                
            	String result=SendHttpGet(URL, null);                
                Message message = handler.obtainMessage(1, result);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
	public static void DownloadFromUrl(String URL, String fileName) {
        try {
                URL url = new URL(URL);
                File SDCardRoot = Environment.getExternalStorageDirectory();
                File file = new File(SDCardRoot, fileName);                
                URLConnection ucon = url.openConnection();
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                }
                FileOutputStream fos = new FileOutputStream(file, false);
                fos.write(baf.toByteArray());
                fos.close();                

        } catch (IOException e) {
                Log.d("ImageManager", "Error: " + e);
        }

	}			
	private static String convertStreamToString(InputStream is, String encoding) throws UnsupportedEncodingException 
	{
	 InputStreamReader isr=null;
	 if(encoding!=null)
		 isr=new InputStreamReader(is, encoding);
	 else  isr=new InputStreamReader(is);
	 BufferedReader reader = new BufferedReader(isr);
	 StringBuilder sb = new StringBuilder();	
	 String line = null;
	 try {
	  while ((line = reader.readLine()) != null) {
	   sb.append(line + "\n");
	  }
	 } catch (IOException e) {
	  e.printStackTrace();
	 } finally {
	  try {
	   is.close();
	  } catch (IOException e) {
	   e.printStackTrace();
	  }
	 }
	 return sb.toString();
	}
}