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

	public static class Methods {
		// список методов
		public static final int GET_TASK = 1;
		public static final int TASK_CONFIRMATION = 2;
		public static final int TASK_SEND_RESULT = 3;
		public static final int TASKS_SEND_RESULT = 4;
		// список параметров к методам
		public static final String PARAMETER_TOKEN = "token";
		public static final String PARAMETER_TASK_UUID = "taskUuid";
	}
	
	public static class Actions {
		public final static String ACTION_GET_TASK = "action_get_task";
		public final static String ACTION_TASK_CONFIRM = "action_task_confirm";
		public final static String ACTION_TASK_SEND_RESULT = "action_task_send_result";
		public final static String ACTION_TASKS_SEND_RESULT = "action_tasks_send_result";
	}

	public TaskServiceProvider(Context context) {
		mContext = context;
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rest.IServiceProvider#RunTask(int, android.os.Bundle)
	 */
	@Override
	public boolean RunTask(int method, Bundle extras) {
		switch (method) {
		case Methods.GET_TASK:
			return getTask(extras);
		case Methods.TASK_CONFIRMATION:
			return taskConfirmation(extras);
		case Methods.TASK_SEND_RESULT:
			return taskSendResult(extras);
		case Methods.TASKS_SEND_RESULT:
			return tasksSendResult(extras);
		}
		return false;
	}
	
	private boolean tasksSendResult(Bundle extras) {
		try {
			return new TaskProcessor(mContext).TasksSendResult(extras);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean taskSendResult(Bundle extras) {
		try {
			return new TaskProcessor(mContext).TaskSendResult(extras);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean taskConfirmation(Bundle extras) {
		try {
			return new TaskProcessor(mContext).TaskConfirmation(extras);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean getTask(Bundle extras) {
		try {
			return new TaskProcessor(mContext).GetTask(extras);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
