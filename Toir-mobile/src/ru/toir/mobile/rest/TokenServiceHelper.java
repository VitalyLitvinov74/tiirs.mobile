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
public class TokenServiceHelper extends ServiceHelperBase {

	/**
	 * 
	 */
	public TokenServiceHelper(Context context, String resultAction) {
		super(context, ProcessorService.Providers.TOKEN_PROVIDER, resultAction);
	}
	
	/**
	 * 
	 * @param tag
	 */
	public void GetTokenByTag(String tag) {
		Bundle bundle = new Bundle();
		bundle.putString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_TAG, tag);
		RunMethod(TokenServiceProvider.Methods.GET_TOKEN_BY_TAG, bundle);
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 */
	public void GetTokenByUsernameAndPassword(String username, String password) {
		Bundle bundle = new Bundle();
		bundle.putString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_USERNAME, username);
		bundle.putString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_PASSWORD, password);
		RunMethod(TokenServiceProvider.Methods.GET_TOKEN_BY_USERNAME_AND_PASSWORD, bundle);
	}

}
