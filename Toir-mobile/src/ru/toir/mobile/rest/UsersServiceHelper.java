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
	public void getUser() {
		Bundle bundle = new Bundle();
		RunMethod(UsersServiceProvider.Methods.GET_USER, bundle);
	}

}
