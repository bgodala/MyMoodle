package com.moodletest;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChoiceMenu extends Activity {
	String token, courseid, userid;
	String username, password;
	Context cont;
	private static final long REPEAT_TIME = 1000 * 30;
	
	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_menu);
		cont = this;
		Intent intent = getIntent();
		token = intent.getStringExtra("token");
		userid = intent.getStringExtra("userid");
		courseid = intent.getStringExtra("courseid");
		username = intent.getStringExtra("username");
		password = intent.getStringExtra("password");
		Button assignButton = (Button) findViewById(R.id.assignments);
		Button messageButton = (Button) findViewById(R.id.message);
		Button filesButton = (Button) findViewById(R.id.files);
		Button courseButton = (Button) findViewById(R.id.course);
		Button urlButton = (Button) findViewById(R.id.urlresources);
		Button forumsButton = (Button) findViewById(R.id.forums);
		assignButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//setAlaram(cont);
				sendMessage(Assignments.class, "assign");

			}
		});
		messageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMessage(ParticipantList.class);
			}
		});
		filesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMessage(Files.class);

			}
		});
		courseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMessage(CourseContents.class);

			}
		});

		urlButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMessage(Assignments.class, "url");

			}
		});
		forumsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMessage(ForumList.class, "forum");

			}
		});
	}

	public void sendMessage(Class test) {
		Intent intent = new Intent(this, test);
		intent.putExtra("token", token);
		intent.putExtra("userid", userid);
		intent.putExtra("courseid", courseid);
		intent.putExtra("username", username);
		intent.putExtra("password", password);
		startActivity(intent);
	}

	public void sendMessage(Class test, String type) {
		Intent intent = new Intent(this, test);
		intent.putExtra("token", token);
		intent.putExtra("userid", userid);
		intent.putExtra("courseid", courseid);
		intent.putExtra("username", username);
		intent.putExtra("password", password);
		intent.putExtra("type", type);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choice_menu, menu);
		return true;
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
