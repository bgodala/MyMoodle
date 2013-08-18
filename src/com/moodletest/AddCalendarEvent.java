package com.moodletest;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class AddCalendarEvent extends Activity {
  private String id;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_calendar_event);
    id = getIntent().getStringExtra("id");
    Event event = new Event();

    event.setSummary("Appointment");
    event.setLocation("Somewhere");

    ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
    attendees.add(new EventAttendee().setEmail("godalabhargav.reddy@hmail.com"));
    // ...
    event.setAttendees(attendees);

    Date startDate = new Date();
    Date endDate = new Date(startDate.getTime() + 3600000);
    DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
    event.setStart(new EventDateTime().setDateTime(start));
    DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
    event.setEnd(new EventDateTime().setDateTime(end));
    
    
    //Event createdEvent = client.events().insert('primary', event).execute();

    //System.out.println(createdEvent.getId());
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.add_calendar_event, menu);
    return true;
  }
  
  

}
