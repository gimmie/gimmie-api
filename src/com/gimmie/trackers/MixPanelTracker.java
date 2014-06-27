package com.gimmie.trackers;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mixpanel.android.mpmetrics.MixpanelAPI.People;

/**
 * Analytics Tracker
 * 
 * @author llun
 * 
 */
public class MixPanelTracker extends Tracker {

  private Configuration configuration;

  private MixpanelAPI mixpanel;

  public MixPanelTracker(Context context, Configuration configuration) {
    this.configuration = configuration;

    try {
      MixpanelAPI mixpanel = MixpanelAPI.getInstance(context,
          "992efeba372e7c023b1fe937ac1c9ce3");
      JSONObject superConfigObject = new JSONObject();
      superConfigObject.put("$bucket", configuration.getKey());
      mixpanel.registerSuperProperties(superConfigObject);
      this.mixpanel = mixpanel;
    } catch (Exception e) {
      Logger.getInstance().debug(Gimmie.LOG_TAG, "Can't create mixpanel instance", e);
    }
  }

  public void update() {
    People people = mixpanel.getPeople();
    people.identify(configuration.getKey());
    people.set("API", configuration.getURL());
    people.set("$username", configuration.getKey());
    people.set("$first_name", configuration.getKey());
    people.set("$last_name", "Android app");
    people.set("$email",
        String.format("%s@gimmieworld.com", configuration.getKey()));
  }

  public void login(String user) {
    if (mixpanel != null) {
      try {
        JSONObject superProperties = new JSONObject();
        superProperties.putOpt("user", user);
        mixpanel.registerSuperProperties(superProperties);
      } catch (JSONException e) {
        // Don't add to mixpanel
        Logger.getInstance().error(Gimmie.LOG_TAG, "Can't add user property to mixpanel", e);
      }
    }
  }

  public void logout() {
    if (mixpanel != null) {
      mixpanel.unregisterSuperProperty("user");
    }
  }

  public void track(String eventName) {
    track(eventName, new HashMap<String, Object>(0));
  }

  public void track(String eventName, Map<String, Object> properties) {
    mixpanel.track(eventName, new JSONObject(properties));
  }

  public void flush() {
    mixpanel.flush();
  }

}
