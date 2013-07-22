package com.moodletest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AssignmentContent extends Activity {

	public String username, password, url, urlid, courseid, userid;
	public TextView assignment;
	DBAdapter database;
	boolean table_content;
	Elements heading, content;
	Element head1;
	org.jsoup.nodes.Document doc;
	WebView webView;
	ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment_content);
		bar=(ProgressBar)findViewById(R.id.progressBar1);
		bar.setVisibility(ProgressBar.VISIBLE);
		table_content = false;
		database = new DBAdapter(this);
		database.open();
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		url = intent.getStringExtra("url");
		urlid = intent.getStringExtra("urlid");
		courseid = intent.getStringExtra("courseid");
		userid = intent.getStringExtra("userid");
		// username="bhargav";
		// password="xmEnEvolution@!)*!((!21081991";
		webView = (WebView) findViewById(R.id.assignment);
		//webView.getSettings().setJavaScriptEnabled(true);
		if (database.assignTableGetContent(urlid, courseid, userid)
				.getString(0) == null) {
			System.out.println("Entered null block");
			table_content = false;
		} else {
			table_content = true;
		}
		if (table_content) {
			//assignment.setText(Html.fromHtml(database.assignTableGetContent(
					//urlid, courseid, userid).getString(0)));
			bar.setVisibility(ProgressBar.GONE);
			webView.loadData(database.assignTableGetContent(
					urlid, courseid, userid).getString(0), "text/html", "utf-8");
		} else {
			new NetworkHandler().execute();
		}

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

	public void ConnHelper() {

		try {
			HttpClient httpclient2 = new DefaultHttpClient();
			HttpPost request = new HttpPost(MainActivity.ipaddr
					+ "/login/index.php");
			request.setHeader("Proxy-Authorization", "Basic cHJha2FzaDppaXRo");
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

			// HttpResponse resp3 = doPost(ipaddr +
			// "/login/index.php","username="+URLEncoder.encode(user.toString(),
			// "utf-8")+"&password="+URLEncoder.encode(password.toString(),
			// "utf-8"));
			HttpEntity respentity = resp3.getEntity();
			System.out.println(convertStreamToString(respentity.getContent()));
			Header header[] = resp3.getAllHeaders();
			if (header != null) {
				for (Header tempheader : header) {

					// cookieManager.setCookie(ipaddr + "/login/index.php",
					// tempheader.getValue());;
					System.out.println("temp header is " + tempheader.getName()
							+ " : " + tempheader.getValue());
				}
			}

			HttpGet httpget = new HttpGet(url);
			// Accept JSON

			// httpget.addHeader("accept", "application/json");
			// httpget.addHeader("Proxy-Authorization","Basic cHJha2FzaDppaXRo");
			// Execute the request
			HttpResponse response;
			try {
				response = httpclient2.execute(httpget);

				//int status = response.getStatusLine().getStatusCode();
				// System.out.println("status code is" +status);
				// System.out.println(response.toString());
				// System.out.println(response.getAllHeaders().toString());
				Header[] head = response.getAllHeaders();
				for (int i = 0; i < head.length; i++) {
					// System.out.println("header "+head[i].getName()+" : header value "+head[i].getValue()+" close ");
				}
				HttpEntity entity = response.getEntity();

				InputStream instream = entity.getContent();

				String result = convertStreamToString(instream);
				doc = Jsoup.parse(result);
				// Elements content=doc.getElementsByClass("topics");

				// System.out.println(Html.fromHtml(head1.toString()+content.toString()));
				// org.jsoup.nodes.Element contentdiv = content.get(0);
				// System.out.println(new
				// org.jsoup.examples.HtmlToPlainText().getPlainText(contentdiv));
				// System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			content = doc.getElementsByClass("no-overflow");
			heading = doc.getElementsByClass("main");
			head1 = heading.get(0);
			// assignment.setText(Html.fromHtml(head1.toString()
			// + content.toString()));
			bar.setVisibility(ProgressBar.GONE);
			webView.loadData(head1.toString() + content.toString(),
					"text/html", "utf-8");
			database.assignTableInsertContent(urlid, courseid, userid,
					head1.toString() + content.toString());
			database.close();
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

}
