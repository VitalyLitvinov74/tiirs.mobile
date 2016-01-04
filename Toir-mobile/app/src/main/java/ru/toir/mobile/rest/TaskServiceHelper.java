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
	 * @param resultAction
	 */
	public TaskServiceHelper(Context context, String resultAction) {
		super(context, ProcessorService.Providers.TASK_PROVIDER, resultAction);
	}

	/**
	 * Получаем новые наряды
	 * 
	 */
	public void GetTaskNew() {

		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_GET_TASK_STATUS, "new");
		RunMethod(TaskServiceProvider.Methods.GET_TASK, bundle);
	}

	/**
	 * Получаем "архивные" наряды, те у которых статус не "Новый"
	 * 
	 */
	public void GetTaskDone() {

		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.PARAMETER_GET_TASK_STATUS, "done");
		RunMethod(TaskServiceProvider.Methods.GET_TASK, bundle);
	}

	/**
	 * Отправляет результат выполнения наряда
	 * 
	 * @param taskUuids
	 *            набор uuid нарядов для отправки их результатов
	 */
	public void SendTaskResult(String[] taskUuids) {

		Bundle bundle = new Bundle();
		bundle.putStringArray(TaskServiceProvider.Methods.PARAMETER_TASK_UUID,
				taskUuids);
		RunMethod(TaskServiceProvider.Methods.TASK_SEND_RESULT, bundle);
	}

}
