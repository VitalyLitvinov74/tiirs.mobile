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
public class TokenServiceProvider implements IServiceProvider {

	private final Context mContext;

	public static class Methods {
		public static final int GET_TOKEN_BY_TAG = 1;
		public static final String GET_TOKEN_PARAMETER_TAG = "tag";

		public static final int GET_TOKEN_BY_USERNAME_AND_PASSWORD = 2;
		public static final String GET_TOKEN_PARAMETER_USERNAME = "username";
		public static final String GET_TOKEN_PARAMETER_PASSWORD = "password";
	}

	public static class Actions {
		public static final String ACTION_GET_TOKEN = "action_get_token";
	}

	/**
	 * 
	 */
	public TokenServiceProvider(Context context) {
		mContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rest.IServiceProvider#RunTask(int, android.os.Bundle)
	 */
	@Override
	public Bundle RunTask(int method, Bundle extras) {

		switch (method) {
		case Methods.GET_TOKEN_BY_TAG:
			return getTokenByTag(extras);
		case Methods.GET_TOKEN_BY_USERNAME_AND_PASSWORD:
			return getTokenByUsernameAndPassword(extras);
		}

		Bundle result = new Bundle();
		result.putBoolean(RESULT, false);
		result.putString(MESSAGE, "Запуск не существующей задачи сервиса.");
		return result;

	}

	/**
	 * 
	 */
	private Bundle getTokenByTag(Bundle extras) {
		try {
			return new TokenProcessor(mContext).getTokenByTag(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getTokenByUsernameAndPassword(Bundle extras) {
		try {
			return new TokenProcessor(mContext)
					.getTokenByUsernameAndPassword(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

}
