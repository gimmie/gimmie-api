package com.gimmie.model;

import com.gimmie.Configuration;
import com.gimmie.RemoteObject;

import org.json.JSONObject;

public class Checkin implements RemoteObject {

  private JSONObject mObject;
  private Configuration mConfiguration;

  public Checkin(JSONObject object, Configuration configuration) {
    mObject = object;
    mConfiguration = configuration;
  }

  public int getAllTime() {
    return mObject.optInt("all_time");
  }

  public int getToday() {
    return mObject.optInt("today");
  }

  public int getThisWeek() {
    return mObject.optInt("this_week");
  }

  public int getThisMonth() {
    return mObject.optInt("this_month");
  }

  public int getPastWeek() {
    return mObject.optInt("past_week");
  }

  public int getPastMonth() {
    return mObject.optInt("past_month");
  }

  public int getPast7Days() {
    return mObject.optInt("past_7_days");
  }

  public int getPast30Days() {
    return mObject.optInt("past_30_days");
  }

  @Override
  public JSONObject raw() {
    return mObject;
  }
  
  public Configuration getConfiguration() {
    return mConfiguration;
  }

  public String toString() {
    return mObject.toString();
  }

}
