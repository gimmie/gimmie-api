package com.gimmie.model;

import com.gimmie.Configuration;
import com.gimmie.Gimmie;
import com.gimmie.Logger;
import com.gimmie.RemoteObject;

import org.json.JSONObject;

/**
 * User object that contain points and level information
 * 
 * @author llun
 * 
 */
public class User implements RemoteObject {

  private JSONObject mObject;
  private Configuration mConfiguration;

  /**
   * Create User object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public User(JSONObject object, Configuration configuration) {
    mObject = object;
    mConfiguration = configuration;
  }

  /**
   * Unique user ID that using when login
   * 
   * @return unique id as String
   */
  public String getUserID() {
    return mObject.optString("user_id");
  }

  /**
   * Total points user get from events
   * 
   * @return points as Integer
   */
  public int getAwardedPoints() {
    return mObject.optInt("awarded_points");
  }

  /**
   * Total points user use
   * 
   * @return points as Integer
   */
  public int getRedeemedPoints() {
    return mObject.optInt("redeemed_points");
  }

  /**
   * Number of points that need for level up
   * 
   * @return number of points that current user needs to go to next level
   */
  public int getPointsToNextLevel() {
    return mObject.optInt("points_to_next_level");
  }

  /**
   * Total points that user currently have and can use to redeem
   * 
   * @return points as Integer
   */
  public int getUsedPoints() {
    return getAwardedPoints() - getRedeemedPoints();
  }

  /**
   * Current user level
   * 
   * @return level as Integer, if game doesn't have level, return 0
   */
  public int getCurrentLevel() {
    return mObject.optInt("current_level", 1);
  }

  /**
   * Number of points that need for go to next level
   * 
   * @return points as Integer
   */
  public int getNextLevelPoints() {
    return mObject.optInt("next_level_points", -1);
  }

  /**
   * Number of points user have in current level
   * 
   * @return points as Integer
   */
  public int getCurrentLevelPoints() {
    return mObject.optInt("current_level_points", -1);
  }

  /**
   * Get user progress for current level
   * 
   * @return progress as Float
   */
  public float getLevelProgressPercent() {
    Logger.getInstance().debug(Gimmie.LOG_TAG, String.format("%s", mObject));
    Logger.getInstance().debug(Gimmie.LOG_TAG, String.format(
            "Awarded Points: %d Next Level Points: %d", getPointsToNextLevel(),
            getNextLevelPoints()));
    return (float) (getAwardedPoints() - getCurrentLevelPoints())
        / (float) (getNextLevelPoints() - getCurrentLevelPoints()) * 100;
  }

  public JSONObject raw() {
    return mObject;
  }

  public Configuration getConfiguration() {
    return mConfiguration;
  }
  
}
