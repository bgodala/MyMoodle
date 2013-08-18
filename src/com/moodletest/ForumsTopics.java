package com.moodletest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.moodletest.MyBroadcastReceiver;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ForumsTopics extends ListActivity {
	
	String username,password,token,courseid,id,url,name,userid,type,urlid;

	TextView assignment;
	ConnHandler handler;
    XmlPullParserFactory factory;// = XmlPullParserFactory.newInstance();
    XmlPullParser xpp;// = factory.newPullParser();
    InputStream strem;
    ArrayList<String> namelist,urllist,idlist;
    Context cont;
    ListView listView;
    ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);
		
		//setContentView(R.layout.activity_assignments);
		handler=new ConnHandler();
		namelist=new ArrayList<String>();
		urllist=new ArrayList<String>();
		idlist=new ArrayList<String>();
		bar = (ProgressBar)findViewById(R.id.progressBar1);
		bar.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(android.R.id.list);
		cont=this;
		Intent intent = getIntent();
	    token = intent.getStringExtra("token");
	    courseid = intent.getStringExtra("courseid");
	    username=intent.getStringExtra("username");
	    password=intent.getStringExtra("password");
	    userid=intent.getStringExtra("userid");
	    urlid=intent.getStringExtra("urlid");
	    System.out.println(token+courseid+username+password+userid);
	    
	    new NetworkHandler().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assignments, menu);
		return true;
	}
	
	public void ConnHelper()
	{
		try {
			HttpResponse resp = ConnHandler.doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"wsfunction=mod_forum_get_forum_discussions&"+"forumids[0]="+urlid);
			
			HttpEntity enty = resp.getEntity();
    		strem= enty.getContent();
    		
    		try
   	     	{
   	         factory = XmlPullParserFactory.newInstance();
   	         factory.setNamespaceAware(true);
   	         xpp = factory.newPullParser();
   	         xpp.setInput(strem, null);
   	         int eventType = xpp.getEventType();
   	         while (eventType != XmlPullParser.END_DOCUMENT) {
   	        	 
   	        	 if(eventType == XmlPullParser.START_TAG) {
   	        		//System.out.println("Entered while loop "+xpp.getName()+" "+xpp.getDepth()+" "+xpp.getAttributeCount()+" ");
   	        	  if(xpp.getDepth()==4 && xpp.getName().equals("KEY") && xpp.getAttributeCount()>0){
   	        		System.out.println("Entered depth 4");
   	        		  if(xpp.getAttributeValue(0).equals("id")){
   	        			xpp.next();
   	        			xpp.next();
   	        			//idlist.add(xpp.getText());
						
   	        			//System.out.println("id is : "+xpp.getText());
   	        			id=xpp.getText();
   	        			idlist.add(id);
					}else if(xpp.getAttributeValue(0).equals("name")){
						xpp.next();
						xpp.next();
						//emaillist.add(xpp.getText());
						//System.out.println("name is : "+xpp.getText());
						name=xpp.getText();
						namelist.add(name);
						
					}  
   	        	  }
   	        		
   	              //System.out.println("Start tag "+xpp.getName()+" "+xpp.getAttributeName(0)+" : "+xpp.getAttributeValue(0));
   	          }
   	          eventType = xpp.next();
   	         }
   	     }catch(Exception e){
   	    	 System.out.println("Error in xml parsing");
   	    	 e.printStackTrace();
   	     }
   	         
	    }  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onListItemClick(ListView list,View view,int position,long id){
		super.onListItemClick(list, view, position, id);
		//String testName = lists[position];
		//Intent intent1 = new Intent(this,Contact.class);
        //intent1.putExtra("department", category);
        //intent1.putExtra("name", testName);
        //startActivity(intent1);
		Intent intent1 = null;
		intent1 = new Intent(this,ForumDiscussion.class);
		
		intent1.putExtra("urlid", idlist.get(position));
		intent1.putExtra("url", MainActivity.ipaddr+"/mod/forum/discuss.php?d="+idlist.get(position));
		intent1.putExtra("courseid", courseid);
        intent1.putExtra("username", username);
        intent1.putExtra("password", password);
        intent1.putExtra("userid", userid);
		startActivity(intent1);
		
		
	}
	class NetworkHandler extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			ConnHelper();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String[] list = new String[namelist.size()];
			for(int i=0;i<namelist.size();i++)
			{
				list[i]=namelist.get(i);
			}
			bar.setVisibility(View.GONE);
			listView.setAdapter(new ArrayAdapter<String>(cont,R.layout.activity_participant_list,list));
			//setListAdapter(new ArrayAdapter<String>(cont,R.layout.activity_assignments,list));
		}
		
		
		
	}


}
