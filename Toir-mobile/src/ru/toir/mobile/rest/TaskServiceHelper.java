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

		RunMethod(TaskServiceProvider.Methods.GET_TASK);
	}

	/**
	 * Отправляет результат выполнения наряда
	 * 
	 * @param token
	 * @param taskUuids набор uuid нарядов для отправки их результатов
	 */
	public void SendTaskResult(String[] taskUuids) {

		Bundle bundle = new Bundle();
		bundle.putStringArray(TaskServiceProvider.Methods.PARAMETER_TASK_UUID,
				taskUuids);
		RunMethod(TaskServiceProvider.Methods.TASK_SEND_RESULT, bundle);
	}

}
