package com.moodletest;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class LoadUrl extends Activity {

	public String username, password, url, urlid, courseid, userid;
	public static Cookie cookie = null;
	public TextView assignment;
	boolean table_content;
	Elements heading, content;
	Element head1;
	org.jsoup.nodes.Document doc;
	WebView webView;
	public final Context cont =this;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment_webview);
		webView = (WebView) findViewById(R.id.assignment);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new HelloWebViewClient());
		CookieSyncManager.createInstance(webView.getContext());
		//webView.loadUrl("http://www.google.com");
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		url = intent.getStringExtra("url");
		urlid = intent.getStringExtra("urlid");
		courseid = intent.getStringExtra("courseid");
		userid = intent.getStringExtra("userid");
		webView.loadUrl(url);
		
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assignment_webview, menu);
		return true;
	}

	
	class HelloWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	    

	}
	
	
}
