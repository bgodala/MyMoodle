/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.moodletest;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;

/**
 * @author Bhargav@google.com (Your Name Here)
 *
 */
class AsyncAddCalendarEvent extends CalendarAsyncTask {

  //private final Calendar entry;
  private final Event event;
  String id1;

  AsyncAddCalendarEvent(CalendarSampleActivity calendarSample, Event event,String id1) {
    super(calendarSample);
    this.event=event;
    this.id1=id1;
  }

  @Override
  protected void doInBackground() throws IOException {
    Event calEvent = client.events().insert(id1, event).execute();
    //model.add(calendar);
    System.out.println(calEvent.getId());
  }
}
