package com.moodletest;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.moodletest.Assignments.NetworkHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

 public class Files extends ListActivity {
	
	String username,password,token,courseid,id,url,name,userid,modname;

	TextView assignment;
	ConnHandler handler;
    XmlPullParserFactory factory;// = XmlPullParserFactory.newInstance();
    XmlPullParser xpp;// = factory.newPullParser();
    InputStream strem;
    ArrayList<String> namelist,urllist,idlist;
    Context cont;
    ListView listView;
    ProgressBar bar;
    DBAdapter database;

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
	    System.out.println(token+courseid+username+password+userid);
	    
	    database = new DBAdapter(this);
	    database.open();
	    //database.assignTableCreate(userid);
	    if(!database.queryTable("usr_assign_table_"+userid))
	    	database.assignTableCreate(userid);
	    database.close();
	    database.open();
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
			HttpResponse resp = ConnHandler.doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"wsfunction=core_course_get_contents&"+"courseid="+courseid);
			
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
   	        	  if(xpp.getDepth()==7 && xpp.getName().equals("KEY") && xpp.getAttributeCount()>0){
   	        		//System.out.println("Entered depth 3");
   	        		  if(xpp.getAttributeValue(0).equals("modname")){
						xpp.next();
						xpp.next();
						modname=xpp.getText();
						
					}else if (xpp.getAttributeValue(0).equals(
							"contents")
							&& (modname.equals("folder") || modname.equals("resource"))) {
						System.out.println("Entered contents block");
						while (true) {
							if (xpp.getEventType() == XmlPullParser.END_TAG
									&& xpp.getDepth() == 7)
								break;

							if (xpp.getDepth() == 10
									&& xpp.getEventType() == XmlPullParser.START_TAG) {
								System.out.println("Entered depth 10");
								if (xpp.getName().equals("KEY")
										&& xpp.getAttributeCount() > 0) {
									if (xpp.getAttributeValue(0)
											.equals("filename")) {
										xpp.next();
										xpp.next();
										// name = xpp.getText();
										System.out.println(xpp
												.getText());
										namelist.add(xpp.getText());
										
									}else if (xpp.getAttributeValue(0)
											.equals("fileurl")) {
										xpp.next();
										xpp.next();
										// name = xpp.getText();
										System.out.println(xpp
												.getText());
										urllist.add(xpp.getText());
										
									}
								}
							}
							xpp.next();
						}

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
		Intent intent1 = new Intent(this,FileDownload.class);
		intent1.putExtra("name", namelist.get(position));
		intent1.putExtra("url", urllist.get(position));
		intent1.putExtra("courseid", courseid);
        intent1.putExtra("username", username);
        intent1.putExtra("password", password);
        intent1.putExtra("userid", userid);
        intent1.putExtra("token", token);
		startActivity(intent1);
        System.out.println("url is : "+urllist.get(position));
		
		
	}
	class NetworkHandler extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			ConnHelper();
			database.close();
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

