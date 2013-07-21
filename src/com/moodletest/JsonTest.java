package com.moodletest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class JsonTest extends ListActivity {

	String username, password, token, courseid, id, url, name, userid, type;

	TextView assignment;
	ConnHandler handler;
	XmlPullParserFactory factory;// = XmlPullParserFactory.newInstance();
	XmlPullParser xpp;// = factory.newPullParser();
	InputStream strem;
	ArrayList<String> namelist, urllist, idlist;
	Context cont;
	ListView listView;
	ProgressBar bar;
	DBAdapter database;
	JSONObject jsonObj;
	String result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);

		// setContentView(R.layout.activity_assignments);
		handler = new ConnHandler();
		namelist = new ArrayList<String>();
		urllist = new ArrayList<String>();
		idlist = new ArrayList<String>();
		bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(android.R.id.list);
		cont = this;
		Intent intent = getIntent();
		token = intent.getStringExtra("token");
		courseid = intent.getStringExtra("courseid");
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		userid = intent.getStringExtra("userid");
		type = intent.getStringExtra("type");
		// System.out.println(token + courseid + username + password + userid);

		database = new DBAdapter(this);
		new NetworkHandler().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assignments, menu);
		return true;
	}

	public void ConnHelper() {
		try {
			HttpResponse resp = ConnHandler.doPost_json(MainActivity.ipaddr
					+ "/webservice/rest/server.php?moodlewsrestformat=json","wstoken=" + token
					+"&wsfunction=core_course_get_contents&" + "courseid="
							+ courseid);

			HttpEntity enty = resp.getEntity();
			//byte[] bytes = EntityUtils.toByteArray(enty);
			//System.out.println(new String(bytes,"UTF-8"));
			 strem = enty.getContent();
			 result = convertStreamToString(strem);
			 System.out.println(result);
			// System.out.println("assignments file "+strem.toString());
			jsonObj = new JSONObject(result);
			System.out.println(jsonObj.toString());
			/*try {
				factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(false);
				xpp = factory.newPullParser();
				// xpp.setInput(strem, null);
				xpp.setInput(new StringReader(new String(bytes, "UTF-8")));
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {

					if (eventType == XmlPullParser.START_TAG) {
						if (xpp.getDepth() == 7 && xpp.getName().equals("KEY")
								&& xpp.getAttributeCount() > 0) {
							if (xpp.getAttributeValue(0).equals("id")) {
								xpp.next();
								xpp.next();
								id = xpp.getText();
							} else if (xpp.getAttributeValue(0).equals("url")) {
								xpp.next();
								xpp.next();
								url = xpp.getText();
							} else if (xpp.getAttributeValue(0).equals("name")) {
								xpp.next();
								xpp.next();
								name = xpp.getText();
							} else if (xpp.getAttributeValue(0).equals(
									"modname")) {
								xpp.next();
								xpp.next();
								if (xpp.getText().equals(type)) {
									// System.out.println(id + " " + name + " "
									// + url);
									namelist.add(name);
									idlist.add(id);
									urllist.add(url);

									if (type.equals("assign")) {
										database.open();
										if (database.assignTableGetContent(id,
												courseid, userid).getCount() == 0) {
											database.assignTableInsertDetails(
													courseid, id, url, name,
													userid);
										}
										database.close();
									} else if (type.equals("url")) {
										while (xpp.getDepth() >= 7) {
											if (xpp.getDepth()==10 && eventType == XmlPullParser.START_TAG) {
												if (xpp.getName().equals("KEY")
														&& xpp.getAttributeCount() > 0) {
													if (xpp.getAttributeValue(0)
															.equals("fileurl")) {
														xpp.next();
														xpp.next();
														// name = xpp.getText();
														System.out.println(xpp
																.getText());
														break;
													}
												}
											}
											xpp.next();
										}
									}
								}
							}
						}
					}
					eventType = xpp.next();
				}

			} catch (Exception e) {
				System.out.println("Error in xml parsing");
				e.printStackTrace();
			}*/

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		Intent intent1 = null;
		if (type.equals("assign")) {
			intent1 = new Intent(this, AssignmentContent.class);
		} else if (type.equals("forum")) {
			intent1 = new Intent(this, ForumsTopics.class);
		} else {
			intent1 = new Intent(this, AssignmentWebview.class);
		}
		intent1.putExtra("urlid", idlist.get(position));
		intent1.putExtra("url", urllist.get(position));
		intent1.putExtra("courseid", courseid);
		intent1.putExtra("username", username);
		intent1.putExtra("password", password);
		intent1.putExtra("userid", userid);
		intent1.putExtra("token", token);
		startActivity(intent1);

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
			String[] list = new String[namelist.size()];
			for (int i = 0; i < namelist.size(); i++) {
				list[i] = namelist.get(i);
			}
			bar.setVisibility(View.GONE);
			listView.setAdapter(new ArrayAdapter<String>(cont,
					R.layout.activity_participant_list, list));

		}

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

	public HttpResponse doPost(String url, String c)
			throws ClientProtocolException, IOException {
		HttpClient httpclient = new MyHttpClient(cont);
		HttpPost request = new HttpPost(url);
		StringEntity s = new StringEntity(c);
		// System.out.println(s.getContent());
		// System.out.println(s.toString());
		// s.setContentEncoding("UTF-8");
		s.setContentType("application/x-www-form-urlencoded");
		// request.setHeader("Content-Type",
		// "application/x-www-form-urlencoded");
		request.setEntity(s);
		// request.addHeader("accept", "application/json");

		return httpclient.execute(request);
	}
	
	public static String convertStreamToString(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    char[] bytes1=new char[1024];
	    int size=1024;
	    try {
	        while (true) {
	        	size= reader.read(bytes1,0,bytes1.length);
	        	if(size!=-1)
	        		sb.append(bytes1);
	        	else
	        		break;
	            
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
