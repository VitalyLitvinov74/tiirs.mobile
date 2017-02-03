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
public class TaskServiceProvider implements IServiceProvider {

	private final Context mContext;

	public TaskServiceProvider(Context context) {
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
		case Methods.GET_TASK:
			return getTask(extras);
		case Methods.TASK_SEND_RESULT:
			return taskSendResult(extras);
		}

		Bundle result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, false);
		result.putString(MESSAGE, "Запуск не существующей задачи сервиса.");
		return result;
	}

	/**
	 * Отправка результата выполнения наряда на сервер
     *
     * @param extras
	 * @return
	 */
	private Bundle taskSendResult(Bundle extras) {
		try {
//			return new TaskProcessor(mContext).TaskSendResult(extras);
            return null;
        } catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * Получение новых нарядов с сервера
     *
     * @param extras
	 * @return
	 */
	private Bundle getTask(Bundle extras) {
		try {
			return new TaskProcessor(mContext).GetTask(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
    }

    public static class Methods {
        // список методов
        public static final int GET_TASK = 1;
        public static final int TASK_SEND_RESULT = 2;

        // список параметров к методам
        public static final String PARAMETER_TASK_UUID = "taskUuid";
        public static final String PARAMETER_GET_TASK_STATUS = "status";

        // список возвращаемых значений
        public static final String RESULT_GET_TASK_COUNT = "taskCount";
    }

    public static class Actions {
        public final static String ACTION_GET_TASK = "action_get_task";
        public final static String ACTION_TASK_SEND_RESULT = "action_task_send_result";
    }

}
