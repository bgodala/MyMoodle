package com.moodletest;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ForumListAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final String[] subject,author,date,post;
  private final Bitmap[] picurl;

  public ForumListAdapter(Context context, String[] subject,Bitmap[] picurl,String[] author,String[] date,String[] post) {
    super(context, R.layout.forum_row_layout, subject);
    this.context = context;
    this.subject = subject;
    this.picurl=picurl;
    this.post=post;
    this.author=author;
    this.date=date;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.forum_row_layout, parent, false);
    TextView subjectTextView = (TextView) rowView.findViewById(R.id.subject);
    subjectTextView.setText(subject[position]);
    TextView authorTextView = (TextView) rowView.findViewById(R.id.by);
    authorTextView.setText(author[position]);
    TextView dateTextView = (TextView) rowView.findViewById(R.id.date);
    dateTextView.setText(date[position]);
    TextView postTextView = (TextView) rowView.findViewById(R.id.post);
    postTextView.setText(post[position]);
    ImageView icon = (ImageView)rowView.findViewById(R.id.userimage);
    //icon.setImageBitmap(picurl[position]);

    

    return rowView;
  }
  
  
}