package com.moodletest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FileDownload extends Activity {

	InputStream strem;
	String url,token,name,courseid;
	File test_file;
	ProgressBar bar;
	final Context cont =this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_download);
		bar=(ProgressBar)findViewById(R.id.progressBar1);
		
		//bar.setVisibility(ProgressBar.VISIBLE);
		Intent intent = getIntent();
	    token = intent.getStringExtra("token");
	    url = intent.getStringExtra("url");
	    name = intent.getStringExtra("name");
	    courseid = intent.getStringExtra("courseid");
	    File dir_path = new File(Environment.getExternalStorageDirectory()
		         +"/Android/data/"+this.getPackageName()+"/files/"+courseid);
	    dir_path.mkdirs();
		test_file = new File(dir_path.getAbsolutePath()+File.separator+name);
		//+this.getPackageName()+File.separator
		//test_file.mkdirs();
		new NetworkHandler().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_download, menu);
		return true;
	}

	public void ConnHelper() {
		try {
			if(test_file.exists())
			{
				bar.setMax(100);
				bar.setProgress(100);
				return;
			}
				
			HttpResponse resp = ConnHandler.doPost(url,"token="+token);

			HttpEntity enty = resp.getEntity();
			strem = enty.getContent();
			int count;
			long lengthOfFile = enty.getContentLength();
			byte data[] = new byte[1024];
			long total=0;
			bar.setMax((int)lengthOfFile);
			FileOutputStream output = new FileOutputStream(test_file);
		      while ((count = strem.read(data)) != -1) {
		    	  total += count;
	                bar.setProgress((int)(total));
	                output.write(data, 0, count);
	            }
		      output.flush();
	            output.close();
	            strem.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class NetworkHandler extends AsyncTask<Void, Void, Void> {

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
			bar.setVisibility(ProgressBar.GONE);
			Uri path = Uri.fromFile(test_file); 
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                startActivity(pdfIntent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(cont, "No Application available to view pdf", Toast.LENGTH_LONG).show(); 
            }
		}
	}

}
