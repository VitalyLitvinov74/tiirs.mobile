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
		public static final String  GET_TOKEN_PARAMETER_TAG = "tag";

		public static final int GET_TOKEN_BY_USERNAME_AND_PASSWORD = 2;
		public static final String  GET_TOKEN_PARAMETER_USERNAME = "username";
		public static final String  GET_TOKEN_PARAMETER_PASSWORD = "password";
	}

	/**
	 * 
	 */
	public TokenServiceProvider(Context context) {
		mContext = context;
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rest.IServiceProvider#RunTask(int, android.os.Bundle)
	 */
	@Override
	public boolean RunTask(int method, Bundle extras) {
		switch (method) {
		case Methods.GET_TOKEN_BY_TAG:
			return getTokenByTag(extras);
		case Methods.GET_TOKEN_BY_USERNAME_AND_PASSWORD:
			return getTokenByUsernameAndPassword(extras);
		}
		return false;
	}
	
	/**
	 * 
	 */
	private boolean getTokenByTag(Bundle extras) {
		return new TokenProcessor(mContext).GetTokenByTag(extras);
	}
	
	/**
	 * 
	 */
	private boolean getTokenByUsernameAndPassword(Bundle extras) {
		return new TokenProcessor(mContext).GetTokenByUsernameAndPassword(extras);
	}


}
