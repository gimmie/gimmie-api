package com.gimmie;

import org.json.JSONException;
import org.json.JSONObject;

import com.gimmie.model.GimmieError;

class RawRemoteObject implements RemoteObject {

  private JSONObject mOutput = null;
  private GimmieError mError = null;
  private Configuration mConfiguration = null;

  public RawRemoteObject(String raw, Configuration configuration) {
    mConfiguration = configuration;
    if (raw != null) {
      try {
        JSONObject output = new JSONObject(raw);
        JSONObject response = output.getJSONObject("response");
        boolean success = response.getBoolean("success");
        if (success) {
          mOutput = response;
        } else {
          mError = new GimmieError(output.getJSONObject("error"), configuration);
        }
      } catch (JSONException e) {
        mError = new GimmieError(e, configuration);
      }
    }
  }

  public RawRemoteObject(Exception exception, Configuration configuration) {
    mConfiguration = configuration;
    mError = new GimmieError(exception, configuration);
  }

  public boolean isSuccess() {
    return mOutput != null;
  }

  public JSONObject getOutput() {
    return mOutput;
  }

  public GimmieError getError() {
    return mError;
  }

  public JSONObject raw() {
    return mOutput;
  }

  public Configuration getConfiguration() {
    return mConfiguration;
  }

}
