package com.gimmie.model;

import java.util.Date;

import org.json.JSONObject;

import com.gimmie.Configuration;
import com.gimmie.Gimmie;
/**
 * Activities model return from Gimmie service from
 * {@link Gimmie#loadRecentActivities(android.os.Handler, com.gimmie.AsyncResult)}
 * api.
 * 
 * @author kaka
 * 
 */
public class Activities extends IDBaseObject {
  
  public static final String TYPE_POINT_CHANGE = "points_change";
  public static final String TYPE_ACTION = "action";
  public static final String TYPE_ACTION_AWARD_POINTS = "Award Points";
  public static final String TYPE_ACTION_INSTANT_REWARDS = "Instant Reward";
  public static final String TYPE_MAYOR = "mayor";
  public static final String TYPE_BADGE = "badge";
  public static final String TYPE_LEVEL = "level";
  

  private JSONObject mObject;
  private Configuration mConfiguration;

  /**
   * Create Activities object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public Activities(JSONObject object, Configuration configuration) {
	super(object, configuration);
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
   * Activities type, includes, {@link #TYPE_POINT_CHANGE},
   * {@link #TYPE_BADGE}, {@link #TYPE_MAYOR}, {@link #TYPE_LEVEL},
   * and subtypes {@link #TYPE_ACTION_AWARD_POINTS} and {@link #TYPE_ACTION_INSTANT_REWARDS}
   * which are under {@link #TYPE_ACTION}
   * 
   * @return type as string
   */
  public String getType() {
	  String type = mObject.optString("type");
	  if(type.equals(TYPE_ACTION)){
		  JSONObject detailsObject = mObject.optJSONObject("detail");
		  type  = detailsObject.optString("action_type");
	  }
	  return type;  
  }

  /**
   * message, usually the brief description of the activity
   * 
   * @return message as string
   */
  public String getMessage() {
	  //extract the message inside details if item is an "action"
	  if (mObject.optString("type").equals(TYPE_ACTION))
		  return mObject.optJSONObject("detail").optString("message");
	  //otherwise assume "content" is the desciption
	  else return mObject.optString("content");
  }
  

  /**
   * 
   * @return Date object that this event happen
   */
  public Date getCreatedTime() {
    Date date = new Date(mObject.optLong("created_at") * 1000);
    return date;
  }

  /**
   * Badge object from the activity
   * 
   * @return badge object for instant reward configure in Gimmie portal. null if 
   * it doesn't exist
   */
  public Badge getBadge() {
    Badge badge = null;
    JSONObject badgeRaw = null;
    if(getType().equals(TYPE_BADGE))
    	badgeRaw = (mObject.optJSONObject("details"));
    if (badgeRaw != null) {
      badge = new Badge(badgeRaw, mConfiguration);
    }
    return badge;
  }
  
  /**
   * Badge object from the activity
   * 
   * @return badge object for instant reward configure in Gimmie portal. null if 
   * it doesn't exist
   */
  public Mayorship geMayorship() {
    Mayorship mayor = null;
    JSONObject mayorRaw = null;
    if(getType().equals(TYPE_MAYOR))
    	mayorRaw = (mObject.optJSONObject("details"));
    if (mayorRaw != null) {
      mayor = new Mayorship(mayorRaw, mConfiguration);
    }
    return mayor;
  }
  
  //**** for compatibility? **********/
  
  /**
   * Claim object incase action is instant reward type which will give reward
   * when user do an action.
   * 
   * @return claim object for instant reward configure in Gimmie portal. null if 
   * it doesn't exist
   */
  public Claim getClaim() {
    Claim claim = null;
    JSONObject claimRaw = (mObject.optJSONObject("details")).optJSONObject("claim");
    if (claimRaw != null) {
      claim = new Claim(claimRaw, mConfiguration);
    }
    return claim;
  }
  
  /**
   * Get {@link Event} object relate to this recent action.
   * 
   * @return Event object in recent action
   */
  public Event getEvent() {
    Event event = null;
    JSONObject eventRaw = mObject.optJSONObject("event");
    if (eventRaw != null) {
      event = new Event(eventRaw, mConfiguration);
    }
    return event;
  }

  
  public JSONObject raw() {
    return mObject;
  }
  
  public Configuration getConfiguration() {
    return mConfiguration;
  }

}
