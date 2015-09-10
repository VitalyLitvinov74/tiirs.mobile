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
		super(context, ProcessorService.Providers.REFERENCE_PROVIDER, resultAction);
	}
	
	/**
	 * 
	 * @param name Список имён справочников которые необходимо получить
	 */
	public void getReference(ArrayList<String> names) {
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(ReferenceServiceProvider.Methods.GET_REFERENCE_PARAMETER_NAME, names);
		RunMethod(ReferenceServiceProvider.Methods.GET_REFERENCE, bundle);
	}

	/**
	 * 
	 * @param name Список uuid шаблонов которые нужно получить
	 */
	public void getOperationPattern(ArrayList<String> uuids) {
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID, uuids);
		RunMethod(ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN, bundle);
	}

}
