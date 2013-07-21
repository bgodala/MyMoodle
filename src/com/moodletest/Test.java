package com.moodletest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class Test extends Activity {

	ConnHandler handler;
	String userid,token;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		handler = new ConnHandler();
		Intent intent = getIntent();
		token = intent.getStringExtra("token");
	    userid = intent.getStringExtra("userid");
		try {
			HttpResponse resp = handler.doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"courseid="+"&wsfunction=moodle_user_get_users_by_courseid");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

}
