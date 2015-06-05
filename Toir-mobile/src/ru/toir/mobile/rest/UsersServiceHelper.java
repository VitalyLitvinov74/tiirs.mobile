/**
 * 
 */
package ru.toir.mobile.rest;

import android.content.Context;
import android.os.Bundle;

/**
 * @author Dmitriy Logachov
 *
 */
public class UsersServiceHelper extends ServiceHelperBase {

	/**
	 * @param context
	 * @param providerId
	 * @param resultAction
	 */

	public UsersServiceHelper(Context context, String resultAction) {
		super(context, ProcessorService.Providers.USERS_PROVIDER, resultAction);
	}
	
	/**
	 * 
	 * @param tag
	 */
	public void GetUser(String tag) {
		Bundle bundle = new Bundle();
		bundle.putString(UsersServiceProvider.Methods.GET_USER_PARAMETER_TAG, tag);
		RunMethod(UsersServiceProvider.Methods.GET_USER, bundle);
	}

}
