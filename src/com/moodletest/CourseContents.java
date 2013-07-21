package com.moodletest;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CourseContents extends Activity {

	public String courseid, userid,token,shortname,fullname,startdate;
	
	DBAdapter database;
	XmlPullParserFactory xmlfactory;// = XmlPullParserFactory.newInstance();
	XmlPullParser xpp;// = factory.newPullParser();
	InputStream strem;
	
	ProgressBar bar;
	Date strtdate;
	TextView courseidText,startdateText,shortnameText,fullnameText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_contents);
		Intent intent = getIntent();
		token = intent.getStringExtra("token");
		courseid = intent.getStringExtra("courseid");
		userid = intent.getStringExtra("userid");
		bar = (ProgressBar)findViewById(R.id.progressBar1);
		bar.setVisibility(View.VISIBLE);
		courseidText = (TextView)findViewById(R.id.courseid);
		startdateText = (TextView)findViewById(R.id.startdate);
		fullnameText = (TextView)findViewById(R.id.fullname);
		shortnameText = (TextView)findViewById(R.id.shortname);
		courseidText.setVisibility(View.GONE);
		startdateText.setVisibility(View.GONE);
		fullnameText.setVisibility(View.GONE);
		shortnameText.setVisibility(View.GONE);
		new NetworkHandler().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_contents, menu);
		return true;
	}

	public void ConnHelper() {

		try {
			HttpResponse resp = ConnHandler.doPost(MainActivity.ipaddr
					+ "/webservice/rest/server.php?wstoken=" + token,
					"wsfunction=core_course_get_courses&options[ids][0]="+courseid);

			HttpEntity enty = resp.getEntity();
			strem = enty.getContent();
			try {
				xmlfactory = XmlPullParserFactory.newInstance();
				xmlfactory.setNamespaceAware(true);
				xpp = xmlfactory.newPullParser();
				xpp.setInput(strem, null);
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {

					if (eventType == XmlPullParser.START_TAG) {
						// System.out.println("Entered while loop "+xpp.getName()+" "+xpp.getDepth()+" "+xpp.getAttributeCount()+" ");
						if (xpp.getDepth() == 4 && xpp.getName().equals("KEY")
								&& xpp.getAttributeCount() > 0) {
							// System.out.println("Entered depth 3");
							if (xpp.getAttributeValue(0).equals("shortname")) {
								xpp.next();
								xpp.next();
								System.out.println("shortname : "+
								xpp.getText());
								shortname=xpp.getText();
							} else if (xpp.getAttributeValue(0).equals(
									"fullname")) {
								xpp.next();
								xpp.next();
								System.out.println("fullname is : "+xpp.getText());
								fullname=xpp.getText();
							} 
							else if (xpp.getAttributeValue(0).equals(
									"startdate")) {
								xpp.next();
								xpp.next();
								
								startdate=xpp.getText();
								strtdate = new Date(Long.parseLong(startdate)*1000);
								System.out.println("startdate is : "+strtdate.toString());
								break;
							} 
						}

						// System.out.println("Start tag "+xpp.getName()+" "+xpp.getAttributeName(0)+" : "+xpp.getAttributeValue(0));
					}
					eventType = xpp.next();
				}
			} catch (Exception e) {
				System.out.println("Error in xml parsing");
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class NetworkHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			ConnHelper();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			bar.setVisibility(View.GONE);
			courseidText.setVisibility(View.VISIBLE);
			startdateText.setVisibility(View.VISIBLE);
			fullnameText.setVisibility(View.VISIBLE);
			shortnameText.setVisibility(View.VISIBLE);
			fullnameText.setText("Course fullname : "+fullname);
			shortnameText.setText("Course shortname :"+shortname);
			courseidText.setText("Course id : "+courseid);
			startdateText.setText("Start Date is : "+strtdate.toGMTString());
		}

	}

}
