package com.gimmie.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gimmie.Configuration;
import com.gimmie.Gimmie;
import com.gimmie.RemoteObject;

/**
 * Profile Object return from
 * {@link Gimmie#getProfile(android.os.Handler, com.gimmie.AsyncResult)} which
 * include points and claims information.
 * 
 * @author llun
 * 
 */
public class Profile implements RemoteObject {

  private JSONObject mObject;
  private Configuration mConfiguration;

  /**
   * Create profile object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public Profile(JSONObject object, Configuration configuration) {
    mObject = object;
    mConfiguration = configuration;
  }

  /**
   * Get {@link User} object contain in this profile
   * 
   * @return user object in profile
   */
  public User getUser() {
    User user = null;
    JSONObject userRaw = mObject.optJSONObject("user");
    if (userRaw != null) {
      user = new User(userRaw, mConfiguration);
    }
    return user;
  }

  /**
   * Get list of {@link Claim} object as array
   * 
   * @return array of claim object
   */
  public Claim[] getClaims() {
    try {
      JSONArray claims = mObject.getJSONArray("claims");
      ArrayList<Claim> claimList = new ArrayList<Claim>(claims.length());
      for (int index = 0; index < claims.length(); index++) {
        claimList.add(new Claim(claims.getJSONObject(index), mConfiguration));
      }
      return claimList.toArray(new Claim[claims.length()]);
    } catch (JSONException e) {
      return new Claim[0];
    }
  }

  /**
   * Get list of {@link Badge} object as array
   * 
   * @return array of badge object
   */
  public Badge[] getBadges() {
    try {
      JSONArray badges = mObject.getJSONArray("badges");
      ArrayList<Badge> badgeList = new ArrayList<Badge>(badges.length());
      for (int index = 0; index < badges.length(); index++) {
        badgeList.add(new Badge(badges.getJSONObject(index), mConfiguration));
      }
      return badgeList.toArray(new Badge[badges.length()]);
    } catch (JSONException e) {
      return new Badge[0];
    }
  }

  /**
   * Get list of {@link Mayorship} object as array
   * 
   * @return array of mayorship object
   */
  public Mayorship[] getMayorships() {
    try {
      JSONArray mayorships = mObject.getJSONArray("mayors");
      ArrayList<Mayorship> mayorshipList = new ArrayList<Mayorship>(mayorships.length());
      for (int index = 0; index < mayorships.length(); index++) {
        mayorshipList.add(new Mayorship(mayorships.getJSONObject(index), mConfiguration));
      }
      return mayorshipList.toArray(new Mayorship[mayorships.length()]);
    } catch (JSONException e) {
      return new Mayorship[0];
    }
  }

  /**
   * JSON raw format return from service
   * 
   * @return String in JSON format
   */
  public String getJSONString() {
    return mObject.toString();
  }

  public JSONObject raw() {
    return mObject;
  }
  
  public Configuration getConfiguration() {
    return mConfiguration;
  }

}
