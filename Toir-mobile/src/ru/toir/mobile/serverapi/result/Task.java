/**
 * 
 */
package ru.toir.mobile.serverapi.result;

import java.util.ArrayList;
import android.content.Context;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.TaskDBAdapter;

/**
 * @author Dmitriy Logachov
 *
 */
public class Task extends ru.toir.mobile.db.tables.Task {

	public ArrayList<EquipmentOperation> equipmentOperations;

	/**
	 * 
	 */
	public Task() {
	}
	
	public static Task load(Context context, String uuid) {

		TaskDBAdapter taskDBAdapter = new TaskDBAdapter(new TOiRDatabaseContext(context));
		ru.toir.mobile.db.tables.Task task = taskDBAdapter.getItem(uuid);
		if (task != null) {
			Task item = new Task();
			item._id = task.get_id();
			item.uuid = task.getUuid();
			item.setUsers_uuid(task.getUsers_uuid());
			item.setClose_date(task.getClose_date());
			item.setTask_status_uuid(task.getTask_status_uuid());
			item.setAttempt_send_date(task.getAttempt_send_date());
			item.setAttempt_count(task.getAttempt_count());
			item.setUpdated(task.isUpdated());
			item.setTask_name(task.getTask_name());
			item.CreatedAt = task.getCreatedAt();
			item.ChangedAt = task.getChangedAt();
			item.equipmentOperations = EquipmentOperation.load(context, uuid);
			return item;
		} else {
			return null;
		}
		
	}
	
}
