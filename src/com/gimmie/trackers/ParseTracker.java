package com.gimmie.trackers;

import android.content.Context;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.parse.Parse;
import com.parse.ParseAnalytics;

/**
 * Created by keang on 5/28/14.
 */
public class ParseTracker extends Tracker {
  private Configuration mConfiguration;
  private Context mContext;
  String mUser;

  public ParseTracker(Context context, Configuration configuration) {
    this.mConfiguration = configuration;
    this.mContext = context;
    //from Parse.com, login with it@gimmieworld.com
    Parse.initialize(context, "6EflecqYKnlauRiEQrxwR9r9TqUNJzTfD4VkGqUn", "W6ZBLfIlcIz8foWSZhNiuroiYig08182poQ3LKAD");
  }

  public void update() {
  }

  public void login(String user) {
    mUser = user;
  }

  public void logout() {
    mUser = null;
  }

  public void track(String eventName) {
    track(eventName, new HashMap<String, Object>(0));
  }

  public void track(String eventName, Map<String, Object> properties) {
    Map<String, String> stringedProperties = new HashMap<String, String>();
    for (Object dimen : properties.keySet().toArray()) {
      stringedProperties.put((String)dimen, properties.get((String)dimen).toString());
    }

    //set user
    if(mUser!=null) stringedProperties.put("uid", mUser);

    //set game key
    stringedProperties.put("game_key", mConfiguration.getKey());

    Logger.getInstance().error("Parse tracer", eventName);
    ParseAnalytics.trackEvent(eventName, stringedProperties);
  }

  public void flush() {
    //Parse can't flush. eww
  }
}
