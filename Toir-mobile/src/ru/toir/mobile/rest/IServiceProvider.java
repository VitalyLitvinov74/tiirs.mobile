/**
 * 
 */
package ru.toir.mobile.rest;

import android.os.Bundle;

/**
 * @author Dmitriy Logachov
 * 
 */
public interface IServiceProvider {

	public static final String RESULT = "result";
	public static final String MESSAGE = "message";

	/**
	 * 
	 * @param method
	 * @param extras
	 * @return
	 */
	Bundle RunTask(int method, Bundle extras);
}
