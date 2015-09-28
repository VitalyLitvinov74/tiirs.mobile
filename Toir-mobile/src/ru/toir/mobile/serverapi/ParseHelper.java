/**
 * 
 */
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.DocumentationType;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentStatus;
import ru.toir.mobile.db.tables.EquipmentType;
import ru.toir.mobile.db.tables.OperationStatus;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.TaskStatus;

/**
 * @author Dmitriy Logachov
 * 
 */
public class ParseHelper {

	/**
	 * 
	 */
	public ParseHelper() {
	}

	public static ArrayList<EquipmentType> getEquipmentTypes(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<EquipmentType> list = new ArrayList<EquipmentType>();
		for (EquipmentSrv equipment : equipments) {
			list.add(equipment.getEquipmentType().getLocal());
		}
		return list;
	}

	public static ArrayList<CriticalType> getCriticalTypes(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<CriticalType> list = new ArrayList<CriticalType>();
		for (EquipmentSrv equipment : equipments) {
			list.add(equipment.getCriticalityType().getLocal());
		}
		return list;
	}

	public static ArrayList<EquipmentStatus> getEquipmentStatuses(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<EquipmentStatus> list = new ArrayList<EquipmentStatus>();
		for (EquipmentSrv equipment : equipments) {
			list.add(equipment.getEquipmentStatus().getLocal());
		}
		return list;
	}

	public static ArrayList<DocumentationType> getEquipmentDocumentationTypes(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<DocumentationType> list = new ArrayList<DocumentationType>();
		for (EquipmentSrv equipment : equipments) {
			List<EquipmentDocumentationSrv> documentations = equipment
					.getDocuments();
			for (EquipmentDocumentationSrv documentation : documentations) {
				list.add(documentation.getDocumentType().getLocal());
			}
		}
		return list;
	}

	public static ArrayList<EquipmentDocumentation> getEquipmentDocumentations(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<EquipmentDocumentation> list = new ArrayList<EquipmentDocumentation>();
		for (EquipmentSrv equipment : equipments) {
			List<EquipmentDocumentationSrv> documentations = equipment
					.getDocuments();
			for (EquipmentDocumentationSrv documentation : documentations) {
				list.add(documentation.getLocal(equipment.getId()));
			}
		}
		return list;
	}

	public static ArrayList<Task> getTasks(TaskSrv[] tasks) {

		ArrayList<Task> list = new ArrayList<Task>();
		for (TaskSrv task : tasks) {
			list.add(task.getLocal());
		}
		return list;
	}

	public static ArrayList<EquipmentOperation> getEquipmentOperations(
			TaskSrv[] tasks) {

		ArrayList<EquipmentOperation> list = new ArrayList<EquipmentOperation>();
		for (TaskSrv task : tasks) {
			List<EquipmentOperationSrv> operations = task.getItems();
			for (EquipmentOperationSrv operation : operations) {
				list.add(operation.getLocal(task.getId()));
			}
		}
		return list;
	}

	public static ArrayList<EquipmentOperationSrv> getEquipmentOperationSrvs(
			TaskSrv[] tasks) {

		ArrayList<EquipmentOperationSrv> list = new ArrayList<EquipmentOperationSrv>();
		for (TaskSrv task : tasks) {
			list.addAll(task.getItems());
		}
		return list;
	}

	public static ArrayList<TaskStatus> getTaskStatuses(TaskSrv[] tasks) {

		ArrayList<TaskStatus> list = new ArrayList<TaskStatus>();
		for (TaskSrv task : tasks) {
			list.add(task.getOrderStatus().getLocal());
		}
		return list;
	}

	public static ArrayList<Equipment> getEquipments(
			ArrayList<EquipmentOperationSrv> operations) {

		ArrayList<Equipment> list = new ArrayList<Equipment>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getEquipment().getLocal());
		}
		return list;
	}

	public static ArrayList<EquipmentSrv> getEquipmentSrvs(
			ArrayList<EquipmentOperationSrv> operations) {

		ArrayList<EquipmentSrv> list = new ArrayList<EquipmentSrv>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getEquipment());
		}
		return list;
	}

	public static ArrayList<OperationType> getOperationTypes(
			ArrayList<EquipmentOperationSrv> operations) {

		ArrayList<OperationType> list = new ArrayList<OperationType>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getOperationType().getLocal());
		}
		return list;
	}

	public static Set<String> getOperationPatternUuids(
			ArrayList<EquipmentOperationSrv> operations) {

		Set<String> list = new HashSet<String>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getOperationPatternId());
		}
		return list;
	}

	public static ArrayList<OperationStatus> getOperationStatuses(
			ArrayList<EquipmentOperationSrv> operations) {

		ArrayList<OperationStatus> list = new ArrayList<OperationStatus>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getStatus().getLocal());
		}
		return list;
	}
}
