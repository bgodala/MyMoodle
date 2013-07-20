package com.moodletest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.moodletest.Participant.NetworkHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ParticipantCourseList extends ListActivity {
	private static final long REPEAT_TIME = 1000 * 60*2;
	String[] lists;
	Intent intent;
	String category, username, password;

	String courseid, userid, token, str = "", fullname;
	ConnHandler handler = new ConnHandler();
	Intent intent1;
	Document dom;
	String name[], id, email,url,urlid,name_service;
	InputStream strem,strem_service;
	Button myButton;
	LinearLayout ll;// = (LinearLayout)findViewById(R.id.participant);
	LayoutParams lp;// = new LayoutParams(LayoutParams.MATCH_PARENT,
					// LayoutParams.WRAP_CONTENT);
	StringBuilder sb;
	XmlPullParserFactory factory,factory_service;// = XmlPullParserFactory.newInstance();
	XmlPullParser xpp,xpp_service;// = factory.newPullParser();
	ArrayList<String> namelist, emaillist, idlist;
	Context cont;
	ProgressBar bar;
	ListView listView;

	DBAdapter database;
	boolean isFirstTime=false;
	/*boolean alarmUp = (PendingIntent.getBroadcast(this, 0, 
	        new Intent("MyBroadcastReceiver.class"), 
	        PendingIntent.FLAG_NO_CREATE) != null);*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);
		intent = getIntent();
		lists = new String[2];
		lists[0] = "hello";
		lists[1] = "world";
		cont = this;
		bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setVisibility(View.VISIBLE);
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		progressBar.setIndeterminate(true);
		getListView().setEmptyView(progressBar);
		listView = (ListView) findViewById(android.R.id.list);
		listView.setEmptyView(progressBar);

		namelist = new ArrayList<String>();
		emaillist = new ArrayList<String>();
		idlist = new ArrayList<String>();

		// bar=(ProgressBar)this.findViewById(R.id.progressBar1);
		// bar.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		intent1 = new Intent(this, Send.class);
		courseid = intent.getStringExtra("courseid");
		token = intent.getStringExtra("token");
		userid = intent.getStringExtra("userid");
		fullname = intent.getStringExtra("fullname");
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		
		setTitle(fullname + "'s courses");
		//System.out.println("userid is " + userid);
		/*if (alarmUp)
		{
		    Log.d("myTag", "Alarm is already active");
		}else
			Log.d("myTag", "Alarm is nnot active");
			*/
		ll = (LinearLayout) findViewById(R.id.participant);
		lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		database = new DBAdapter(this);
		database.open();
		/*
		 * 				SharedPreferences.Editor editor = sharepref.edit();
				editor.putString("username", uname.getText().toString());
				editor.putString("password", pass.getText().toString());
				editor.commit();
		 * 
		 */
		if(database.queryTable("usr_assign_table_" + userid)==false){
			SharedPreferences.Editor editor = MainActivity.sharepref.edit();
			editor.putString("isfirsttime", "true");
			editor.commit();
			//System.out.println("creating table");
			database.assignTableCreate(userid);
			isFirstTime=true;
		}else{
			SharedPreferences.Editor editor = MainActivity.sharepref.edit();
			editor.putString("isfirsttime", "false");
			editor.commit();
			isFirstTime=false;
		}
		if (!database.queryTable("usr_course_table_" + userid))
			database.courseTableCreate(userid);
		database.close();
		System.out.println((PendingIntent.getBroadcast(cont, 0, new Intent(cont, MyBroadcastReceiver.class),
				PendingIntent.FLAG_NO_CREATE))!=null);
		new NetworkHandler().execute();

	}

	public void ConnHelper() {
		try {
			HttpResponse resp = handler.doPost(MainActivity.ipaddr
					+ "/webservice/rest/server.php?wstoken=" + token, "userid="
					+ userid + "&wsfunction=core_enrol_get_users_courses");

			HttpEntity enty = resp.getEntity();
			strem = enty.getContent();
			try {
				factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				xpp = factory.newPullParser();
				xpp.setInput(strem, null);
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {

					if (eventType == XmlPullParser.START_TAG) {
						// System.out.println("Entered while loop "+xpp.getName()+" "+xpp.getDepth()+" "+xpp.getAttributeCount()+" ");
						if (xpp.getDepth() == 4 && xpp.getName().equals("KEY")
								&& xpp.getAttributeCount() > 0) {
							// System.out.println("Entered depth 3");
							if (xpp.getAttributeValue(0).equals("id")) {
								xpp.next();
								xpp.next();
								idlist.add(xpp.getText());
								// System.out.println("fullname : "+
								// xpp.getText());
							} else if (xpp.getAttributeValue(0).equals(
									"fullname")) {
								xpp.next();
								xpp.next();
								namelist.add(xpp.getText());
								//System.out.println(xpp.getText());
							}/*
							 * else
							 * if(xpp.getAttributeValue(0).equals("email")){
							 * xpp.next(); xpp.next();
							 * emaillist.add(xpp.getText()); }
							 */
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
		if(isFirstTime==true){
		for (int i = 0; i < idlist.size(); i++) {
			
			// DefaultHttpClient client = new
			// MyHttpClient(getApplicationContext());
			try {
				HttpResponse resp_service = ConnHandler.doPost(MainActivity.ipaddr
						+ "/webservice/rest/server.php?wstoken=" + token,
						"wsfunction=core_course_get_contents&" + "courseid="
								+ idlist.get(i));

				HttpEntity enty_service = resp_service.getEntity();
				strem_service = enty_service.getContent();
	   	         
				try {
					factory_service = XmlPullParserFactory.newInstance();
					factory_service.setNamespaceAware(true);
					xpp_service = factory_service.newPullParser();
					xpp_service.setInput(strem_service,null);
					//xpp_service.setInput(new StringReader(convertStreamToString(strem_service)));
					int eventType = xpp_service.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {

						if (eventType == XmlPullParser.START_TAG) {

							if (xpp_service.getDepth() == 7
									&& xpp_service.getName().equals("KEY")
									&& xpp_service.getAttributeCount() > 0) {

								if (xpp_service.getAttributeValue(0).equals("id")) {
									xpp_service.next();
									xpp_service.next();
									urlid = xpp_service.getText();
								} else if (xpp_service.getAttributeValue(0).equals(
										"url")) {
									xpp_service.next();
									xpp_service.next();
									url = xpp_service.getText();
								} else if (xpp_service.getAttributeValue(0).equals(
										"name")) {
									xpp_service.next();
									xpp_service.next();
									name_service = xpp_service.getText();
								} else if (xpp_service.getAttributeValue(0).equals(
										"modname")) {
									xpp_service.next();
									xpp_service.next();
									System.out.println(xpp_service.getText());
									if (xpp_service.getText().equals("assign")) {
										try {
											database.open();
											if (database.assignTableGetContent(
													urlid, idlist.get(i), userid)
													.getCount() == 0) {
												database.assignTableInsertDetails(
														idlist.get(i), urlid, url,
														name_service, userid);
					
											}
											database.close();
										} catch (Exception e) {
											e.printStackTrace();
										}

									}
								}
							}

							// System.out.println("Start tag "+xpp_service.getName()+" "+xpp_service.getAttributeName(0)+" : "+xpp_service.getAttributeValue(0));
						}
						eventType = xpp_service.next();
					}
				} catch (Exception e) {
					System.out.println("Error in xml parsing");
					
					e.printStackTrace();
					
				}
				

			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}}
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		Intent intent1 = new Intent(this, ChoiceMenu.class);
		intent1.putExtra("courseid", idlist.get(position));
		intent1.putExtra("token", token);
		intent1.putExtra("userid", userid);
		intent1.putExtra("username", username);
		intent1.putExtra("password", password);
		startActivity(intent1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class NetworkHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ConnHelper();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			name = new String[namelist.size()];
			database.open();
			for (int i = 0; i < namelist.size(); i++) {
				name[i] = namelist.get(i);
				if (database.courseTableGetContent(userid, idlist.get(i)).getCount() == 0) {
					database.courseTableInsertDetails(idlist.get(i), name[i],
							userid);
					//System.out.println("inserted");

				}
			}
			database.close();
			//if(isFirstTime==true)
			if((PendingIntent.getBroadcast(cont, 0, new Intent(cont, MyBroadcastReceiver.class),PendingIntent.FLAG_NO_CREATE))==null){
				System.out.println("setting up alaram");
				setAlaram(cont);
			}
			bar.setVisibility(View.GONE);
			// setListAdapter(new
			// ArrayAdapter<String>(cont,R.layout.activity_participant_list,name));
			listView.setAdapter(new ArrayAdapter<String>(cont,
					R.layout.activity_participant_list, name));
			System.out.println("Checking broad cast is set or not ");
			System.out.println((PendingIntent.getBroadcast(cont, 0, new Intent(cont, MyBroadcastReceiver.class),
					PendingIntent.FLAG_NO_CREATE))!=null);

		}
	}
	
	public void setAlaram(Context context) {
		AlarmManager service = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, MyBroadcastReceiver.class);
		i.putExtra("username", username);
		i.putExtra("password", password);
		i.putExtra("token", token);
		i.putExtra("userid", userid);
		i.putExtra("ipaddr", MainActivity.ipaddr);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 30);
		// Start 30 seconds after boot completed
		//
		// Fetch every 30 seconds
		// InexactRepeating allows Android to optimize the energy consumption
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				cal.getTimeInMillis(), REPEAT_TIME, pending);

		// service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
		// REPEAT_TIME, pending);

	}
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (TestService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences.Editor editor = MainActivity.sharepref.edit();
		editor.putString("lock", "false");
		editor.commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences.Editor editor = MainActivity.sharepref.edit();
		editor.putString("lock", "true");
		editor.commit();
	}
}