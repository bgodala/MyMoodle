package com.moodletest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Participant extends Activity implements View.OnClickListener {
	String courseid,userid,token,str="";
	ConnHandler handler = new ConnHandler();
	Intent intent1;
	Document dom;
	String name,id,email;
	InputStream strem;
	Button myButton;
	LinearLayout ll;// = (LinearLayout)findViewById(R.id.participant);
    LayoutParams lp;// = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	StringBuilder sb;
    XmlPullParserFactory factory;// = XmlPullParserFactory.newInstance();
    XmlPullParser xpp;// = factory.newPullParser();
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_participant);
		Intent intent = getIntent();
		intent1=new Intent(this,Send.class);
		courseid = intent.getStringExtra("courseid");
		token = intent.getStringExtra("token");
	    userid = intent.getStringExtra("userid");
	    ll = (LinearLayout)findViewById(R.id.participant);
	    lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    new NetworkHandler().execute();
	    

	    //TextView view = (TextView)findViewById(R.id.participants);
	    //view.setText(str);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.participant, menu);
		return true;
	}

	public void ConnHelper()
	{

    		
    		try
   	     	{
   	         factory = XmlPullParserFactory.newInstance();
   	         factory.setNamespaceAware(true);
   	         xpp = factory.newPullParser();
   	         //String myString = IOUtils.toString(in, "UTF-8");

   	         //new StringReader(sb.toString());
   	         //xpp.setInput(in, null);
   	         //System.out.println(sb.toString());
   	         //String str1 = sb.toString();
   	         //str1.replaceAll(">\\s*<", "><");
   	         //System.out.println(str);
   	         xpp.setInput( new StringReader(sb.toString()) );
   	         int eventType = xpp.getEventType();
   	         while (eventType != XmlPullParser.END_DOCUMENT) {
   	          if(eventType == XmlPullParser.START_DOCUMENT) {
   	              System.out.println("Start document");
   	          } else if(eventType == XmlPullParser.START_TAG) {
   	        	  
   	        		  if(xpp.getName().equals("SINGLE"))
   	        		  {
   	        			  System.out.println("Entered SINGLE and depth is "+xpp.getDepth() );
   	        			  xpp.next();
   	        			  //xpp.getName().equals("SINGLE")
   	        			  while(true)
   	        			  {
   	        				  if(xpp.getEventType()!=XmlPullParser.START_TAG)
   	        				  {
   	        					  if(xpp.getEventType()==XmlPullParser.END_TAG)
   	        					  {
   	        						  if(xpp.getName().equals("SINGLE") && xpp.getDepth()==3)
   	        						  {
   	        							myButton=new Button(this);
   	        							myButton.setTag(id);
   	        			            	myButton.setText("Send message to "+name);
   	        			            	myButton.setOnClickListener(this);
   	        			            	 
   	        			            	
   	        			            	ll.addView(myButton,lp); 
   	        			            	
   	        			            	break;
   	        						  }
   	        					  }
   	        					xpp.next();
   	        					continue;
   	        				  }
   	        				  else if(xpp.getName().equals("KEY") && xpp.getEventType()==XmlPullParser.START_TAG)
   	        				  {
   	        					  if(xpp.getAttributeCount()>0 && xpp.getAttributeName(0).equals("name"))
   	        					  {
   	        						  
   	        						  if(xpp.getAttributeValue(0).equals("id") && xpp.getDepth()==4)
   	        						  {
   	        							
   	        							  xpp.next();
   	        							  xpp.next();
   	        							  id=xpp.getText();
   	        							System.out.println("fullname : "+ xpp.getText());
   	        						  }else if(xpp.getAttributeValue(0).equals("fullname") && xpp.getDepth()==4)
   	        						  {
   	        							  xpp.next();
   	        							  xpp.next();
   	        							  name=xpp.getText();
   	        						  }else if(xpp.getAttributeValue(0).equals("email") && xpp.getDepth()==4)
   	        						  {
   	        							  xpp.next();
   	        							  xpp.next();
   	        							  email=xpp.getText();
   	        						  }
   	        					  }
   	        				  }
   	        				  xpp.next();
   	        			  }
   	        		  }
   	              //System.out.println("Start tag "+xpp.getName()+" "+xpp.getAttributeName(0)+" : "+xpp.getAttributeValue(0));
   	          } else if(eventType == XmlPullParser.END_TAG) {
   	              //System.out.println("End tag "+xpp.getName());
   	          } else if(eventType == XmlPullParser.TEXT) {
   	              //System.out.println("Text "+xpp.getText());
   	          }
   	          eventType = xpp.next();
   	         }
   	         System.out.println("End document");
   	     }catch(Exception e){
   	    	 System.out.println("Error in xml parsing");
   	    	 e.printStackTrace();
   	     }
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		intent1.putExtra("tuid", arg0.getTag().toString());
		intent1.putExtra("token", token);
		startActivity(intent1);
	}
	class NetworkHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				HttpResponse resp = handler.doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"courseid="+courseid+"&wsfunction=moodle_user_get_users_by_courseid");
				
				HttpEntity enty = resp.getEntity();
	    		strem= enty.getContent();
	    		
	   	         InputStreamReader is = new InputStreamReader(strem);
	   	         sb=new StringBuilder();
	   	         BufferedReader br = new BufferedReader(is);
	   	         String read = br.readLine();

	   	         while(read != null) {
	   	             //System.out.println(read);
	   	             sb.append(read);
	   	             read =br.readLine();

	   	         }
		    }  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ConnHelper();
		}
	
	}
}
