package com.gimmie.trackers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by llun on 14/5/14.
 */
public class GATracker extends Tracker {

  /**
   * Index as set from Google Analytics console
   */
  private static final int REWARD_ID_DIMENSION_INDEX = 1;
  private static final int REWARD_NAME_DIMENSION_INDEX = 2;
  private static final int STORE_ID_DIMENSION_INDEX= 3;
  private static final int STORE_NAME_DIMENSION_INDEX = 4;
  private static final int CATEGORY_ID_DIMENSION_INDEX = 5;
  private static final int CATEGORY_NAME_DIMENSION_INDEX = 6;

  private static final HashMap<String, Integer> DIMENSION_ID_MAP;
  static{
    DIMENSION_ID_MAP = new HashMap<String, Integer>();
    DIMENSION_ID_MAP.put(Tracker.REWARD_ID_DIMENSION, REWARD_ID_DIMENSION_INDEX);
    DIMENSION_ID_MAP.put(Tracker.REWARD_NAME_DIMENSION, REWARD_NAME_DIMENSION_INDEX);
    DIMENSION_ID_MAP.put(Tracker.STORE_ID_DIMENSION, STORE_ID_DIMENSION_INDEX);
    DIMENSION_ID_MAP.put(Tracker.STORE_NAME_DIMENSION, STORE_NAME_DIMENSION_INDEX);
    DIMENSION_ID_MAP.put(Tracker.CATEGORY_ID_DIMENSION, CATEGORY_ID_DIMENSION_INDEX);
    DIMENSION_ID_MAP.put(Tracker.CATEGORY_NAME_DIMENSION, CATEGORY_NAME_DIMENSION_INDEX);
  }

  private Configuration mConfiguration;
  private Context mContext;

  private com.google.android.gms.analytics.Tracker mGATracker;

  public GATracker(Context context, Configuration configuration) {
    mConfiguration = configuration;
    mContext = context;

    GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
    mGATracker = analytics.newTracker("UA-51271973-1");
  }

  public void update() {

  }

  public void login(String username) {
    mGATracker.set("&uid", username);
  }

  public void logout() {
    mGATracker.set("&uid", null);
  }

  public void flush() {
    GoogleAnalytics.getInstance(mContext).dispatchLocalHits();
  }

  public void track(String eventName) {
    this.track(eventName, null);
  }

  public void track(String eventName, Map<String, Object> properties) {
    HitBuilders.EventBuilder eventBuilder= new HitBuilders.EventBuilder()
            .setCategory("Android")
            .setAction(eventName)
            .setLabel(mConfiguration.getKey());
    //set custom dimensions
    if(properties!=null && eventBuilder!=null) {
      for (Object dimen : properties.keySet().toArray()) {
        eventBuilder.setCustomDimension(
            DIMENSION_ID_MAP.get((String)dimen),
            properties.get((String)dimen).toString());
      }
    }
    mGATracker.setScreenName("AndroidSDK");
    mGATracker.send(eventBuilder.build());

  }

}
