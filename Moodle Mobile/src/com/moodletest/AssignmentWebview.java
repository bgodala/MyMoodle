package com.moodletest;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class AssignmentWebview extends Activity {

	public String username, password, url, urlid, courseid, userid;
	public static Cookie cookie = null;
	public TextView assignment;
	boolean table_content;
	Elements heading, content;
	Element head1;
	org.jsoup.nodes.Document doc;
	WebView webView;
	public final Context cont =this;
	List<Cookie> cookies;
	Cookie sessionCookie;
	CookieManager cookieManager;
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CookieSyncManager.getInstance().stopSync();
		SharedPreferences.Editor editor = MainActivity.sharepref.edit();
		editor.putString("lock", "false");
		editor.commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CookieSyncManager.getInstance().startSync();
		SharedPreferences.Editor editor = MainActivity.sharepref.edit();
		editor.putString("lock", "true");
		editor.commit();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment_webview);
		webView = (WebView) findViewById(R.id.assignment);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new HelloWebViewClient());
		CookieSyncManager.createInstance(webView.getContext());
		cookieManager = CookieManager.getInstance();
		//webView.loadUrl("http://www.google.com");
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		url = intent.getStringExtra("url");
		urlid = intent.getStringExtra("urlid");
		courseid = intent.getStringExtra("courseid");
		userid = intent.getStringExtra("userid");
		webView.loadUrl(url);
		//new NetworkHandler().execute();
		
	}

	public void ConnHelper() {

		try {
			DefaultHttpClient httpclient2 = new DefaultHttpClient();
			HttpPost request = new HttpPost(MainActivity.ipaddr
					+ "/login/index.php");
			//request.setHeader("Proxy-Authorization", "Basic cHJha2FzaDppaXRo");
			StringEntity s = new StringEntity("username="
					+ URLEncoder.encode(username, "utf-8") + "&password="
					+ URLEncoder.encode(password, "utf-8"));
			System.out.println(s.getContent());
			// s.setContentEncoding("UTF-8");
			s.setContentType("application/x-www-form-urlencoded");

			// request.setHeader("Content-Type",
			// "application/x-www-form-urlencoded");
			request.setEntity(s);
			// request.addHeader("accept", "application/json");

			HttpResponse resp3 = httpclient2.execute(request);
			cookies = httpclient2.getCookieStore().getCookies();
			System.out.println("got cookies of size : "+cookies.size());
			for (int i = 0; i < cookies.size(); i++) {
			    cookie = cookies.get(i);
			}
			//webView.setC
			sessionCookie = cookie;
			
			//CookieSyncManager.createInstance(webView.getContext());
			//cookieManager = CookieManager.getInstance();
			if (sessionCookie != null) {
			    cookieManager.removeSessionCookie();
			    String cookieString = sessionCookie.getName() + "=" + sessionCookie.getValue() + "; domain=" + sessionCookie.getDomain();
			    cookieManager.setCookie("moodle.iith.ac.in", cookieString);
			    //CookieSyncManager.getInstance().sync();
			    System.out.println("sync done");
			    //Thread.sleep(2000);
			    SystemClock.sleep(500);
			    for(long i=0;i<100000l;i++){
			    		System.out.println("test");
			    	
			    }
			}
			//webView.loadUrl(url);
			//webView.loadData("<body>Hello world</body>", "text/html", "utf-8");
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assignment_webview, menu);
		return true;
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
			webView.loadUrl(url);
			//webView.loadUrl("http://www.google.com");
			//content = doc.getElementsByClass("no-overflow");
			//heading = doc.getElementsByClass("main");
			//head1 = heading.get(0);
			//assignment.setText(Html.fromHtml(head1.toString()
			//		+ content.toString()));
			//database.assignTableInsertContent(urlid, courseid, userid,
			//		head1.toString() + content.toString());
		}

	}
	class HelloWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	    

	}
	
	
}
