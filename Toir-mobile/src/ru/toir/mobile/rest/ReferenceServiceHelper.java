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
public class ReferenceServiceHelper extends ServiceHelperBase {

	/**
	 * 
	 */
	public ReferenceServiceHelper(Context context, String resultAction) {
		super(context, ProcessorService.Providers.REFERENCE_PROVIDER, resultAction);
	}
	
	/**
	 * 
	 * @param tag
	 */
	public void getReference(String uuid) {
		Bundle bundle = new Bundle();
		bundle.putString(ReferenceServiceProvider.Methods.GET_REFERENCE_PARAMETER_NAME, uuid);
		RunMethod(ReferenceServiceProvider.Methods.GET_REFERENCE, bundle);
	}
	
}
