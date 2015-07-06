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
public class TaskServiceHelper extends ServiceHelperBase {

	/**
	 * @param context
	 * @param providerId
	 * @param resultAction
	 */

	public TaskServiceHelper(Context context, String resultAction) {
		super(context, ProcessorService.Providers.TASK_PROVIDER, resultAction);
	}
	
	/**
	 * 
	 * @param tag
	 */
	public void GetTask(String user_tag, String token) {
		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.GET_TASK_PARAMETER_USER_TAG, user_tag);
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_TOKEN, token);
		RunMethod(TaskServiceProvider.Methods.GET_TASK, bundle);
	}
	
	public void TaskConfirmation(String user_tag, String token) {
		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.GET_TASK_PARAMETER_USER_TAG, user_tag);
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_TOKEN, token);
		RunMethod(TaskServiceProvider.Methods.TASK_CONFIRMATION, bundle);
	}

}
