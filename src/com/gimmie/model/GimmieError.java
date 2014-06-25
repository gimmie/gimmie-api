package com.gimmie.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gimmie.AsyncResult;
import com.gimmie.Configuration;
import com.gimmie.Gimmie;
import com.gimmie.RemoteObject;

/**
 * Error wrapper class for JSONObject and Exception when request to service for
 * handle with getError in {@link AsyncResult}
 * 
 * @author llun
 * 
 */
public class GimmieError implements RemoteObject {

  public static final String EXCEPTION_CODE = "-1";

  private JSONObject mRawError;
  private Exception mException;
  private Configuration mConfiguration;

  /**
   * Create GimmieError from JSONObject return by service
   * 
   * @param rawError
   *          JSON response from service
   * @param configuration
   *          Gimmie configuration object
   */
  public GimmieError(JSONObject rawError, Configuration configuration) {
    mRawError = rawError;
    mConfiguration = configuration;
  }

  /**
   * Create GimmieError from Exception
   * 
   * @param exception
   *          Exception object need to handle
   * @param configuration
   *          Gimmie configuration object
   */
  public GimmieError(Exception exception, Configuration configuration) {
    mException = exception;
    mConfiguration = configuration;
  }

  /**
   * Indicate is this error contains Exception
   * 
   * @return true, if this error contain Exception object, otherwise false.
   */
  public boolean isExceptionType() {
    return mException != null;
  }

  /**
   * Get exception object in this error.
   * 
   * @return exception object
   */
  public Exception getException() {
    return mException;
  }

  /**
   * Get JSON object return from service
   * 
   * @return JSONObject
   */
  public JSONObject getErrorJSON() {
    return mRawError;
  }

  /**
   * Get error message in JSONObject or Exception in case this error is
   * exception type
   * 
   * @return message in JSONObject or Exception
   */
  public String getMessage() {
    String message = "";
    if (isExceptionType()) {
      message = mException.getMessage();
    } else {
      try {
        message = mRawError.getString("message");
      } catch (JSONException e) {
        Log.d(Gimmie.LOG_TAG, "Can't process JSON error", e);
      }
    }
    return message;
  }

  /**
   * Error code in JSONObject or {@link #EXCEPTION_CODE} if this is exception
   * type.
   * 
   * @return error code as String
   */
  public String getCode() {
    String code = "-1";
    if (isExceptionType()) {
      code = EXCEPTION_CODE;
    } else {
      try {
        code = mRawError.getString("code");
      } catch (JSONException e) {
        Log.d(Gimmie.LOG_TAG, "Can't process JSON error", e);
      }
    }
    return code;
  }

  @Override
  public String toString() {
    if (mException != null) {
      return mException.toString();
    } else {
      return mRawError.toString();
    }
  }

  public JSONObject raw() {
    return mRawError;
  }

  public Configuration getConfiguration() {
    return mConfiguration;
  }

}
