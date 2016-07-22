package com.gimmie;

import com.gimmie.model.GimmieError;
import com.gimmie.trackers.NullTracker;
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
 * Provide methods for Gimmie API.
 * <p>
 * The API URL is in format https://api.gimmieworld.com/1/, so if the domain is
 * change to other e.g. api-service.gimmieworld.com the API will be
 * https://api-service.gimmieworld.com.
 * <p>
 * <pre>
 * Gimmie gimmie = Gimmie.getInstance();
 * gimmie.login(user_unique_id);
 * </pre>
 * <p>
 * If user doesn't login, the API can using for fetching rewards catalogue but
 * can't trigger or get profile because there is no user information.
 *
 * @author llun
 */
public class Gimmie {

  public static final String LOG_TAG = "Gimmie";

  public static final String LOGIN_INFORMATION_NAME = "name";
  public static final String LOGIN_INFORMATION_EMAIL = "email";

  public static final String TOP_POINTS = "points";
  public static final String TOP_REDEMPTION_PRICES = "prices";
  public static final String TOP_REDEMPTION_COUNT = "redemptions_count";

  private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

  private String mUser = "";

  private Configuration mConfiguration;
  private Tracker mTracker;

  private Map<String, String> mAdditionInformation = new HashMap<String, String>(
          2);

  private static Gimmie mInstance;

  public static Gimmie getInstance(Configuration configuration) {
    return getInstance(configuration, new NullTracker(Logger.getInstance()));
  }

  public static Gimmie getInstance(Configuration configuration, Tracker tracker) {
    if (mInstance == null) {
      mInstance = new Gimmie(configuration, tracker);
    }
    return mInstance;
  }

  Gimmie(Configuration configuration, Tracker tracker) {
    mConfiguration = configuration;
    mTracker = tracker;
  }

  /**
   * Login user to Gimmie service
   *
   * @param user unique user id
   */
  public void login(String user) {
    login(user, null);
  }

  public void login(String user, Map<String, String> additionInformation) {
    mUser = user.trim();
    mTracker.login(mUser);

    mAdditionInformation.clear();
    if (additionInformation != null) {
      mAdditionInformation.putAll(additionInformation);
    }
  }

  /**
   * Logout user from Gimmie service
   */
  public void logout() {
    mUser = "";
    mTracker.logout();
  }

  public void trigger(String eventName) {
    trigger(eventName, new HashMap<String, String>(), null);
  }

  /**
   * Trigger event to Gimmie service with given event name and waiting for get a
   * result.
   *
   * @param eventName Game event name register in Gimmie portal under game menu
   * @param data      Event data which will end up as user properties
   * @param result    Actions result configure in Gimmie portal
   */
  public void trigger(final String eventName,
                      final HashMap<String, String> data,
                      final AsyncResult<CombineResponse> result) {
    JSONObject dataJSON = new JSONObject(data);
    HashMap<String, String> input = new HashMap<String, String>(2);
    input.put("event_name", eventName);
    input.put("event_data", dataJSON.toString());

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
        if (mConfiguration.getLanguage() != null && mConfiguration.getLanguage().length() > 0) {
          mixinMap.put("locale", mConfiguration.getLanguage());
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
