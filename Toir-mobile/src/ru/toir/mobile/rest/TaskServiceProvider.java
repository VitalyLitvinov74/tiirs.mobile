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
		// список параметров к методам
		public static final String GET_TASK_PARAMETER_USER_TAG = "tag_id";
		public static final String PARAMETER_TOKEN = "token";
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
		}
		return false;
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
