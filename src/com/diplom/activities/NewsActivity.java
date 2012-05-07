package com.diplom.activities;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.diplom.basics.News;
import com.diplom.elements.NewsAdapter;
import com.diplom.net.HttpClient;

public class NewsActivity extends Activity {
	private String rssSource="http://rts.micex.ru/export/news.aspx";
	private ListView list;
	private List<News> newsList; 
	private SimpleDateFormat srcDateFormat=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);	
	private ProgressDialog progressDlg;
	private PopupWindow popup=null;

	private void showPopup(News n){
		if(popup!=null)
			popup.dismiss();
		popup=new PopupWindow(this);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupView = inflater.inflate(R.layout.news_popup, list, false);
		TextView dateView = (TextView) popupView.findViewById(R.id.PopupDate);
		dateView.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				popup.dismiss();
			}
		});
		dateView.setText(News.getDateFormat().format(n.getDate()));
		TextView textView = (TextView) popupView.findViewById(R.id.PopupText);		
		textView.setText(n.getDescription());
		TextView linkView = (TextView) popupView.findViewById(R.id.PopupLink);		
		linkView.setText(n.getLink());
		popup.setContentView(popupView);
		popup.showAtLocation(list, Gravity.CENTER, 0, 0);
		popup.update(400, 300);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.news);
		list=(ListView)findViewById(R.id.NewsList);		
        
        list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				News n=(News)view.getTag();				
				showPopup(n);
			}
			
		});
        progressDlg=ProgressDialog.show(this, "", "Загрузка новостей. Подождите пожалуйста...", true);
        final Handler loadHandler=new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        		super.handleMessage(msg);
        		list.setAdapter(new NewsAdapter(getApplication(), newsList.toArray(new News[newsList.size()])));        		
        		progressDlg.dismiss();        		
        	}
        };
		Thread thread=new Thread(){
				@Override
				public void run() {						
					super.run();
					try{						
						String rssData=HttpClient.SendHttpGet(rssSource, "windows-1251");
						if(rssData!=null){
							Document d=getXmlDocument(rssData);				
							XPath xpath = XPathFactory.newInstance().newXPath();
							NodeList titles=(NodeList)xpath.compile("//item/title/text()").evaluate(d, XPathConstants.NODESET), 
									dates=(NodeList)xpath.compile("//item/pubDate/text()").evaluate(d, XPathConstants.NODESET),
									descriptions=(NodeList)xpath.compile("//item/description/text()").evaluate(d, XPathConstants.NODESET), 
									guids=(NodeList)xpath.compile("//item/guid/text()").evaluate(d, XPathConstants.NODESET);
							newsList=new ArrayList<News>();
							for(int i=0;i<titles.getLength();i++){
								String date=dates.item(i).getNodeValue();
								newsList.add(new News(srcDateFormat.parse(date), 
										titles.item(i).getNodeValue(), descriptions.item(i).getNodeValue(), guids.item(i).getNodeValue()));
							}
							loadHandler.sendMessage(new Message());
						}
					}catch(Exception ex){
						ex.printStackTrace();
						loadHandler.sendMessage(new Message());
					}
				}			
		};
		thread.start();
	}
	private Document getXmlDocument(String str)
	{				
		StringReader sr = new StringReader(str);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;		
		try {
			builder = factory.newDocumentBuilder();
			return builder.parse(is);
		} catch (Exception ex) {			
			ex.printStackTrace();
		}
		return null;		
	}	
}
