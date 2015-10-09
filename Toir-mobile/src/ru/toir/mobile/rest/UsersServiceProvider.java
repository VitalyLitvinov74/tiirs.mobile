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
public class UsersServiceProvider implements IServiceProvider {

	private final Context mContext;

	public static class Methods {
		public static final int GET_USER = 1;
	}

	public static class Actions {
		public static final String ACTION_GET_USER = "action_get_user";
	}

	public UsersServiceProvider(Context context) {
		mContext = context;
	}

	@Override
	public Bundle RunTask(int method, Bundle extras) {

		Bundle result = new Bundle();
		boolean success;

		switch (method) {
		case Methods.GET_USER:
			success = getUser(extras);
			result.putBoolean(IServiceProvider.RESULT, success);
			return result;
		}

		result.putBoolean(IServiceProvider.RESULT, false);
		return result;
	}

	private boolean getUser(Bundle extras) {
		try {
			return new UsersProcessor(mContext).getUser(extras);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
