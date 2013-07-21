package com.moodletest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.moodletest.Participant.NetworkHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Xml;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ParticipantList extends ListActivity {
	String[] lists; 
	Intent intent;
	String category;
	
	String courseid,userid,token,str="";
	ConnHandler handler = new ConnHandler();
	Intent intent1;
	Document dom;
	String name[],id,email;
	InputStream strem;
	Button myButton;
	LinearLayout ll;// = (LinearLayout)findViewById(R.id.participant);
    LayoutParams lp;// = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	StringBuilder sb;
    XmlPullParserFactory factory;// = XmlPullParserFactory.newInstance();
    XmlPullParser xpp;// = factory.newPullParser();
    ArrayList<String> namelist,emaillist,idlist;
    Context cont;
    ProgressBar bar;
    ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);
		intent = getIntent();
		lists = new String[2];
		lists[0]="hello";
		lists[1]="world";
		cont=this;
		bar = (ProgressBar)findViewById(R.id.progressBar1);
		bar.setVisibility(View.VISIBLE);
		//ProgressBar progressBar = new ProgressBar(this);
        //progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        //progressBar.setIndeterminate(true);
        //getListView().setEmptyView(progressBar);
		listView = (ListView) findViewById(android.R.id.list);
		//listView.setEmptyView(progressBar);
		
        namelist =new ArrayList<String>();
        emaillist =new ArrayList<String>();
        idlist = new ArrayList<String>();
		
		//bar=(ProgressBar)this.findViewById(R.id.progressBar1);
		//bar.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		intent1=new Intent(this,Send.class);
		courseid = intent.getStringExtra("courseid");
		token = intent.getStringExtra("token");
	    userid = intent.getStringExtra("userid");
	    ll = (LinearLayout)findViewById(R.id.participant);
	    lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    new NetworkHandler().execute();
		
		
	}

	public void ConnHelper()
	{
		try {
			HttpResponse resp = handler.doPost(MainActivity.ipaddr + "/webservice/rest/server.php?wstoken="+token,"courseid="+courseid+"&wsfunction=core_enrol_get_enrolled_users");
			
			HttpEntity enty = resp.getEntity();
    		strem= enty.getContent();
    		
   	         //InputStreamReader is = new InputStreamReader(strem);
   	         //sb=new StringBuilder();
   	         //BufferedReader br = new BufferedReader(is);
   	         //String read = br.readLine();

   	         //while(read != null) {
   	             //System.out.println(read);
   	           //  sb.append(read);
   	             //read =br.readLine();
    		try
   	     	{
   	         factory = XmlPullParserFactory.newInstance();
   	         factory.setNamespaceAware(true);
   	         xpp = factory.newPullParser();
   	         //xpp = Xml.newPullParser();
   	         //xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
    			//String myString = IOUtils.toString(in, "UTF-8");

   	         //new StringReader(sb.toString());
   	         xpp.setInput(strem, null);
   	         //System.out.println(sb.toString());
   	         //String str1 = sb.toString();
   	         //str1.replaceAll(">\\s*<", "><");
   	         //System.out.println(str);
   	         //xpp.setInput( new StringReader(sb.toString()) );
   	         int eventType = xpp.getEventType();
   	         while (eventType != XmlPullParser.END_DOCUMENT) {
   	        	 System.out.println("Entered while loop");
   	        	 if(eventType == XmlPullParser.START_TAG) {
   	        		//System.out.println("Entered while loop "+xpp.getName()+" "+xpp.getDepth()+" "+xpp.getAttributeCount()+" ");
   	        	  if(xpp.getDepth()==4 && xpp.getName().equals("KEY") && xpp.getAttributeCount()>0){
   	        		//System.out.println("Entered depth 3");
   	        		  if(xpp.getAttributeValue(0).equals("id")){
   	        			xpp.next();
   	        			xpp.next();
   	        			idlist.add(xpp.getText());
						System.out.println("fullname : "+ xpp.getText());
					}else if(xpp.getAttributeValue(0).equals("fullname")){
						xpp.next();
						xpp.next();
						namelist.add(xpp.getText());
						System.out.println(xpp.getText());
					}else if(xpp.getAttributeValue(0).equals("email")){
						xpp.next();
						xpp.next();
						emaillist.add(xpp.getText());
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
		Intent intent1 = new Intent(this,Send.class);
		System.out.println("id is :"+idlist.get(position));
		intent1.putExtra("tuid", idlist.get(position));
		intent1.putExtra("token", token);
		startActivity(intent1);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class NetworkHandler extends AsyncTask<Void, Void, Void> {

		
	    @Override
	    protected void onPreExecute(){
	        
	    }
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ConnHelper();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			name = new String[namelist.size()];
			for(int i=0;i<namelist.size();i++)
			{
				name[i]=namelist.get(i);
			}
			bar.setVisibility(View.GONE);
			//setListAdapter(new ArrayAdapter<String>(cont,R.layout.activity_participant_list,name));
			//listView.setAdapter(new ArrayAdapter<String>(cont,R.layout.activity_participant_list,name));
			listView.setAdapter(new ContactListAdapter(cont,name));
		}	
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