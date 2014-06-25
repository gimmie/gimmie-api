package com.gimmie.model;

import org.json.JSONObject;

import com.gimmie.Configuration;
import com.gimmie.Gimmie;
import com.gimmie.RemoteObject;

/**
 * Action model return from Gimmie service from
 * {@link Gimmie#loadRecentActions(android.os.Handler, com.gimmie.AsyncResult)}
 * api.
 * 
 * @author llun
 * 
 */
public class Action implements RemoteObject {
  
  public static final String TYPE_AWARD_POINT = "Award Points";
  public static final String TYPE_INSTANCE_REWARD = "Instant Reward";

  private JSONObject mObject;
  private Configuration mConfiguration;

  /**
   * Create Action object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public Action(JSONObject object, Configuration configuration) {
    mObject = object;
    mConfiguration = configuration;
  }

  /**
   * Is this action success when trigger the event to the server.
   * 
   * @return true if action can done otherwise false.
   */
  public boolean isSuccess() {
    return mObject.optBoolean("success");
  }

  /**
   * Action type, currently has only two types, {@link #TYPE_AWARD_POINT} or
   * {@link #TYPE_INSTANCE_REWARD}
   * 
   * @return action type as string
   */
  public String getType() {
    return mObject.optString("action_type");
  }

  /**
   * Action message configure in Gimmie portal when create event.
   * 
   * @return action message as string
   */
  public String getMessage() {
    return mObject.optString("message");
  }

  /**
   * Claim object incase action is instant reward type which will give reward
   * when user do an action.
   * 
   * @return claim object for instant reward configure in Gimmie portal
   */
  public Claim getClaim() {
    Claim claim = null;
    JSONObject claimRaw = mObject.optJSONObject("claim");
    if (claimRaw != null) {
      claim = new Claim(claimRaw, mConfiguration);
    }
    return claim;
  }

  /**
   * Points that user earn for this action, if this action is
   * {@link #TYPE_INSTANCE_REWARD}, points return -1 because instant reward action
   * doesn't have point.
   * 
   * @return points as integer for {@link #TYPE_INSTANCE_REWARD} only.
   */
  public int getPoints() {
    return mObject.optInt("points", -1);
  }
  
  public JSONObject raw() {
    return mObject;
  }
  
  public Configuration getConfiguration() {
    return mConfiguration;
  }

}
