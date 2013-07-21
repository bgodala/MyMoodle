package com.moodletest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParticipantsCourses extends Activity implements View.OnClickListener{

	Document dom;
	String token,userid,courseid,id,name;
	Intent newIntent;
	Button myButton=null;
	ConnHandler handler;
	LinearLayout ll;
	LayoutParams lp;
	InputStream strem;
	Context cont;
	StringBuilder sb;
	
	DBAdapter database;
     XmlPullParserFactory factory;// = XmlPullParserFactory.newInstance();
       XmlPullParser xpp;// = factory.newPullParser();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_courses);
		Intent intent = getIntent();
		ll = (LinearLayout)findViewById(R.id.Layout_courses_list);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    token = intent.getStringExtra("token");
	    userid = intent.getStringExtra("userid");
	    handler = new ConnHandler();
	    newIntent = new Intent(this,ParticipantList.class);
	    newIntent.putExtra("token",token);
	    newIntent.putExtra("userid",userid);
	    cont= this;
	    database = new DBAdapter(this);
	    database.open();
	    //database.assignTableCreate(userid);
	    if(!database.queryTable("usr_course_table_"+userid))
	    	database.courseTableCreate(userid);
	    database.close();
	    new NetworkHandler().execute();
	    	    
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.participants_courses, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		newIntent.putExtra("courseid",v.getTag().toString());
		startActivity(newIntent);
		
		// TODO Auto-generated method stub
		
	}
	
	public void ConnHelper(){




    		
    		try
   	     	{
   	         //String myString = IOUtils.toString(in, "UTF-8");
   	         InputStreamReader is = new InputStreamReader(strem);
   	         sb=new StringBuilder();
   	         BufferedReader br = new BufferedReader(is);
   	         String read = br.readLine();

   	         while(read != null) {
   	             //System.out.println(read);
   	             sb.append(read);
   	             read =br.readLine();

   	         }
   	         factory = XmlPullParserFactory.newInstance();
   	         factory.setNamespaceAware(true);
   	         xpp = factory.newPullParser();
            xpp.setInput( new StringReader(sb.toString()) );
   	         //new StringReader(sb.toString());
   	         //xpp.setInput(in, null);
   	         //System.out.println(sb.toString());
   	         //System.out.println(str);

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
 	   	        			            	myButton.setText(name);
 	   	        			            	myButton.setOnClickListener(this);

 	        			            //stuff that updates ui
 	        			                	ll.addView(myButton,lp);
   	        							/*myButton=new Button(cont);
   	        							  synchronized(myButton){
   	        								
   	        			            	runOnUiThread(new Runnable() {
   	        			                 public void run() {
   	   	        							
   	   	        							myButton.setTag(id);
   	   	        			            	myButton.setText("Send message to "+name);
   	   	        			            	myButton.setOnClickListener((OnClickListener)cont);

   	        			            //stuff that updates ui
   	        			                	ll.addView(myButton,lp);
   	        			                	notify();
   	        			                	
   	        			                }
   	        			            });
   	        			            	myButton.wait();}*/
   	        			            	
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
	
	class NetworkHandler extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				HttpResponse resp = handler.doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"userid="+userid+"&wsfunction=moodle_enrol_get_users_courses");
				
		        
				HttpEntity enty = resp.getEntity();
				strem= enty.getContent();
  	         }catch(Exception e){
    			e.printStackTrace();
    		}
			//ConnHelper();
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

