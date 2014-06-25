package com.gimmie.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gimmie.Configuration;
import com.gimmie.Gimmie;

/**
 * Category model returns from Gimmie service in
 * {@link Gimmie#loadCategory(android.os.Handler, com.gimmie.AsyncResult)} api.
 * 
 * @author llun
 * 
 */
public class Category extends IDBaseObject {

  private Reward[] rewardsCache;
  private String countryCode;

  /**
   * Create Category object from JSONObject return by service
   * 
   * @param object
   *          JSON response from service
   */
  public Category(JSONObject object, Configuration configuration) {
    this(object, configuration, configuration.getDefaultCountry());
  }

  /**
   * Create Category object from JSONObject and country code for filter reward
   * that available only specific country.
   * 
   * @param object
   *          JSON response from service
   * @param configuration
   *          Gimmie configuration object
   * @param country
   *          country code e.g. TH, SG or global
   */
  public Category(JSONObject object, Configuration configuration,
      String countryCode) {
    super(object, configuration);

    this.countryCode = countryCode;
  }

  /**
   * Get category name
   * 
   * @return category name as string
   */
  public String getName() {
    return mObject.optString("name");
  }

  /**
   * Get country filter code
   * 
   * @return country code filter
   */
  public String getCountry() {
    return countryCode;
  }

  /**
   * Get list of {@link Reward} as array
   * 
   * @return array of reward
   */
  public Reward[] getRewards() {
    if (rewardsCache == null) {
      try {
        JSONArray rewards = mObject.getJSONArray("rewards");

        ArrayList<Reward> featuredRewards = new ArrayList<Reward>(
            rewards.length());
        ArrayList<Reward> normalRewards = new ArrayList<Reward>(
            rewards.length());
        ArrayList<Reward> soldOutRewards = new ArrayList<Reward>(
            rewards.length());

        for (int index = 0; index < rewards.length(); index++) {
          final Reward reward = new Reward(rewards.getJSONObject(index),
              mConfiguration);

          List<Reward> targetList = normalRewards;
          if (reward.isSoldOut()) {
            targetList = soldOutRewards;
          } else if (reward.isFeatured()) {
            targetList = featuredRewards;
          }

          String countries[] = reward.getCountries();
          if (countries.length > 0) {
            int position = Arrays.binarySearch(countries,
                Configuration.DATA_COUNTRY_GLOBAL);
            if (position < 0) {
              position = Arrays.binarySearch(countries, countryCode,
                  new Comparator<String>() {

                    @Override
                    public int compare(String lhs, String rhs) {
                      return lhs.compareToIgnoreCase(rhs);
                    }

                  });
              if (position >= 0) {
                targetList.add(reward);
              }
            } else {
              targetList.add(reward);
            }
          } else {
            targetList.add(reward);
          }

        }

        Collections.shuffle(featuredRewards);
        Collections.shuffle(normalRewards);
        
        ArrayList<Reward> finalList = new ArrayList<Reward>(rewards.length());
        finalList.addAll(featuredRewards);
        finalList.addAll(normalRewards);
        finalList.addAll(soldOutRewards);

        rewardsCache = finalList.toArray(new Reward[finalList.size()]);
        return rewardsCache;
      } catch (JSONException e) {
        Log.e("Gimmie", "Something wrong", e);
        return new Reward[0];
      }
    } else {
      return rewardsCache;
    }

  }

}
