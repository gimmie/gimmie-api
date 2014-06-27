package com.gimmie.trackers;

import android.content.Context;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by keang on 5/27/14.
 */
public class AggregatedTracker extends Tracker{
  private Configuration mConfiguration;
  private Context mContext;
  private ArrayList<Tracker> mTrackers;

  public AggregatedTracker(Context context, Configuration configuration) {
    mConfiguration = configuration;
    mContext = context;

    mTrackers = new ArrayList<Tracker>();
    mTrackers.add(new GATracker(mContext, mConfiguration));
    //mTrackers.add(new MixPanelTracker(mContext, mConfiguration)); wait till danny pays
    mTrackers.add(new ParseTracker(mContext, mConfiguration));

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
