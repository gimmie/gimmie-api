package com.gimmie.model;

import org.json.JSONObject;

import com.gimmie.Configuration;
import com.gimmie.RemoteObject;

public class TopPlayer implements RemoteObject {

  private JSONObject mObject;
  private Configuration mConfiguration;
  
  public TopPlayer(JSONObject object, Configuration configuration) {
    mObject = object;
    mConfiguration = configuration;
  }
  
  public String getExternalUID() {
    return mObject.optString("external_uid");
  }
  
  public int getRank() {
    return mObject.optInt("rank");
  }
  
  public double getValue() {
    return mObject.optDouble("value");
  }
  
  public JSONObject raw() {
    return mObject;
  }
  
  public Configuration getConfiguration() {
    return mConfiguration;
  }
  
}
