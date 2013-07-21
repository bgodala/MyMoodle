package com.moodletest;

import java.io.IOException;
import java.net.URLEncoder;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class UserInfo extends Activity {
	Document dom;
	XPath xpath;
	XPathExpression expr;
	XPathFactory xPathfactory;
	ConnHandler handler = new ConnHandler();
	TextView usrinfo;
	String token;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		Intent intent = getIntent();
	    token = intent.getStringExtra("token");
	    
	    usrinfo = (TextView)findViewById(R.id.textView1);
	    new NetworkHandler().execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_info, menu);
		return true;
	}
	
	class NetworkHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
		    try {
				HttpResponse resp = ConnHandler.doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"wsfunction=moodle_webservice_get_siteinfo");
				dom = handler.returnDom(resp);
	            
		    } catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			usrinfo.setText("Full Name : "+handler.getKeyValue("fullname", dom)+"\nName : "+handler.getKeyValue("lastname", dom)+"\nUser ID : "+handler.getKeyValue("userid", dom));
		}
		
		
	}

}
