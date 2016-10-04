/**
 * 
 */
package ru.toir.mobile.rest;

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;

/**
 * @author Dmitriy Logachov
 * 
 */
public class ReferenceServiceHelper extends ServiceHelperBase {

	/**
	 * 
	 */
	public ReferenceServiceHelper(Context context, String resultAction) {
		super(context, ProcessorService.Providers.REFERENCE_PROVIDER,
				resultAction);
	}

	/**
	 * Получаем/сохраняем варианты результатов выполнения операции
	 * 
	 * @param operationTypeUuids
	 *            Набор uuid типов операций
	 */
	public void getOperationResult(String[] operationTypeUuids) {

		Bundle bundle = new Bundle();
		bundle.putStringArray(
				ReferenceServiceProvider.Methods.GET_OPERATION_RESULT_PARAMETER_UUID,
				operationTypeUuids);
		RunMethod(ReferenceServiceProvider.Methods.GET_OPERATION_RESULT, bundle);
	}

	/**
	 * Получаем/сохраняем шаблоны выполнения операций
	 *
     * @param uuids
     *            Список uuid шаблонов которые нужно получить
	 */
	public void getOperationPattern(ArrayList<String> uuids) {

		Bundle bundle = new Bundle();
		bundle.putStringArrayList(
                ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID, uuids);
        RunMethod(ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN, bundle);
    }

	/**
	 * Получаем/сохраняем
	 */
	public void getDocumentType() {

		RunMethod(ReferenceServiceProvider.Methods.GET_DOCUMENTATION_TYPE);
	}

	/**
	 * Получаем/сохраняем
	 */
	public void getEquipmentStatus() {

		RunMethod(ReferenceServiceProvider.Methods.GET_EQUIPMENT_STATUS);
	}

	/**
	 * Получаем/сохраняем
	 */
	public void getEquipmentType() {

		RunMethod(ReferenceServiceProvider.Methods.GET_EQUIPMENT_TYPE);
	}

	/**
	 * Получаем/сохраняем
	 */
	public void getMeasureType() {

		RunMethod(ReferenceServiceProvider.Methods.GET_MEASURE_TYPE);
	}

	/**
	 * Получаем/сохраняем
	 */
	public void getOperationStatus() {

		RunMethod(ReferenceServiceProvider.Methods.GET_OPERATION_STATUS);
	}

	/**
	 * Получаем/сохраняем
	 */
	public void getOperationType() {

		RunMethod(ReferenceServiceProvider.Methods.GET_OPERATION_TYPE);
	}

	/**
	 * Получаем/сохраняем
	 */
	public void getTaskStatus() {

		RunMethod(ReferenceServiceProvider.Methods.GET_TASK_STATUS);
	}

	/**
	 * Получаем/сохраняем
	 * 
	 * @param equipmentUuids
	 *            Набор uuid оборудования
	 */
	public void getEquipment(String[] equipmentUuids) {

		Bundle bundle = new Bundle();
		bundle.putStringArray(
				ReferenceServiceProvider.Methods.GET_EQUIPMENT_PARAMETER_UUID,
				equipmentUuids);
		RunMethod(ReferenceServiceProvider.Methods.GET_EQUIPMENT, bundle);
	}

	/**
	 * Получаем/сохраняем
	 */
	public void getCriticalType() {

		RunMethod(ReferenceServiceProvider.Methods.GET_CRITICAL_TYPE);
	}

	/**
	 * Получаем/сохраняем
	 * 
	 * @param equipmentUuids
	 *            Набор uuid оборудования
	 */
	public void getDocumentation(String[] equipmentUuids) {

		Bundle bundle = new Bundle();
		bundle.putStringArray(
				ReferenceServiceProvider.Methods.GET_DOCUMENTATION_PARAMETER_UUID,
				equipmentUuids);
		RunMethod(ReferenceServiceProvider.Methods.GET_DOCUMENTATION, bundle);
	}

	/**
	 * Получаем/сохраняем
	 */
	public void getAll() {

		RunMethod(ReferenceServiceProvider.Methods.GET_ALL);
	}

	/**
	 * Получаем/сохраняем
	 * 
	 * @param documentationUuids
	 *            Набор uuid файлов документации
	 */
	public void getDocumentationFile(String[] documentationUuids) {

		Bundle bundle = new Bundle();
		bundle.putStringArray(
				ReferenceServiceProvider.Methods.GET_DOCUMENTATION_FILE_PARAMETER_UUID,
				documentationUuids);
		RunMethod(ReferenceServiceProvider.Methods.GET_DOCUMENTATION_FILE,
				bundle);
	}

}
