package com.gimmie;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class CombineResponse implements RemoteObject {

  public static final String FIELD_ACTIONS = "actions";
  public static final String FIELD_BADGES = "badges";
  public static final String FIELD_CATEGORIES = "categories";
  public static final String FIELD_CHECK_IN = "check_in";
  public static final String FIELD_MAYORS = "mayors";
  public static final String FIELD_MAYOR = "mayor";
  public static final String FIELD_USER = "user";

  private JSONObject mObject;
  private Map<String, Object> mCache;
  private Configuration mConfiguration;

  public CombineResponse(JSONObject object, Configuration configuration) {
    mObject = object;
    mCache = new HashMap<String, Object>();
    mConfiguration = configuration;
  }

  @SuppressWarnings("unchecked")
  public <E extends RemoteObject> E getSubObject(Class<E> clazz, String field) {

    E output = null;

    if (mCache.containsKey(field)) {
      output = (E) mCache.get(field);
    } else {
      try {
        JSONObject subObject = mObject.optJSONObject(field);
        if (subObject != null) {
          Constructor<E> constructor = clazz.getConstructor(JSONObject.class,
              Configuration.class);
          output = constructor.newInstance(subObject, mConfiguration);
          mCache.put(field, output);
        }

      } catch (Exception e) {
        Logger.getInstance().verbose(e.getMessage());
      }
    }

    return output;
  }

  @SuppressWarnings("unchecked")
  public <E extends RemoteObject> List<E> getSubArray(Class<E> clazz,
      String field) {
    List<E> output = new ArrayList<E>();
    if (mCache.containsKey(field)) {
      output = (List<E>) mCache.get(field);
    } else {
      JSONArray rawArray = mObject.optJSONArray(field);
      if (rawArray != null) {
        ArrayList<E> objectList = new ArrayList<E>(rawArray.length());
        for (int index = 0; index < rawArray.length(); index++) {
          JSONObject rawObject = rawArray.optJSONObject(index);
          if (rawObject != null) {
            try {
              Constructor<E> constructor = clazz.getConstructor(
                  JSONObject.class, Configuration.class);
              E child = constructor.newInstance(rawObject, mConfiguration);
              objectList.add(child);
            } catch (Exception e) {
              Logger.getInstance().verbose(e.getMessage());
            }
          }

        }

        output = objectList;
        mCache.put(field, output);
      }
    }

    return output;
  }

  @Override
  public JSONObject raw() {
    return mObject;
  }

  public String toString() {
    return mObject.toString();
  }

  @Override
  public Configuration getConfiguration() {
    return mConfiguration;
  }

}
