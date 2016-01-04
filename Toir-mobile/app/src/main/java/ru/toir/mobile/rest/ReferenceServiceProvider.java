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
public class ReferenceServiceProvider implements IServiceProvider {

	private final Context mContext;

	public static class Methods {
		public static final int GET_OPERATION_RESULT = 1;
		public static final int GET_OPERATION_PATTERN = 2;
		public static final int GET_DOCUMENTATION_TYPE = 3;
		public static final int GET_EQUIPMENT_STATUS = 4;
		public static final int GET_EQUIPMENT_TYPE = 5;
		public static final int GET_MEASURE_TYPE = 6;
		public static final int GET_OPERATION_STATUS = 7;
		public static final int GET_OPERATION_TYPE = 8;
		public static final int GET_TASK_STATUS = 9;
		public static final int GET_EQUIPMENT = 10;
		public static final int GET_CRITICAL_TYPE = 11;
		public static final int GET_DOCUMENTATION = 12;
		public static final int GET_ALL = 13;
		public static final int GET_DOCUMENTATION_FILE = 14;

		public static final String GET_OPERATION_PATTERN_PARAMETER_UUID = "operationUuid";
		public static final String GET_DOCUMENTATION_PARAMETER_UUID = "documentationUuid";
		public static final String GET_OPERATION_RESULT_PARAMETER_UUID = "operationResultUuid";
		public static final String GET_EQUIPMENT_PARAMETER_UUID = "equipmentUuid";
		public static final String GET_DOCUMENTATION_FILE_PARAMETER_UUID = "documentationFileUuid";
		public static final String GET_IMAGE_FILE_PARAMETER_UUID = "imageFileUuid";
		
		public static final String RESULT_GET_DOCUMENTATION_FILE_UUID = "loadedUuid";
	}

	public static class Actions {
		public static final String ACTION_GET_ALL = "action_get_all";
		public static final String ACTION_GET_OPERATION_RESULT = "action_get_operation_result";
		public static final String ACTION_GET_OPERATION_PATTERN = "action_get_operation_pattern";
		public static final String ACTION_GET_DOCUMENT_TYPE = "action_get_document_type";
		public static final String ACTION_GET_EQUIPMENT_STATUS = "action_get_equipment_status";
		public static final String ACTION_GET_EQUIPMENT_TYPE = "action_get_equipment_type";
		public static final String ACTION_GET_MEASURE_TYPE = "action_get_measure_type";
		public static final String ACTION_GET_OPERATION_STATUS = "action_get_operation_status";
		public static final String ACTION_GET_OPERATION_TYPE = "action_get_operation_type";
		public static final String ACTION_GET_TASK_STATUS = "action_get_task_status";
		public static final String ACTION_GET_EQUIPMENT = "action_get_equipment";
		public static final String ACTION_GET_CRITICAL_TYPE = "action_get_critical_type";
		public static final String ACTION_GET_DOCUMENTATION = "action_get_documentation";
		public static final String ACTION_GET_DOCUMENTATION_FILE = "action_get_documentation_file";
	}

	/**
	 * 
	 */
	public ReferenceServiceProvider(Context context) {
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
		case Methods.GET_OPERATION_RESULT:
			return getOperationResult(extras);
		case Methods.GET_OPERATION_PATTERN:
			return getOperationPattern(extras);
		case Methods.GET_DOCUMENTATION_TYPE:
			return getDocumentType(extras);
		case Methods.GET_EQUIPMENT_STATUS:
			return getEquipmentStatus(extras);
		case Methods.GET_EQUIPMENT_TYPE:
			return getEquipmentType(extras);
		case Methods.GET_MEASURE_TYPE:
			return getMeasureType(extras);
		case Methods.GET_OPERATION_STATUS:
			return getOperationStatus(extras);
		case Methods.GET_OPERATION_TYPE:
			return getOperationType(extras);
		case Methods.GET_TASK_STATUS:
			return getTaskStatus(extras);
		case Methods.GET_EQUIPMENT:
			return getEquipment(extras);
		case Methods.GET_CRITICAL_TYPE:
			return getCriticalType(extras);
		case Methods.GET_DOCUMENTATION:
			return getDocumentation(extras);
		case Methods.GET_ALL:
			return getAll(extras);
		case Methods.GET_DOCUMENTATION_FILE:
			return getDocumentationFile(extras);
		}

		Bundle result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, false);
		result.putString(MESSAGE, "Запуск не существующей задачи сервиса.");
		return result;

	}

	/**
	 * 
	 */
	private Bundle getOperationResult(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getOperationResult(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getOperationPattern(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getOperationPattern(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getDocumentType(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getDocumentType(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getEquipmentStatus(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getEquipmentStatus(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getEquipmentType(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getEquipmentType(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getMeasureType(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getMeasureType(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getOperationStatus(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getOperationStatus(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getOperationType(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getOperationType(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getTaskStatus(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getTaskStatus(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getEquipment(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getEquipment(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getCriticalType(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getCriticalType(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getDocumentation(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getDocumentation(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getAll(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getAll(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 */
	private Bundle getDocumentationFile(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getDocumentationFile(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

}
