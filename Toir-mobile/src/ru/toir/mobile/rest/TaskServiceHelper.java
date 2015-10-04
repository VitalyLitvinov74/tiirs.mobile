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
	public void GetTask() {

		Bundle bundle = new Bundle();
		RunMethod(TaskServiceProvider.Methods.GET_TASK, bundle);
	}

	/**
	 * Отправляет результат выполнения наряда
	 * 
	 * @param token
	 * @param taskUuid
	 */
	public void SendTaskResult(String taskUuid) {

		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_TASK_UUID,
				taskUuid);
		RunMethod(TaskServiceProvider.Methods.TASK_SEND_RESULT, bundle);
	}

	/**
	 * Отправляет результаты выполнения нарядов
	 * 
	 * @param taskUuid
	 */
	public void SendTasksResult() {

		Bundle bundle = new Bundle();
		RunMethod(TaskServiceProvider.Methods.TASKS_SEND_RESULT, bundle);
	}

}
