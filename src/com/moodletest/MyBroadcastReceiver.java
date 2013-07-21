package com.moodletest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {

	String username,password,token,courseid,userid,ipaddr;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		username=intent.getStringExtra("username");
		password=intent.getStringExtra("password");
		token=intent.getStringExtra("token");
		courseid=intent.getStringExtra("courseid");
		userid=intent.getStringExtra("userid");
		ipaddr=intent.getStringExtra("ipaddr");
		Log.d("My Receiver","Received");
		Intent serviceIntent = new Intent(context,TestService.class);
		serviceIntent.putExtra("username", username);
		serviceIntent.putExtra("password", password);
		serviceIntent.putExtra("courseid", courseid);
		serviceIntent.putExtra("token", token);
		serviceIntent.putExtra("userid", userid);
		serviceIntent.putExtra("ipaddr", ipaddr);
		context.startService(serviceIntent);
		
	}

}
