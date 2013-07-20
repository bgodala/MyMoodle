package com.moodletest;

import org.apache.http.HttpResponse;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainScreen extends Activity {
	String token,userid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		Intent intent = getIntent();
	    token = intent.getStringExtra("token");
	    userid = intent.getStringExtra("userid");
	    Toast.makeText(this, userid, Toast.LENGTH_LONG).show();
	    //HttpResponse resp1 = doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"wsfunction=moodle_webservice_get_siteinfo");
	    Button UserInfo = (Button)findViewById(R.id.user_info);
	    UserInfo.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View v) {
    	    
    	    sendMessage(token,userid,UserInfo.class);
    	    }
    	    
	    });
	    Button Participants = (Button)findViewById(R.id.participants);
	    Participants.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View v) {
    	    
    	    sendMessage(token,userid,ParticipantCourseList.class);
    	    }
    	    
	    });
	
	    Button Courses = (Button)findViewById(R.id.courses);
	    Courses.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				sendMessage(token,userid,Course.class);
				
		        
			    }

			});
	}
	public void sendMessage(String token,String userid, Class test){
		Intent intent = new Intent(this,test);
        intent.putExtra("token", token);
        intent.putExtra("userid", userid);
        startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

}
