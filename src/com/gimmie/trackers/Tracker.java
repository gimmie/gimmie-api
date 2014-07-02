package com.gimmie.trackers;

import java.util.Map;

public interface Tracker {

  /**
   * Custom dimension name for tracking.
   */
  static final String REWARD_ID_DIMENSION = "reward_id";
  static final String REWARD_NAME_DIMENSION = "reward_name";
  static final String STORE_ID_DIMENSION = "store_id";
  static final String STORE_NAME_DIMENSION = "store_name";
  static final String CATEGORY_ID_DIMENSION = "category_id";
  static final String CATEGORY_NAME_DIMENSION = "category_name";

  /**
   * Update Tracker from Configuration
   */
  void update();

  /**
   * Log login user in tracker information
   * 
   * @param username
   */
  void login(String username);

  /**
   * Remove login user from tracker information
   */
  void logout();

  /**
   * Flush tracker data to service if available.
   */
  void flush();

  /**
   * Tracking event
   * 
   * @param eventName
   */
  void track(String eventName);

  /**
   * Tracking event name with properties
   * 
   * @param eventName
   * @param properties
   */
  void track(String eventName, Map<String, Object> properties);

}
