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
public class UsersServiceProvider implements IServiceProvider {

	private final Context mContext;

	public static class Methods {
		public static final int GET_USER = 1;
	}

	public static class Actions {
		public static final String ACTION_GET_USER = "action_get_user";
	}

	public UsersServiceProvider(Context context) {
		mContext = context;
	}

	@Override
	public Bundle RunTask(int method, Bundle extras) {

		switch (method) {
		case Methods.GET_USER:
			return getUser(extras);
		}

		Bundle result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, false);
		result.putString(MESSAGE, "Запуск не существующей задачи сервиса.");
		return result;
	}

	/**
	 * Получаем информацию о пользователе
	 * 
	 * @param extras
	 * @return
	 */
	private Bundle getUser(Bundle extras) {

		try {
			return new UsersProcessor(mContext).getUser(extras);
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

}
