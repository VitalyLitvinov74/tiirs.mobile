/**
 * 
 */
package ru.toir.mobile.rest;

import android.content.Context;
import android.os.Bundle;

/**
 * @author koputo
 *
 */
public class EquipmentOperationResultServiceHelper extends ServiceHelperBase {

	/**
	 * @param context
	 * @param providerId
	 * @param resultAction
	 */
	public EquipmentOperationResultServiceHelper(Context context, String resultAction) {
		super(context, ProcessorService.Providers.EQUIPMENT_OPERATION_RESULT_PROVIDER, resultAction);
	}
	
	public void SendResult(String user_tag, String token) {
		Bundle bundle = new Bundle();
		bundle.putString(EquipmentOperationResultServiceProvider.Methods.PARAMETER_TOKEN, token);
		RunMethod(EquipmentOperationResultServiceProvider.Methods.SEND_RESULT, bundle);
	}

}
