package com.gimmie;

import android.util.Log;

import com.gimmie.model.GimmieError;

/**
 * Default implementation of AsyncResult that ignore all error that cause in
 * network or response from Gimmie. Need to extend this class and implements
 * {@link #getResult(RemoteObject)}.
 * 
 * @author llun
 * 
 * @param <E>
 *            Class extends from RemoteObject
 */
public abstract class BaseResult<E extends RemoteObject> implements
		AsyncResult<E> {

	/**
	 * Show error in log but don't do anything. Extend this class and overide
	 * this method if you want to handle error.
	 * 
	 * @param error
	 *            Error object with message explain the error
	 */
	@Override
  public void getError(GimmieError error) {
    Logger.getInstance().debug(String.format("Got an error: %s", error.getMessage()));
	}

}
