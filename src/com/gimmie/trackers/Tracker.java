package com.gimmie.trackers;

import android.content.Context;

import java.util.Map;

public abstract class Tracker {

  /**
   * Custom dimension name for tracking.
   */
  public static final String REWARD_ID_DIMENSION = "reward_id";
  public static final String REWARD_NAME_DIMENSION = "reward_name";
  public static final String STORE_ID_DIMENSION = "store_id";
  public static final String STORE_NAME_DIMENSION = "store_name";
  public static final String CATEGORY_ID_DIMENSION = "category_id";
  public static final String CATEGORY_NAME_DIMENSION = "category_name";

  private static Tracker sTracker;

  public static Tracker getTracker(Context context, Configuration configuration) {
    if (sTracker == null) {
      synchronized (Tracker.class) {
        if (sTracker == null) {
          sTracker = new AggregatedTracker(context, configuration);
        }
      }
    }

    return sTracker;
  }

  /**
   * Update Tracker from Configuration
   */
  public abstract void update();

  /**
   * Log login user in tracker information
   * 
   * @param username
   */
  public abstract void login(String username);

  /**
   * Remove login user from tracker information
   */
  public abstract void logout();

  /**
   * Flush tracker data to service if available.
   */
  public abstract void flush();

  /**
   * Tracking event
   * 
   * @param eventName
   */
  public abstract void track(String eventName);

  /**
   * Tracking event name with properties
   * 
   * @param eventName
   * @param properties
   */
  public abstract void track(String eventName, Map<String, Object> properties);

}
