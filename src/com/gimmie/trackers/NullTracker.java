package com.gimmie.trackers;

import com.gimmie.Logger;

import java.util.Map;

/**
 * Created by llun on 7/2/14.
 */
public class NullTracker implements Tracker {

  private Logger mLogger;

  public NullTracker(Logger logger) {
    mLogger = logger;
  }

  @Override
  public void update() {
    mLogger.verbose("Update");
  }

  @Override
  public void login(String username) {
    mLogger.verbose("Login: %s", username);
  }

  @Override
  public void logout() {
    mLogger.verbose("Logout");
  }

  @Override
  public void flush() {
    mLogger.verbose("Flush");
  }

  @Override
  public void track(String eventName) {
    track(eventName, null);
  }

  @Override
  public void track(String eventName, Map<String, Object> properties) {
    mLogger.verbose("Tracker: %s", eventName);
  }
}
