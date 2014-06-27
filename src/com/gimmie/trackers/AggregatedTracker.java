package com.gimmie.trackers;

import com.gimmie.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by keang on 5/27/14.
 */
public class AggregatedTracker extends Tracker{

  private Configuration mConfiguration;
  private ArrayList<Tracker> mTrackers;

  public AggregatedTracker(Tracker[] trackers) {
    mTrackers = new ArrayList<Tracker>(trackers.length);
    mTrackers.addAll(Arrays.asList(trackers));
  }
  public void update() {
  }

  public void login(String username) {
    for(Tracker t : mTrackers){
      t.login(username);
    }
  }

  public void logout() {
    for(Tracker t : mTrackers){
      t.logout();
    }
  }

  public void flush() {
    for(Tracker t : mTrackers){
      t.flush();
    }
  }

  public void track(String eventName) {
    for(Tracker t : mTrackers){
      t.track(eventName);
    }
  }

  public void track(String eventName, Map<String, Object> properties) {
    for(Tracker t : mTrackers){
      t.track(eventName, properties);
    }
  }

}
