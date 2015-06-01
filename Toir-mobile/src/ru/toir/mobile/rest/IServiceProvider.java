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
	/**
	 * 
	 * @param method
	 * @param extras
	 * @return
	 */
	boolean RunTask(int method, Bundle extras);
}
