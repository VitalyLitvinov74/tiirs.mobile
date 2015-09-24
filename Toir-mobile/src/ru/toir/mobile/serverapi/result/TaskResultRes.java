/**
 * 
 */
package ru.toir.mobile.serverapi.result;

import android.content.Context;

/**
 * @author Dmitriy Logachov
 *
 */
public class TaskResultRes {

	public TaskRes task;

	/**
	 * 
	 */
	public TaskResultRes() {
	}
	
	/**
	 * Выбирает из базы все данные связанные с результатами выполнения наряда.
	 * Наряд, список операций в наряде, список результатов выполнения операций, список результатов измерения.
	 * Списки результатов выполнения операций и результатов измерений могут быть пустыми.
	 * @param taskUuid
	 * @return true если есть наряд и операции связанные с этим нарядом
	 */
	public boolean load(Context context, String taskUuid) {

		task = TaskRes.load(context, taskUuid);
		if (task != null) {
			return true;
		} else {
			return false;
		}
	}

}
