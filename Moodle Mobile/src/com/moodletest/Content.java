package com.moodletest;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Content extends Activity {

	
	 String token;
	 String cid;
     Document dom;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        
        Intent intent = getIntent();
	    token = intent.getStringExtra("token");
	    cid=intent.getStringExtra("cid");
	    ConnHandler handler = new ConnHandler();
	    TextView Course = (TextView)findViewById(R.id.content);
	    Course.setMovementMethod(new ScrollingMovementMethod());
	    
	    try {
			HttpResponse resp = handler.doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"courseid="+cid+"&wsfunction=core_course_get_contents");
            
			HttpEntity enty = resp.getEntity();
    		InputStream strem= enty.getContent();
    		handler.xmlparser(strem);
			
    		dom = handler.returnDom(resp);
			Course.setText("Summary::"+handler.getKeyValue("summary", dom));
			NodeList mulist = dom.getElementsByTagName("SINGLE");
            for(int i=0;i<mulist.getLength();i++){
            	System.out.println(mulist.getLength()+" : "+i);
            	Course.append(handler.getKeyValue("name", dom,i)+"\n");
            	
            	
            }
          /*  Button myButton;
            String courseid="";
            NodeList mulist = dom.getElementsByTagName("SINGLE");
            for(int i=0;i<mulist.getLength();i++){
            	myButton = new Button(this);
            	courseid=handler.getKeyValue("id", dom, i);
            myButton.setText(handler.getKeyValue("fullname", dom,i));
            
            LinearLayout ll = (LinearLayout)findViewById(R.id.Layout);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ll.addView(myButton, lp);
            myButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					
				}
			});
            }*/
	    } //catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//} 
	    catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception occured in Content");
		}
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content, menu);
        return true;
    }
}
