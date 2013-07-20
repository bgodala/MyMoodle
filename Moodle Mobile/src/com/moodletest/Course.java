package com.moodletest;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Course extends Activity implements View.OnClickListener {

	 String token;
	 String uid;
	Document dom;
	String courseid="";
	 Intent intent1;// = new Intent(this,Content.class);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
		Intent intent = getIntent();
		intent1=new Intent(this,Content.class);
	    token = intent.getStringExtra("token");
	    uid=intent.getStringExtra("userid");
	    ConnHandler handler = new ConnHandler();
	    TextView Course = (TextView)findViewById(R.id.Courses);
	    
	    try {
			HttpResponse resp = handler.doPost(MainActivity.ipaddr+"/webservice/rest/server.php?wstoken="+token,"userid="+uid+"&wsfunction=moodle_enrol_get_users_courses");
			dom = handler.returnDom(resp);
            Button myButton;
            
            NodeList mulist = dom.getElementsByTagName("SINGLE");
            for(int i=0;i<mulist.getLength();i++){
            	myButton = new Button(this);
            	courseid=handler.getKeyValue("id", dom, i);
            	System.out.println(courseid);
            myButton.setText(handler.getKeyValue("fullname", dom,i));
            
            
           myButton.setId(Integer.parseInt(courseid));
           myButton.setTag(courseid);
           myButton.setOnClickListener(this);
            
            
            LinearLayout ll = (LinearLayout)findViewById(R.id.Layout);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ll.addView(myButton, lp);
           
            
            
           
            }
	    } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /*public void setmyButton(){
    	
        startActivity(intent);
    }*/
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course, menu);
        return true;
    }

	@Override
	public void onClick(View arg0) {
		intent1.putExtra("cid", arg0.getTag().toString());
		intent1.putExtra("token", token);
		startActivity(intent1);
		// TODO Auto-generated method stub
		
	}
}

