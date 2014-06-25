package com.gimmie.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gimmie.Configuration;

/**
 * Event detail defined in Gimmie portal under game menu
 * 
 * @author llun
 * 
 */
public class Event extends IDBaseObject {

  /**
   * Create Event from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public Event(JSONObject object, Configuration configuration) {
    super(object, configuration);
  }

  /**
   * Event description
   * 
   * @return description as String
   */
  public String getDescription() {
    return mObject.optString("description");
  }

  /**
   * Event unique name define in portal
   * 
   * @return event name as String
   */
  public String getName() {
    return mObject.optString("name");
  }

  /**
   * List of {@link Action} as array which happen when this event trigger
   * 
   * @return array of {@link Action}
   */
  public Action[] getActions() {
    try {
      JSONArray actions = mObject.getJSONArray("actions");
      ArrayList<Action> actionList = new ArrayList<Action>(actions.length());
      for (int index = 0; index < actions.length(); index++) {
        actionList.add(new Action(actions.getJSONObject(index), mConfiguration));
      }
      return actionList.toArray(new Action[actions.length()]);
    } catch (JSONException e) {
      return new Action[0];
    }
  }

}
