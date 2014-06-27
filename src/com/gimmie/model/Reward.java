package com.gimmie.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.gimmie.Configuration;
import com.gimmie.Gimmie;

/**
 * Reward object in catalogue return with
 * {@link Gimmie#loadCategory(android.os.Handler, com.gimmie.AsyncResult)} api
 * 
 * @author llun
 * 
 */
public class Reward extends IDBaseObject {

  /**
   * Create Reward object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public Reward(JSONObject object, Configuration configuration) {
    super(object, configuration);
  }

  /**
   * Get reward name that configure in Gimmie portal
   * 
   * @return reward name as String
   */
  public String getName() {
    return mObject.optString("name");
  }

  /**
   * Get reward shortname which automatically trim when put reward name, the
   * name is 20 characters long.
   * 
   * @return reward short name as String
   */
  public String getShortName() {
    return mObject.optString("short_name");
  }

  /**
   * Reward description configure in Gimmie portal.
   * 
   * @return reward description as String
   */
  public String getDescription() {
    return mObject.optString("description");
  }

  /**
   * Store name that reward belong to.
   * 
   * @return Store name as String
   */
  public String getStoreName() {
    return mObject.optString("store_name");
  }

  /**
   * Get fine-print/term&condition configure in portal
   * 
   * @return fine-print/term&condition as String
   */
  public String getFinePrint() {
    return mObject.optString("fine_print");
  }

  /**
   * Get points that need to redeem this reward
   * 
   * @return points number as Integer
   */
  public int getPoints() {
    return mObject.optInt("points", -1);
  }

  /**
   * Get total quantity reward put in portal
   * 
   * @return total number of rewards in portal
   */
  public int getTotalQuantity() {
    return mObject.optInt("total_quantity", -1);
  }

  /**
   * Get number of rewards that already claimed by user
   * 
   * @return total number of claimed reward
   */
  public int getTotalClaimed() {
    return mObject.optInt("claimed_quantity", -1);
  }

  public Date getExpiredDate() {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
	        Locale.getDefault());

    try {
      return format.parse(mObject.optString("valid_until"));
    } catch (ParseException e) {
      Logger.getInstance().error(Gimmie.LOG_TAG, "Can't parse valid date", e);
      return null;
    }

  }

  public Date getStartDate() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
        Locale.getDefault());

    try {
      return format.parse(mObject.optString("start_date"));
    } catch (ParseException e) {
      Logger.getInstance().error("Can't parse start date", e);
      return null;
    }
  }

  public Date getEndDate() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
        Locale.getDefault());

    try {
      return format.parse(mObject.optString("end_date"));
    } catch (ParseException e) {
      Logger.getInstance().verbose(Gimmie.LOG_TAG, "Can't parse end date", e);
      return null;
    }
  }

  /**
   * Get reward image url
   * 
   * @return image url as String
   */
  public String getImageURL() {
    String imageURL = mObject.optString("image_url_retina");

    if (imageURL.startsWith("//")) {
      return String.format("http:%s", imageURL);
    } else if (imageURL.startsWith("/")) {
      return String.format("http://gm.llun.in.th:3000%s", imageURL);
    } else {
      return imageURL;
    }
  }

  /**
   * Get reward claim link, the URL will appear when this object live in Claim
   * object.
   * 
   * @return url for claiming object.
   */
  public String getURL() {
    Gimmie gimmie = Gimmie.getInstance();
    String fullname = gimmie.getUserInformation(Gimmie.LOGIN_INFORMATION_NAME);
    String email = gimmie.getUserInformation(Gimmie.LOGIN_INFORMATION_EMAIL);

    StringBuilder builder = new StringBuilder(mObject.optString("url"));
    if (fullname != null && fullname.length() > 0) {
      builder.append("&name=" + fullname);
    }
    if (email != null && email.length() > 0) {
      builder.append("&email=" + email);
    }

    return builder.toString();
  }

  /**
   * List of country this reward available.
   * 
   * @return list of country code e.g. { "TH", "SG", "ID", "PH" } or { "global"
   *         } in case reward available global.
   */
  public String[] getCountries() {
    ArrayList<String> codes = new ArrayList<String>();
    JSONArray array = mObject.optJSONArray("country_codes");
    if (array != null) {
      for (int index = 0; index < array.length(); index++) {
        codes.add(array.optString(index));
      }
    }
    return codes.toArray(new String[0]);
  }

  /**
   * Check is the reward already fully redeemed.
   * 
   * @return true if the reward is fully redeemed otherwise false.
   */
  public boolean isSoldOut() {
    if ((getTotalQuantity() > 0 && getTotalQuantity() > getTotalClaimed())
        || getTotalQuantity() < 0) {
      return false;
    }
    return true;
  }

  public boolean isExpired() {
    Date today = new Date();
    if (today.compareTo(getExpiredDate()) > 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Check is reward a featured item.
   * 
   * @return true if it's featured otherwise false.
   */
  public boolean isFeatured() {
    return mObject.optBoolean("featured");
  }

}
