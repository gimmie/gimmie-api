package com.gimmie.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gimmie.Configuration;
import com.gimmie.Gimmie;

/**
 * Claim object that create after redeem a reward
 * 
 * @author llun
 * 
 */
public class Claim extends IDBaseObject {

  /**
   * Create Claim object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   * @param configuration
   *          Gimmie configuration object
   */
  public Claim(JSONObject object, Configuration configuration) {
    super(object, configuration);
  }

  /**
   * Get claim reward code, the code specify by sponsor in portal
   * 
   * @return reward code as String
   */
  public String getCode() {
    return mObject.optString("code");
  }

  /**
   * Get {@link Reward} relate to this claim
   * 
   * @return reward object
   */
  public Reward getReward() {
    try {
      return new Reward(mObject.getJSONObject("reward"), mConfiguration);
    } catch (JSONException e) {
      return null;
    }
  }

  public Date getRedeemedDate() {
    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
        Locale.getDefault());
    String createdAt = mObject.optString("created_at");
    try {
      return parser.parse(createdAt);
    } catch (ParseException e) {
      Logger.getInstance().verbose(Gimmie.LOG_TAG, "Can't parse date format", e);
      return null;
    }
  }

}
