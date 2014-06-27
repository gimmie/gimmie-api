package com.gimmie;

import com.gimmie.model.Activities;
import com.gimmie.model.Badge;
import com.gimmie.model.Category;
import com.gimmie.model.Claim;
import com.gimmie.model.Event;
import com.gimmie.model.GimmieError;
import com.gimmie.model.Profile;
import com.gimmie.model.RecentAction;
import com.gimmie.model.Reward;
import com.gimmie.model.TopPlayer;
import com.gimmie.trackers.Tracker;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provide methods for Gimmie API. Before using components or any API, Have to
 * initial Gimmie instance and login with unique user id. Gimmie instance create
 * with context which provide by your application Gimmie read configuration from
 * gimmie.cfg or AndroidManifest.xml file. Gimmie will try read from gimmie.cfg
 * file first and if file is not found, will check AndroidManifest.xml
 * meta-data.
 * 
 * gimmie.cfg method, creaete gimmie.cfg file under assets folder with below
 * format. <strong>api</strong> key is an optional.
 * 
 * <pre>
 * {@code
 * 	{
 *    "key"   : "oauth_key",
 *    "secret": "oauth_secret",
 *    "api"   : "api_url"
 *  }
 * }
 * </pre>
 * 
 * AndroidManifest.xml methods, add meta-data tag under your application with
 * below format.
 * 
 * <pre>
 * {@code
 * <meta-data android:name="com.gimmie.api.key" android:value="oauth_key_from_portal" />
 * <meta-data android:name="com.gimmie.api.secret" android:value="oauth_secret_from_portal" />
 * <meta-data android:name="com.gimmie.api.url" android:value="gimmie api url" />
 * 
 * <meta-data android:name="com.gimmie.notification.system" android:value="true|false" />
 * <meta-data android:name="com.gimmie.notification.id" android:value="integer" />
 * <meta-data android:name="com.gimmie.notification.toast" android:value="true|false" />
 * <meta-data android:name="com.gimmie.notification.popup" android:value="true|false" />
 * 
 * <meta-data android:name="com.gimmie.data.default_country" android:value="country_code" />
 * <meta-data android:name="com.gimmie.data.language" android:value="locale_code" />
 * }
 * </pre>
 * 
 * and get Gimmie object by {@link #getInstance(Context)} or passing oauth_key
 * and oauth_secret directly to getInstance method by
 * {@link #getInstance(Context, String, String)}. After get instance use login
 * method and passing user unique id for login.
 * 
 * The API URL is in format https://api.gimmieworld.com/1/, so if the domain is
 * change to other e.g. api-service.gimmieworld.com the API will be
 * https://api-service.gimmieworld.com.
 * 
 * <pre>
 * Gimmie gimmie = Gimmie.getInstance(activity);
 * gimmie.login(user_unique_id);
 * </pre>
 * 
 * If user doesn't login, the API can using for fetching rewards catalogue but
 * can't trigger or get profile because there is no user information.
 * 
 * @author llun
 * 
 */
public class Gimmie {

  public static final String LOG_TAG = "Gimmie";

  public static final String LOGIN_INFORMATION_NAME = "name";
  public static final String LOGIN_INFORMATION_EMAIL = "email";
  private static final String KEY_LOGIN_NAME = "com.gimmie.login";

  public static final String TOP_POINTS = "points";
  public static final String TOP_REDEMPTION_PRICES = "prices";
  public static final String TOP_REDEMPTION_COUNT = "redemptions_count";

  private static final String PARAMETER_OUTPUT_KEY = "output";
  private static final String PARAMETER_ERROR_KEY = "error";

  private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

  private Tracker mTracker;
  private String mCountry;
  private String mLocale;

  private String mDeviceID;
  private String mUser = "";

  private Configuration mConfiguration;

  private Map<String, String> mAdditionInformation = new HashMap<String, String>(
      2);

  private static Gimmie mInstance;

  public static Gimmie getInstance(Configuration configuration) {
    if (mInstance == null) {
      mInstance = new Gimmie(configuration);
    }
    return mInstance;
  }

  Gimmie(Configuration configuration) {
    mConfiguration = configuration;
  }

  /**
   * Login with device unique ID or generated ID for device that doesn't support
   * ANDROID_ID.
   */
  public void login() {
    login(mDeviceID);
  }

  /**
   * Login user to Gimmie service
   * 
   * @param user
   *          unique user id
   */
  public void login(String user) {
    login(user, null);
  }

  public void login(String user, String name, String email) {
    HashMap<String, String> additionInformation = new HashMap<String, String>(2);
    if (name != null) {
      additionInformation.put(LOGIN_INFORMATION_NAME, name);
    }

    if (email != null) {
      additionInformation.put(LOGIN_INFORMATION_EMAIL, email);
    }

    login(user, additionInformation);
  }

  public void login(String user, Map<String, String> additionInformation) {
    mUser = user.trim();
    mTracker.login(mUser);

    mAdditionInformation.clear();
    if (additionInformation != null) {
      mAdditionInformation.putAll(additionInformation);
    }
  }

  public void loginAndTransferFromGuest(String user, String name, String email) {
    String guestUserID = mUser;
    login(user, name, email);
    if (guestUserID.startsWith("guest:")) {
      transferDataFromGuestID(guestUserID);
    }
  }

  /**
   * Logout user from Gimmie service
   */
  public void logout() {
    mUser = "";
    mTracker.logout();
  }

  /**
   * Set country for showing rewards only available in specific country. Default
   * value is global.
   * 
   * @param country
   *          Country code e.g. SG, US or Global. Invalid code will cause
   *          rewards catalogue empty
   */
  public void setCountry(String country) {
    mCountry = country.toLowerCase(Locale.US);
  }

  /**
   * Set network result language
   *
   * @param locale
   *          Country locale code e.g. EN, TH.
   */
  public void setLocale(String locale) {
    mLocale = locale.toLowerCase(Locale.US);
  }

  /**
   * Get generate login id from {@link #login()} method. This login id won't
   * change even logout.
   *
   * @return generate login as String
   */
  public String getGeneratedLogin() {
    return mDeviceID;
  }

  /**
   * Current logged in user
   *
   * @return string that pass to login method or empty string
   */
  public String getUser() {
    return mUser;
  }

  public String getUserInformation(String key) {
    return mAdditionInformation.get(key);
  }

  /**
   * Check is user already login
   *
   * @return true if already login otherwise false
   */
  public boolean isLoggedIn() {
    return mUser != null && mUser.length() > 0;
  }

  /**
   * Get user profile from Gimmie service
   *
   * @param result
   *          Profile result object from Gimmie service
   */
  public void getProfile(final AsyncResult<Profile> result) {
    invoke("profile", null, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null)
          return;

        if (rawResult.isSuccess()) {
          result.getResult(new Profile(rawResult.getOutput(),
                  mConfiguration));
        } else {
          result.getError(rawResult.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        if (result != null) result.getError(error);
      }
    });
  }

  /**
   * List all reward categories from Gimmie service
   * 
   * @param result
   *          List of categories object in collection result.
   */
  public void loadCategory(final AsyncResult<RemoteCollection<Category>> result) {
    invoke("categories", null, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null)
          return;

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          try {
            JSONArray rawCategories = raw.getJSONArray("categories");
            ArrayList<Category> categoryList = new ArrayList<Category>(
                    rawCategories.length());
            for (int index = 0; index < rawCategories.length(); index++) {
              JSONObject rawStore = rawCategories.getJSONObject(index);

              Category category = new Category(rawStore, mConfiguration,
                      mCountry);
              categoryList.add(category);
            }
            result.getResult(new RemoteCollection<Category>(categoryList
                    .toArray(new Category[rawCategories.length()]),
                    mConfiguration
            ));
          } catch (JSONException e) {
            result.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          result.getError(rawResult.getError());
        }

      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (result != null) result.getError(error);
      }
    });
  }

  /**
   * Checking with given mayorship id and venue
   * 
   * @param id
   * @param venue
   */
  public void checkin(String id, String venue,
      final AsyncResult<CombineResponse> result) {
    HashMap<String, String> parameters = new HashMap<String, String>(1);
    parameters.put("venue", venue);

    invoke("check_in/" + id, parameters, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {

        if (result == null) return;

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          Logger.getInstance().verbose("Trigger response: " + raw.toString());
          CombineResponse response = new CombineResponse(raw, mConfiguration);
          result.getResult(response);
        } else {
          result.getError(rawResult.getError());
        }

      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        Logger.getInstance().error(error.toString());
        if (error.isExceptionType()) {
          Logger.getInstance().error(error.getException().getMessage(), error.getException());
        }
        if (result != null) result.getError(error);
      }
    });
  }

  /**
   * Trigger event to Gimmie service with given event name and waiting for get a
   * result.
   *
   * @param eventName
   *          Game event name register in Gimmie portal under game menu
   * @param result
   *          Actions result configure in Gimmie portal
   */
  public void trigger(final String eventName,
      final AsyncResult<CombineResponse> result) {
    HashMap<String, String> input = new HashMap<String, String>(1);
    input.put("event_name", eventName);

    Logger.getInstance().verbose("Trigger event: " + eventName);
    invoke("trigger", input, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null) return;

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          Logger.getInstance().verbose("Trigger response: " + raw.toString());
          CombineResponse response = new CombineResponse(raw, mConfiguration);
          result.getResult(response);
        } else {
          result.getError(rawResult.getError());
        }

      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        Logger.getInstance().error(error.toString());
        if (error.isExceptionType()) {
          Logger.getInstance().error(error.getException().getMessage(), error.getException());
        }

        if (result != null) result.getError(error);
      }

    });

  }

  /**
   * Trigger event to Gimmie service with given event id and waiting for get a
   * result.
   *
   * @param eventID
   *          Game event id register in Gimmie portal under game menu
   * @param result
   *          Actions result configure in Gimmie portal
   */
  public void trigger(final int eventID,
      final AsyncResult<CombineResponse> result) {
    HashMap<String, String> input = new HashMap<String, String>(1);
    input.put("event_id", String.format("%d", eventID));

    Logger.getInstance().verbose("Trigger event: " + eventID);
    invoke("trigger", input, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {

        if (result == null) return;

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          Logger.getInstance().verbose("Trigger response: " + raw.toString());
          CombineResponse response = new CombineResponse(raw, mConfiguration);
          result.getResult(response);
        } else {
          result.getError(rawResult.getError());
        }

      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        Logger.getInstance().error(error.toString());
        if (error.isExceptionType()) {
          Logger.getInstance().error(error.getException().getMessage(), error.getException());
        }
        if (result != null) result.getError(error);
      }

    });
  }

  /**
   * Redeem reward with reward id and wait for response
   *
   * @param rewardID
   * @param result
   */
  public void redeem(final int rewardID,
      final AsyncResult<Claim> result) {
    HashMap<String, String> input = new HashMap<String, String>(1);
    input.put("reward_id", String.format("%d", rewardID));
    input.put("email", "1");

    invoke("redeem", input, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {

        if (result == null)
          return;

        if (rawResult.isSuccess()) {
          JSONObject object = rawResult.getOutput();
          try {
            result.getResult(new Claim(object.getJSONObject("claim"),
                    mConfiguration));
          } catch (JSONException e) {
            result.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          result.getError(rawResult.getError());
        }

      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (result != null) result.getError(error);
      }

    });
  }

  /**
   * Get reward information with specific reward id
   *
   * @param rewardID
   *          reward that user want to get information
   * @param result
   *          Result with reward object.
   */
  public void loadReward(final int rewardID,
      final AsyncResult<Reward> result) {
    HashMap<String, String> input = new HashMap<String, String>(1);
    input.put("reward_id", String.format("%d", rewardID));

    invoke("rewards", input, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null)
          return;

        if (rawResult.isSuccess()) {
          JSONObject object = rawResult.getOutput();
          try {
            JSONArray array = object.getJSONArray("rewards");
            if (array.length() > 0) {
              JSONObject first = array.getJSONObject(0);
              result.getResult(new Reward(first, mConfiguration));
            } else {
              result.getResult(null);
            }
          } catch (JSONException e) {
            result.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          result.getError(rawResult.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (result != null) result.getError(error);
      }

    });
  }

  /**
   * Load claim, a reward that already redeemed by user, with claim id.
   *
   * @param claimID
   *          claim id return when redeem item or list in profile
   * @param result
   *          Claim object information
   */
  public void loadClaim(final int claimID,
      final AsyncResult<Claim> result) {
    HashMap<String, String> input = new HashMap<String, String>(1);
    input.put("claim_id", String.format("%d", claimID));

    invoke("claims", input, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null)
          return;

        if (rawResult.isSuccess()) {
          JSONObject object = rawResult.getOutput();
          try {
            JSONArray array = object.getJSONArray("claims");
            if (array.length() > 0) {
              JSONObject first = array.getJSONObject(0);
              result.getResult(new Claim(first, mConfiguration));
            } else {
              result.getResult(null);
            }
          } catch (JSONException e) {
            result.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          result.getError(rawResult.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (result != null) result.getError(error);
      }

    });
  }

  /**
   * List all events available for game.
   *
   * @param result
   *          List of all events in collection
   */
  public void loadEvents(final AsyncResult<RemoteCollection<Event>> result) {
    invoke("events", null, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null)
          return;

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          try {
            JSONArray rawEvents = raw.getJSONArray("events");
            ArrayList<Event> eventList = new ArrayList<Event>(rawEvents
                    .length());
            for (int index = 0; index < rawEvents.length(); index++) {
              JSONObject rawEvent = rawEvents.getJSONObject(index);
              Event event = new Event(rawEvent, mConfiguration);
              eventList.add(event);
            }
            result.getResult(new RemoteCollection<Event>(eventList
                    .toArray(new Event[rawEvents.length()]), mConfiguration));
          } catch (JSONException e) {
            result.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          result.getError(rawResult.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().error(error.getMessage(), error.getException());
        if (result != null) result.getError(error);
      }
    });
  }

  /**
   * @deprecated Use {@link Gimmie#loadRecentActivities(Handler, AsyncResult)}
   *             instead
   * 
   *             Load latest 20 recent action trigger by user in application
   *
   * @param result
   *          Last 20 recent action in collection
   */
  public void loadRecentActions(final AsyncResult<RemoteCollection<RecentAction>> result) {
    invoke("recent_actions", null, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null)
          return;

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          try {
            JSONArray rawRecentActions = raw.getJSONArray("recent_actions");
            ArrayList<RecentAction> actionList = new ArrayList<RecentAction>(
                    rawRecentActions.length());
            for (int index = 0; index < rawRecentActions.length(); index++) {
              JSONObject rawEvent = rawRecentActions.getJSONObject(index);
              RecentAction action = new RecentAction(rawEvent,
                      mConfiguration);
              actionList.add(action);
            }
            result.getResult(new RemoteCollection<RecentAction>(actionList
                    .toArray(new RecentAction[rawRecentActions.length()]),
                    mConfiguration));
          } catch (JSONException e) {
            result.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          result.getError(rawResult.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (result != null) result.getError(error);
      }

    });
  }

  /**
   * Load latest 20 recent activities trigger by user in application
   * 
   * @param handler
   *          Android Handler for sending a message when get response from
   *          Gimmie service
   * @param result
   *          Last 20 recent activities in collection
   */
  public void loadRecentActivities(final Handler handler,
      final AsyncResult<RemoteCollection<Activities>> result) {
    invoke("recent_activities", null, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null)
          return;

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          try {
            JSONArray rawRecentActivitiesArray = raw
                    .getJSONArray("recent_activities");
            ArrayList<Activities> recentActivitiesList = new ArrayList<Activities>(
                    rawRecentActivitiesArray.length());
            for (int index = 0; index < rawRecentActivitiesArray.length(); index++) {
              JSONObject rawActivity = rawRecentActivitiesArray
                      .getJSONObject(index);
              Activities action = new Activities(rawActivity,
                      mConfiguration);
              recentActivitiesList.add(action);
            }
            result.getResult(new RemoteCollection<Activities>(
                    recentActivitiesList
                            .toArray(new Activities[rawRecentActivitiesArray
                                    .length()]), mConfiguration));
          } catch (JSONException e) {
            result.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          result.getError(rawResult.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (result != null) result.getError(error);
      }

    });
  }

  /**
   * Load top 20 users
   *
   * @param type
   *          top 20 type, can be {@link #TOP_POINTS},
   *          {@link #TOP_REDEMPTION_PRICES} or {@link #TOP_REDEMPTION_COUNT}
   * @param players
   *          Top 20 players
   */
  public void loadTop20(final String type,
      final AsyncResult<RemoteCollection<TopPlayer>> players) {
    invoke("top20" + type, null, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (players == null) return;

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          try {
            JSONArray rawPlayers = raw.getJSONArray("players");
            ArrayList<TopPlayer> playerList = new ArrayList<TopPlayer>(
                    rawPlayers.length());
            for (int index = 0; index < rawPlayers.length(); index++) {
              JSONObject rawPlayer = rawPlayers.getJSONObject(index);
              TopPlayer player = new TopPlayer(rawPlayer, mConfiguration);
              playerList.add(player);
            }
            players.getResult(new RemoteCollection<TopPlayer>(playerList
                    .toArray(new TopPlayer[rawPlayers.length()]),
                    mConfiguration));
          } catch (JSONException e) {
            players.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          players.getError(rawResult.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (players != null) players.getError(error);
      }

    });
  }

  /**
   * Load all badges available
   *
   * @param result
   *          badges in collection. All categories and tiers in one flat
   *          collection
   * @param progressRequired
   *          Set to true if progress is required. Will increase loading time
   */
  public void loadBadges(final AsyncResult<RemoteCollection<Badge>> result,
      final boolean progressRequired) {
    HashMap<String, String> map = null;
    if (progressRequired) {
      map = new HashMap<String, String>();
      map.put("progress", "1");
    }

    invoke("badges", map, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null)
          return;
        ArrayList<Badge> badgeList = new ArrayList<Badge>();
        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          try {
            JSONObject rawBadgeCategories = raw.getJSONObject("badges");
            JSONArray badgeCatNames = rawBadgeCategories.names();
            for (int cat = 0; cat < badgeCatNames.length(); cat++) {
              JSONArray rawBadgeTiers = rawBadgeCategories
                      .getJSONArray(badgeCatNames.getString(cat));
              for (int tier = 0; tier < rawBadgeTiers.length(); tier++) {
                JSONArray rawBadgesArray = rawBadgeTiers.optJSONArray(tier);
                for (int i = 0; i < rawBadgesArray.length(); i++) {
                  JSONObject rawBadge = rawBadgesArray.getJSONObject(i);
                  Badge badge = new Badge(rawBadge, mConfiguration);
                  badgeList.add(badge);
                }
              }
            }

            result.getResult(new RemoteCollection<Badge>(badgeList
                    .toArray(new Badge[badgeList.size()]), mConfiguration));
          } catch (JSONException e) {
            result.getError(new GimmieError(e, mConfiguration));
          }
        } else {
          result.getError(rawResult.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (result != null) result.getError(error);
      }

    });
  }

  /**
   * Merge all guest user data to application logged in user. Relate issue: #796
   * 
   * @param guestUserID
   */
  public void transferDataFromGuestID(final String guestUserID) {
    transferDataFromGuestID(null, guestUserID, null);
  }

  /**
   * Merge all guest user data to application logged in user. Relate issue: #796
   *
   * @param guestUserID
   * @param profile
   */
  public void transferDataFromGuestID(final String guestUserID, final AsyncResult<Profile> profile) {

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("old_uid", guestUserID);

    invoke("login", map, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject result) {
        if (profile == null) return;

        if (result.isSuccess()) {
          profile.getResult(new Profile(result.getOutput(), mConfiguration));
        } else {
          profile.getError(result.getError());
        }
      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        if (profile != null) profile.getError(error);
      }

    });

  }

  public void sendRegIdToPortal(final String regid, final AsyncResult<CombineResponse> result) {
    HashMap<String, String> input = new HashMap<String, String>(1);
    input.put("target", "android");
    input.put("id", regid);
    invoke("register_token", input, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {
        if (result == null) return;

        final HashMap<String, Object> parameters = new HashMap<String, Object>(
            2);

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          Logger.getInstance().verbose("Register_token response: " + raw.toString());
          CombineResponse response = new CombineResponse(raw, mConfiguration);
          result.getResult(response);
        } else {
          result.getError(rawResult.getError());
        }

      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        Logger.getInstance().error(error.toString());
        if (error.isExceptionType()) {
          Logger.getInstance().error(error.getException().getMessage(), error.getException());
        }
        if (result != null) result.getError(error);
      }

    });
  }

  public void getNotificationToken(final AsyncResult<CombineResponse> result) {
    invoke("notification_token", null, new AsyncResult<RawRemoteObject>() {

      @Override
      public void getResult(final RawRemoteObject rawResult) {

        if (result == null) return;

        final HashMap<String, Object> parameters = new HashMap<String, Object>(
            2);

        if (rawResult.isSuccess()) {
          JSONObject raw = rawResult.getOutput();
          CombineResponse response = new CombineResponse(raw, mConfiguration);
          result.getResult(response);
        } else {
          result.getError(rawResult.getError());
        }

      }

      @Override
      public void getError(final GimmieError error) {
        Logger.getInstance().verbose("Error: " + error.getMessage());
        Logger.getInstance().error(error.toString());
        if (error.isExceptionType()) {
          Logger.getInstance().error(error.getException().getMessage(), error.getException());
        }

        if (result != null) result.getError(error);
      }

    });
  }

  private void invoke(final String target,
      final Map<String, String> parameters,
      final AsyncResult<RawRemoteObject> result) {
    Logger.getInstance().verbose("Call: " + target);
    mExecutor.execute(new Runnable() {

      @Override
      public void run() {

        StringBuilder url = new StringBuilder();
        url.append(mConfiguration.getURL());
        url.append(target);

        HashMap<String, String> mixinMap = new HashMap<String, String>();
        mixinMap.put("ua", Configuration.SDK_VERSION);
        if (parameters != null) {
          mixinMap.putAll(parameters);
        }
        if (mLocale != null && mLocale.length() > 0) {
          mixinMap.put("locale", mLocale);
        }

        url.append(".json?");
        LinkedList<NameValuePair> params = new LinkedList<NameValuePair>();
        for (String key : mixinMap.keySet()) {
          params.add(new BasicNameValuePair(key, mixinMap.get(key)));
        }

        String paramString = URLEncodedUtils.format(params, "utf-8");
        url.append(paramString);

        Logger.getInstance().verbose("Get: " + url.toString());

        OAuthService service = new ServiceBuilder().provider(GimmieApi.class)
                .apiKey(mConfiguration.getKey())
                .apiSecret(mConfiguration.getSecret()).build();

        Token token = new Token(mUser, mConfiguration.getSecret());
        OAuthRequest request = new OAuthRequest(Verb.GET, url.toString());
        service.signRequest(token, request);
        Response response = request.send();

        result.getResult(new RawRemoteObject(response.getBody(), mConfiguration));
      }
    });
  }

  public Configuration getConfiguration() {
    return mConfiguration;
  }

  public Tracker getTracker() {
    return mTracker;
  }

  public static class GimmieApi extends DefaultApi10a {

    private static final String AUTHORIZE_URL = "http://api.gimmieworld.com/oauth/authorize?token=%s";

    @Override
    public String getAccessTokenEndpoint() {
      return "http://api.gimmieworld.com/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(Token token) {
      return String.format(AUTHORIZE_URL, token.getToken());

    }

    @Override
    public String getRequestTokenEndpoint() {
      return "http://api.gimmieworld.com/oauth/request_token";
    }

  }

}
