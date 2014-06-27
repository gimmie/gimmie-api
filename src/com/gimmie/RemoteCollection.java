package com.gimmie;

import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * RemoteObject implementation for object list response from remote service
 * 
 * @author llun
 * 
 * @param <E>
 *          Class extends from RempteObject that live in collection list
 */
public class RemoteCollection<E extends RemoteObject> implements RemoteObject {

  private E[] mCollection;
  private Configuration mConfiguration;

  /**
   * Create RemoteCollection with array of remote object specify by generic
   * parameters.
   * 
   * @param collection
   *          Array of object extends RemoteObject specify by generic parameter
   */
  public RemoteCollection(E[] collection, Configuration configuration) {
    mCollection = collection;
    mConfiguration = configuration;
  }

  /**
   * Get list of remote object as array.
   * 
   * @return Array of object extends RemoteObject specify by generic parameter
   */
  public E[] getCollection() {
    return mCollection;
  }

  public JSONObject raw() {
    JSONArray array = new JSONArray(Arrays.asList(mCollection));
    HashMap<String, JSONArray> collectionOutput = new HashMap<String, JSONArray>(
        1);
    collectionOutput.put("array", array);

    JSONObject object = new JSONObject(collectionOutput);
    return object;
  }

  public Configuration getConfiguration() {
    return mConfiguration;
  }

}
