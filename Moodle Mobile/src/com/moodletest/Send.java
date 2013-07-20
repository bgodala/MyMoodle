package com.moodletest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Send extends Activity {

	String token;
	String tuid;
	String message;
	Document dom;
	EditText mes;
	// boolean done=false;
	ConnHandler handler;
	final Context cont =this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		Intent intent = getIntent();
		token = intent.getStringExtra("token");
		tuid = intent.getStringExtra("tuid");
		handler = new ConnHandler();
		mes = (EditText) findViewById(R.id.editText1);
		Button send = (Button) findViewById(R.id.send);
		// ConnHandler handler = new ConnHandler();
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				message = mes.getText().toString();
				new NetworkHandler().execute();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_send, menu);
		return true;
	}

	public void ConnHelper() {
		try {
			HttpResponse resp=handler.doPost(MainActivity.ipaddr
							+ "/webservice/rest/server.php?wstoken="
							+ token,
							"messages[0][touserid]="
									+ tuid
									+ "&messages[0][text]="
									+ message
									+ "&wsfunction=core_message_send_instant_messages");
			System.out.println(resp.getStatusLine().getStatusCode());
			// Toast.makeText(this, "message sent",
			// Toast.LENGTH_LONG).show();
			// done=true;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			Toast.makeText(cont, "Message Sent Successfully", Toast.LENGTH_LONG).show();
			System.out.println("Finished on post execution");
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
