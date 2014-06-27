package com.gimmie;

import com.gimmie.model.GimmieError;

/**
 * Asynchronous response, all output from Gimmie API return with this object
 * format. Using Gimmie API have to extends this by anonymous class and
 * implements both methods or using {@link BaseResult} for ignore error.
 * 
 * @author llun
 * 
 * @param <E>
 *            Class extends from RemoteObject (Currently is all Gimmie model
 *            classes)
 */
public interface AsyncResult<E extends RemoteObject> {

	/**
	 * Get remote object from service
	 * 
	 * @param result
	 *            Class extends from RemoteObject
	 */
	public void getResult(E result);

	/**
	 * Get error's happen in network or request.
	 * 
	 * @param error
	 *            Error object with message explain the error
	 */
	public void getError(GimmieError error);

}
