package com.moodletest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MainActivity extends Activity {
	public static final String PREFS_NAME = "MyPrefsFile";
	String url;
	String username = null, passwrd = null, fullname;
	String token, userid;
	Document dom, dom1;
	DocumentBuilder builder;
	DocumentBuilderFactory factory;
	EditText uname, pass;
	XPath xpath;
	XPathExpression expr;
	XPathFactory xPathfactory;
	ConnHandler handler = new ConnHandler();
	Context cont=this;
	EditText webserver, extwebservice;
	public static String ipaddr;
	String webservice;
	public static SharedPreferences sharepref;
	ProgressDialog progress;
	XmlPullParserFactory xmlfactory;// = XmlPullParserFactory.newInstance();
	XmlPullParser xpp;// = factory.newPullParser();
	InputStream strem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View inflatedView = getLayoutInflater().inflate(R.layout.settings_menu,
				null);

		webserver = (EditText) inflatedView.findViewById(R.id.servertext);
		extwebservice = (EditText) inflatedView.findViewById(R.id.servicetext);
		setContentView(R.layout.activity_main);
		sharepref = getSharedPreferences(PREFS_NAME, 0);
		setTitle("My Moodle Application");
		cont = this;
		username = sharepref.getString("username", null);
		passwrd = sharepref.getString("password", null);
		ipaddr = sharepref.getString("server",
				"http://moodle.iith.ac.in/moodle");
		webservice = sharepref.getString("service", "moodle_mobile");
		System.out.println(webservice);
		webserver.setText(ipaddr);
		extwebservice.setText(webservice);
		uname = (EditText) findViewById(R.id.EditText01);
		pass = (EditText) findViewById(R.id.editText2);
		Button login = (Button) findViewById(R.id.button1);
		if (username != null && passwrd != null) {
			uname.setText(username);
			pass.setText(passwrd);
			/*
			 * try { url = ipaddr +
			 * "/login/token.php?username="+URLEncoder.encode(username,
			 * "utf-8")+"&password="+URLEncoder.encode(passwrd,
			 * "utf-8")+"&service=moodle_mobile_app"; token =
			 * (String)doGet(url).get("token"); /*HttpResponse resp =
			 * doPost(ipaddr + "/webservice/rest/server.php?wstoken="+token,
			 * "userid=3&wsfunction=moodle_enrol_get_users_courses"); HttpEntity
			 * enty = resp.getEntity(); InputStream strem = enty.getContent();
			 * factory = DocumentBuilderFactory.newInstance(); builder =
			 * factory.newDocumentBuilder(); dom = builder.parse(strem);
			 * xPathfactory = XPathFactory.newInstance(); xpath =
			 * xPathfactory.newXPath(); expr =
			 * xpath.compile("//KEY[@name=\"fullname\"]"); NodeList nl2 =
			 * (NodeList) expr.evaluate(dom, XPathConstants.NODESET);
			 * System.out.
			 * println("node list of expr  "+nl2.item(0).getChildNodes
			 * ().item(0).getChildNodes().getLength()); if(dom != null){
			 * System.out.println("dom is not null"); }
			 * 
			 * NodeList nl = dom.getElementsByTagName("KEY"); Node nod =
			 * nl.item(1); System.out.println("test dom ");
			 * System.out.println(nod.hasAttributes());
			 * System.out.println(dom.getFirstChild
			 * ().getChildNodes().item(1).getChildNodes
			 * ().item(1).getChildNodes()
			 * .item(1).getChildNodes().item(0).getChildNodes
			 * ().item(0).getNodeValue());
			 * System.out.println(getElementValue(nl.item(0))); HttpResponse
			 * resp1 = doPost(ipaddr +
			 * "/webservice/rest/server.php?wstoken="+token
			 * ,"wsfunction=moodle_webservice_get_siteinfo"); dom1 =
			 * handler.returnDom(resp1); userid=handler.getKeyValue("userid",
			 * dom1); Log.d("USERID",userid);
			 * 
			 * 
			 * } catch (Exception e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } sendmessage(token,userid);
			 */

		}

		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new NetworkHandler().execute();

			}
		});

	}

	public void sendmessage(String str, String userid) {
		Intent intent = new Intent(this, ParticipantCourseList.class);
		intent.putExtra("token", str);
		intent.putExtra("userid", userid);
		intent.putExtra("fullname", fullname);
		intent.putExtra("username", username);
		intent.putExtra("password", passwrd);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			settings();
			return true;
		case R.id.proxy:
			proxy();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void dispErrorMsg() {
		Toast.makeText(cont, "Invalid username or password", Toast.LENGTH_LONG)
				.show();
	}

	public static JSONObject doGet(String url) {
		JSONObject json = null;
		HttpClient httpclient = new DefaultHttpClient();
		// Prepare a request object
		HttpGet httpget = new HttpGet(url);
		// Accept JSON
		httpget.addHeader("accept", "application/json");
		httpget.addHeader("Proxy-Authorization", "Basic cHJha2FzaDppaXRo");
		// Execute the request
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);

			int status = response.getStatusLine().getStatusCode();
			if (status != 200) {
				// dispErrorMsg();
				return null;

			}
			System.out.println("status code is" + status);
			System.out.println(response.toString());
			System.out.println(response.getAllHeaders().toString());
			Header[] head = response.getAllHeaders();
			for (int i = 0; i < head.length; i++) {
				System.out.println("header " + head[i].getName()
						+ " : header value " + head[i].getValue() + " close ");
			}
			HttpEntity entity = response.getEntity();

			InputStream instream = entity.getContent();

			String result = convertStreamToString(instream);
			System.out.println(result);
			// construct a JSON object with result
			json = new JSONObject(result);
			// Closing the input stream will trigger connection release
			instream.close();
			// Get the response entity

			// System.out.println(json.get("token"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Return the json
		return json;
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

	public static HttpResponse doPost(String url, String c)
			throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost request = new HttpPost(url);
		request.setHeader("Proxy-Authorization", "Basic cHJha2FzaDppaXRo");
		StringEntity s = new StringEntity(c);
		System.out.println(s.getContent());
		// s.setContentEncoding("UTF-8");
		s.setContentType("application/x-www-form-urlencoded");

		// request.setHeader("Content-Type",
		// "application/x-www-form-urlencoded");
		request.setEntity(s);
		// request.addHeader("accept", "application/json");

		return httpclient.execute(request);
	}

	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public void onStop() {
		super.onStop();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		Log.d("onstopn", "entered on stop");
		/*
		 * SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		 * SharedPreferences.Editor editor = settings.edit();
		 * editor.putString("username", uname.getText().toString());
		 * editor.putString("password", pass.getText().toString());
		 * 
		 * 
		 * // Commit the edits! editor.commit();
		 */

	}

	public void ConnHelper() {
		try {
			HttpResponse resp = doPost(MainActivity.ipaddr
					+ "/webservice/rest/server.php?wstoken=" + token,
					"wsfunction=core_webservice_get_site_info");

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
						if (xpp.getDepth() == 3 && xpp.getName().equals("KEY")
								&& xpp.getAttributeCount() > 0) {
							// System.out.println("Entered depth 3");
							if (xpp.getAttributeValue(0).equals("username")) {
								xpp.next();
								xpp.next();
								// System.out.println("fullname : "+
								// xpp.getText());
							} else if (xpp.getAttributeValue(0).equals(
									"fullname")) {
								xpp.next();
								xpp.next();
								fullname = xpp.getText();
								System.out.println(xpp.getText());
							} else if (xpp.getAttributeValue(0)
									.equals("userid")) {
								xpp.next();
								xpp.next();
								userid = xpp.getText();
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
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// LayoutParams params = progress.getWindow().getAttributes();
			// params.width = LayoutParams.FILL_PARENT;
			// progress.getWindow().setAttributes((android.view.WindowManager.LayoutParams)
			// params);
			progress = ProgressDialog.show(cont, "Loading",
					"Please wait....Logging in", true);

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			Editable user = uname.getText();
			Editable password = pass.getText();
			Editable server = webserver.getText();
			Editable service = extwebservice.getText();
			System.out.println(user.toString() + " " + password.toString());
			try {
				SharedPreferences.Editor editor = sharepref.edit();
				editor.putString("username", uname.getText().toString());
				editor.putString("password", pass.getText().toString());
				editor.commit();
				username = uname.getText().toString();
				passwrd = pass.getText().toString();

				// ipaddr=server.toString();
				// webservice=service.toString();
				System.out.println("ipaddress in doinbackground " + ipaddr);
				System.out
						.println("webservice in doinbackground " + webservice);
				url = ipaddr + "/login/token.php?username="
						+ URLEncoder.encode(user.toString(), "utf-8")
						+ "&password="
						+ URLEncoder.encode(password.toString(), "utf-8")
						+ "&service=" + webservice;
				JSONObject obj = doGet(url);
				if (!obj.has("token")) {
					System.out.println("in token verification");
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dispErrorMsg();
							// TODO Auto-generated method stub

						}
					});

					return null;
				} else {
					token = (String) obj.get("token");
					System.out.println(obj.toString());
					// HttpResponse resp1 = doPost(ipaddr +
					// "/webservice/rest/server.php?wstoken="+token,"wsfunction=core_webservice_get_site_info");
					// dom1 = handler.returnDom(resp1);
					// userid=handler.getKeyValue("userid", dom1);
					ConnHelper();
					Log.d("USERID", userid);
					sendmessage(token, userid);
				}

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
			progress.dismiss();
		}

	}

	public void settings() {
		final Dialog settings = new Dialog(this);
		settings.setContentView(R.layout.settings_menu);
		settings.setTitle("Settings");

		// set the custom dialog components - text, image and button
		TextView server = (TextView) settings.findViewById(R.id.server);
		server.setText("Settings");
		final EditText url = (EditText) settings.findViewById(R.id.servertext);
		final EditText service = (EditText) settings
				.findViewById(R.id.servicetext);
		url.setText(sharepref.getString("server",
				"http://moodle.iith.ac.in/moodle"));
		service.setText(sharepref.getString("service", "moodle_mobile"));
		// ImageView image = (ImageView) settings.findViewById(R.id.image);
		// image.setImageResource(R.drawable.ic_launcher);

		Button dialogButton = (Button) settings.findViewById(R.id.cancel);
		Button saveButton = (Button) settings.findViewById(R.id.save);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				settings.dismiss();
			}
		});
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SharedPreferences.Editor editor = sharepref.edit();
				editor.putString("server", url.getText().toString());
				editor.putString("service", service.getText().toString());
				editor.commit();
				ipaddr = url.getText().toString();
				webservice = service.getText().toString();
				System.out.println("ipaddress in settings " + ipaddr);
				System.out.println("webservice in settings " + webservice);
				settings.dismiss();

			}
		});
		settings.show();
	}

	public void proxy() {
		final Dialog settings = new Dialog(this);
		settings.setContentView(R.layout.proxy_menu);
		settings.setTitle("Proxy Settings");

		/*
		 * // set the custom dialog components - text, image and button TextView
		 * server = (TextView) settings.findViewById(R.id.server);
		 * server.setText("Settings"); final EditText url =
		 * (EditText)settings.findViewById(R.id.servertext); final EditText
		 * service = (EditText)settings.findViewById(R.id.servicetext);
		 * url.setText
		 * (sharepref.getString("server","http://moodle.iith.ac.in/moodle" ));
		 * service.setText(sharepref.getString("service", "moodle_mobile"));
		 * //ImageView image = (ImageView) settings.findViewById(R.id.image);
		 * //image.setImageResource(R.drawable.ic_launcher);
		 * 
		 * Button dialogButton = (Button) settings.findViewById(R.id.cancel);
		 * Button saveButton = (Button)settings.findViewById(R.id.save); // if
		 * button is clicked, close the custom dialog
		 * dialogButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { settings.dismiss(); } });
		 * saveButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub
		 * 
		 * 
		 * SharedPreferences.Editor editor = sharepref.edit();
		 * editor.putString("server", url.getText().toString());
		 * editor.putString("service", service.getText().toString());
		 * editor.commit(); System.out.println(url.getText().toString());
		 * System.out.println(service.getText().toString()); settings.dismiss();
		 * 
		 * } });
		 */
		settings.show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("Entered on pause");
		SharedPreferences.Editor editor = sharepref.edit();
		editor.putString("lock", "false");
		editor.commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences.Editor editor = sharepref.edit();
		editor.putString("lock", "true");
		editor.commit();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Destroy_TAG","Called on destroy");
	}
	


}
