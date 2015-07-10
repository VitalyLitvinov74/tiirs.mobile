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
	 * @param token
	 */
	public void GetTask(String token) {
		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_TOKEN, token);
		RunMethod(TaskServiceProvider.Methods.GET_TASK, bundle);
	}
	
	/**
	 * 
	 * @param token
	 */
	public void TaskConfirmation(String token) {
		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_TOKEN, token);
		RunMethod(TaskServiceProvider.Methods.TASK_CONFIRMATION, bundle);
	}
	
	/**
	 * Отправляет результаты выполнения наряда
	 * @param token
	 * @param taskUuid
	 */
	public void SendTaskResult(String token, String taskUuid) {
		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_TOKEN, token);
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_TASK_UUID, taskUuid);
		RunMethod(TaskServiceProvider.Methods.TASK_SEND_RESULT, bundle);
	}
	
	public void SendTasksResult(String token) {
		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_TOKEN, token);
		RunMethod(TaskServiceProvider.Methods.TASKS_SEND_RESULT, bundle);
	}
	

}
