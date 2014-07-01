package com.gimmie.model;

import com.gimmie.Configuration;
import com.gimmie.RemoteObject;

import org.json.JSONObject;

/**
 * Abstract base object for providing id method
 * 
 * @author llun
 * 
 */
abstract class IDBaseObject implements RemoteObject {

  protected JSONObject mObject;
  protected Configuration mConfiguration;

  /**
   * Create Object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public IDBaseObject(JSONObject object, Configuration configuration) {
    mObject = object;
  }

  /**
   * Object unique id in service
   * 
   * @return Object ID as Intenger
   */
  public int getID() {
    return mObject.optInt("id", -1);
  }

  /**
   * JSON raw format return from service
   * 
   * @return String in JSON format
   */
  public String getJSONString() {
    return mObject.toString();
  }

  @Override
  public String toString() {
    return mObject.toString();
  }

  public JSONObject raw() {
    return mObject;
  }

  public Configuration getConfiguration() {
    return mConfiguration;
  }
  
}
