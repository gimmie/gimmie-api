package com.gimmie.model;

import java.util.Date;

import org.json.JSONObject;

import com.gimmie.Configuration;
import com.gimmie.Gimmie;

/**
 * @deprecated
 * Use {link {@link Activities} instead
 * 
 * Recent action return from
 * {@link Gimmie#loadRecentActions(android.os.Handler, com.gimmie.AsyncResult)}
 * api.
 * 
 * @author llun
 * 
 */
public class RecentAction extends IDBaseObject {

  /**
   * Create RecentAction object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public RecentAction(JSONObject object, Configuration configuration) {
    super(object, configuration);
  }

  /**
   * Get {@link RecentAction} for current recent action. Recent action include
   * action, event and create time.
   * 
   * @return Action object in recent action.
   */
  public Action getAction() {
    Action action = null;
    JSONObject actionRaw = mObject.optJSONObject("action");
    if (actionRaw != null) {
      action = new Action(actionRaw, mConfiguration);
    }
    return action;
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

  /**
   * Get time that this action happen.
   * 
   * @return Date object that this event happen
   */
  public Date getCreatedTime() {
    Date date = new Date(mObject.optLong("created_at") * 1000);
    return date;
  }

}
