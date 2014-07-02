package com.gimmie;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Configuration {

  private static final String DEFAULT_GIMMIE_API_URL = "https://api.gimmieworld.com/1/";

  public static final String DEFAULT_COUNTRY = "global";
  public static final String DEFAULT_LANGUAGE = "en";
  public static final String DEFAULT_SPONSOR_URL = "http://www.gimmieworld.com/sponsors/?utm_source=tool&utm_medium=widget&utm_campaign=internal";

  public static final String API_KEY = "com.gimmie.api.key";
  public static final String API_SECRET = "com.gimmie.api.secret";
  public static final String API_URL = "com.gimmie.api.url";
  public static final String API_SPONSOR_URL = "com.gimmie.api.sponsor";

  public static final String DATA_COUNTRY_GLOBAL = "global";
  public static final String DATA_COUNTRY = "com.gimmie.data.default_country";
  public static final String DATA_LANGUAGE = "com.gimmie.data.language";

  public static final String VIEW_LEADERBOARD = "com.gimmie.view.leaderboard";

  public static final String NOTIFICATION_SYSTEM = "com.gimmie.notification.system.enable";
  public static final String NOTIFICATION_SYSTEM_ID = "com.gimmie.notification.system.id";
  public static final String NOTIFICATION_TOAST = "com.gimmie.notification.toast.enable";
  public static final String NOTIFICATION_POPUP = "com.gimmie.notification.popup.enable";
  public static final String NOTIFICATION_POPUP_DURATION = "com.gimmie.notification.popup.duration";
  public static final String NOTIFICATION_POPUP_FEATURED_DURATION = "com.gimmie.notification.popup.featured_duration";

  public static final String ALLOW_GUEST_REDEEM = "com.gimmie.redeem.allow_guest";
  
  public static final String GCM_PROJECT_NUMBER = "com.gimmie.gcm_project_number";
  
  public static final String SDK_VERSION = "android 2.0";
  
  private Map<String, Object> map = new HashMap<String, Object>(10);

  public Configuration(Map<String, Object> map) {
    this.map.putAll(map);
  }

  public String getKey() {
    return (String) map.get(API_KEY);
  }

  public void setKey(String key) {
    map.put(API_KEY, key);
  }

  public String getSecret() {
    return (String) map.get(API_SECRET);
  }

  public void setSecret(String secret) {
    map.put(API_SECRET, secret);
  }

  public String getURL() {
    if (map.containsKey(API_URL) && ((String) map.get(API_URL)).length() > 0) {
      return (String) map.get(API_URL);
    }
    return DEFAULT_GIMMIE_API_URL;
  }

  public void setURL(String url) {
    map.put(API_URL, url);
  }

  public boolean isSystemNotificationEnabled() {
    if (map.containsKey(NOTIFICATION_SYSTEM)) {
      return (Boolean) map.get(NOTIFICATION_SYSTEM);
    }
    return false;
  }

  public int getSystemNotificationID() {
    if (map.containsKey(NOTIFICATION_SYSTEM_ID)) {
      return (Integer) map.get(NOTIFICATION_SYSTEM_ID);
    }
    return -1;
  }

  public boolean isToastNotificationEnabled() {
    if (map.containsKey(NOTIFICATION_TOAST)) {
      return (Boolean) map.get(NOTIFICATION_TOAST);
    }
    return false;
  }

  public boolean isPopupNotificationEnabled() {
    if (map.containsKey(NOTIFICATION_POPUP)) {
      return (Boolean) map.get(NOTIFICATION_POPUP);
    }
    return false;
  }

  /**
   * Popup notification duration in milliseconds
   * 
   * @return popup duration in milliseconds
   */
  public int getPopupNotificationDuration() {
    if (map.containsKey(NOTIFICATION_POPUP_DURATION)) {
      return (Integer) map.get(NOTIFICATION_POPUP_DURATION) * 1000;
    }
    return 2000;
  }

  /**
   * Featured reward notification after normal notification duration in
   * milliseconds
   *
   * @return feature reward notification in milliseconds
   */
  public int getFeaturedRewardNotificationDuration() {
    if (map.containsKey(NOTIFICATION_POPUP_FEATURED_DURATION)) {
      return (Integer) map.get(NOTIFICATION_POPUP_FEATURED_DURATION) * 1000;
    }
    return 2000;
  }

  public void setDefaultCountry(String country) {
    map.put(DEFAULT_COUNTRY, country.toUpperCase(Locale.getDefault()));
  }

  public String getDefaultCountry() {
    if (map.containsKey(DATA_COUNTRY)) {
      return (String) map.get(DATA_COUNTRY);
    }
    return DEFAULT_COUNTRY;
  }

  public String getLanguage() {
    if (map.containsKey(DATA_LANGUAGE)) {
      return (String) map.get(DATA_LANGUAGE);
    }
    return DEFAULT_LANGUAGE;
  }

  public String getSponsorURL() {
    if (map.containsKey(API_SPONSOR_URL)) {
      return (String) map.get(API_SPONSOR_URL);
    }
    return DEFAULT_SPONSOR_URL;
  }
  
  /**
   * 
   * @return Google API Project number, initiated from manifest file
   */
  public String getGcmProjectNumber(){
    if(map.containsKey(GCM_PROJECT_NUMBER)){
      return (String)map.get(GCM_PROJECT_NUMBER);
    } return null;
  }

  public boolean isLeaderboardViewEnable() {
    if (map.containsKey(VIEW_LEADERBOARD)) {
      return (Boolean) map.get(VIEW_LEADERBOARD);
    }
    return true;
  }

  public boolean isAllowGuestRedeem() {
    if (map.containsKey(ALLOW_GUEST_REDEEM)) {
      return (Boolean) map.get(ALLOW_GUEST_REDEEM);
    }
    return false;
  }

  public Map<String, Object> raw() {
    return map;
  }

}
