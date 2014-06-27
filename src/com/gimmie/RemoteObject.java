package com.gimmie;

import org.json.JSONObject;

/**
 * RemoteObject interface for marking object come from network service.
 * 
 * @author llun
 * 
 */
public interface RemoteObject {

  public JSONObject raw();
  public Configuration getConfiguration();
  
}
