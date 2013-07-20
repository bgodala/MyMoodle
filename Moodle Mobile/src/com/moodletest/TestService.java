package com.moodletest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.moodletest.Assignments.NetworkHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;

public class TestService extends Service {

	private NotificationManager mNM;
	public static final String PREFS_NAME = "MyPrefsFile";
	
	XmlPullParserFactory factory_service;// = XmlPullParserFactory.newInstance();
	XmlPullParser xpp_service;// = factory.newPullParser();
	// ArrayList<String> courseidlist,coursenamelist,assidlist,assnamelist;
	String courseid, urlid, token, url, name, userid;
	DBAdapter database;
	InputStream strem_service;
	ArrayList<String> courseidlist;

	private int NOTIFICATION = 11134;
	public String ipaddr;
	String user, password;
	SharedPreferences sharepref;
	public boolean isFirstTime = false;
	
	File test_file;
	FileOutputStream fos;
	StringBuilder sb;
	byte[] bytes;
	boolean lock=true;
	final Context cont=this;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		System.out.println("service started");
		//android.os.Debug.waitForDebugger();
		test_file = new File(Environment.getExternalStorageDirectory()
		         +File.separator+"sample.txt");
		try{
		fos=new FileOutputStream(test_file);
		byte[] data = new byte[3];
		data[0]='h';
		data[1]='e';
		data[2]='l';
		fos.write(data);
		fos.close();
		fos.flush();
		}catch(Exception e)
		{
			System.out.println("error in creating file");
			e.printStackTrace();
		}
		sharepref = getSharedPreferences(PREFS_NAME, 0);
		courseidlist = new ArrayList<String>();
		// System.out.println(sharepref.getString("isfirsttime", "failed"));
		/*if(sharepref.getString("lock", "false").equals("true")){
			System.out.println("locked");
			return super.onStartCommand(intent, flags, startId);
		}*/
		if (sharepref.getString("isfirsttime", "false").equals("true")) {
			isFirstTime = true;
			SharedPreferences.Editor editor = sharepref.edit();
			editor.putString("isfirsttime", "false");
			editor.commit();
			// System.out.println("Changed value success");
		}

			
		user = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		token = intent.getStringExtra("token");
		courseid = intent.getStringExtra("courseid");
		userid = intent.getStringExtra("userid");
		ipaddr = intent.getStringExtra("ipaddr");
		database = new DBAdapter(this);
		database.open();
		// database.assignTableCreate(userid);
		if (!database.queryTable("usr_assign_table_" + userid))
			database.assignTableCreate(userid);
		Cursor courseidList = database.courseTableGetContent(userid);
		while (courseidList.isAfterLast() == false) {
			courseidlist.add(courseidList.getString(0));
			// System.out.println("course id is : " +
			// courseidList.getString(0));
			courseidList.moveToNext();
		}
		database.close();

		new NetworkHandler().execute();

		return super.onStartCommand(intent, flags, startId);
	}

	public void ConnHelper() {

		for (int i = 0; i < courseidlist.size(); i++) {
			courseid = courseidlist.get(i);
			// DefaultHttpClient client = new
			// MyHttpClient(getApplicationContext());
			
			try {
				HttpResponse resp_service = ConnHandler.doPost(ipaddr
						+ "/webservice/rest/server.php?wstoken=" + token,
						"wsfunction=core_course_get_contents&" + "courseid="
								+ courseidlist.get(i));

				HttpEntity enty_service = resp_service.getEntity();
				bytes = EntityUtils.toByteArray(enty_service);
				//System.out.println(new String(bytes,"UTF-8"));
				//strem_service = enty_service.getContent();
	   	         
					factory_service = XmlPullParserFactory.newInstance();
					factory_service.setNamespaceAware(false);
					xpp_service = factory_service.newPullParser();
					//xpp_service.setInput(strem_service,null);
					File file =new File(Environment.getExternalStorageDirectory(),courseidlist.get(i)+"_"+i+".txt");
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(bytes);
					fos.flush();
					fos.close();
					xpp_service.setInput(new StringReader(new String(bytes,"UTF-8")));
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
									name = xpp_service.getText();
								} else if (xpp_service.getAttributeValue(0).equals(
										"modname")) {
									xpp_service.next();
									xpp_service.next();
									if (xpp_service.getText().equals("assign")) {
										try {
											database.open();
											if (database.assignTableGetContent(
													urlid, courseidlist.get(i), userid)
													.getCount() == 0) {
												System.out.println("urlid and course id are : "+urlid+" "+courseidlist.get(i));
												try {
													System.out.println(new String(bytes,"UTF-8"));
												} catch (UnsupportedEncodingException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
												}
												database.assignTableInsertDetails(
														courseidlist.get(i), urlid, url,
														name, userid);
												if (isFirstTime == false) {
													showNotification("New Assignment "
															+ name);
												}
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
				//} catch (Exception e) {

					
				//}
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error in xml parsing");

				e.printStackTrace();
				break;
			}
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting. We put an icon in the
		// status bar.
		// showNotification("Assignment Notifications Started");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void showNotification(String str) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = str;

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher,
				text, System.currentTimeMillis());
		// notification.audioStreamType=Notification.DEFAULT_SOUND;
		notification.defaults = Notification.DEFAULT_ALL
				| Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.FLAG_AUTO_CANCEL;
		// The PendingIntent to launch our activity if the user selects this
		// notification
		Intent i = new Intent(this, AssignmentContent.class);

		i.putExtra("username", user);
		i.putExtra("password", password);
		i.putExtra("token", token);
		i.putExtra("courseid", courseid);
		i.putExtra("userid", userid);
		i.putExtra("url", url);
		i.putExtra("urlid", urlid);
		i.putExtra("ipaddr", ipaddr);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, str, text, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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

	class NetworkHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			ConnHelper();

			return null;
		}

	}
	public HttpResponse doPost(String url, String c) throws ClientProtocolException, IOException 
    {
        HttpClient httpclient = new MyHttpClient(cont);
        HttpPost request = new HttpPost(url);
        StringEntity s = new StringEntity(c);
        //System.out.println(s.getContent());
        //System.out.println(s.toString());
        //s.setContentEncoding("UTF-8");
        s.setContentType("application/x-www-form-urlencoded");
        //request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(s);
        //request.addHeader("accept", "application/json");
        

        return httpclient.execute(request);
}
	private String inputStreamToString(InputStream is) {
	    String s = "";
	    String line = "";
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    
	    // Read response until the end
	    try {
			while ((line = rd.readLine()) != null) { s += line; }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // Return full string
	    System.out.println(s);
	    return s;
	}
}
