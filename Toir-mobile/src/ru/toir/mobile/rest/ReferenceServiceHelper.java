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
	
}
