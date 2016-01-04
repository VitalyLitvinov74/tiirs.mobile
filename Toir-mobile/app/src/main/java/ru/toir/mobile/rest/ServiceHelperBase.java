/**
 * 
 */
package ru.toir.mobile.rest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Dmitriy Logachov
 *
 */
public class ServiceHelperBase {
	private final Context mContext;
	private final int mProviderId;
	private final String mResultAction;


	/**
	 * 
	 */
	public ServiceHelperBase(Context context, int providerId, String resultAction) {
		mContext = context;
		mProviderId = providerId;
		mResultAction = resultAction;
	}

	protected void RunMethod(int methodId) {
		RunMethod(methodId, null);
	}

	protected void RunMethod(int methodId, Bundle bundle) {
		Intent service = new Intent(mContext, ProcessorService.class);
		service.putExtra(ProcessorService.Extras.PROVIDER_EXTRA, mProviderId);
		service.putExtra(ProcessorService.Extras.METHOD_EXTRA, methodId);
		service.putExtra(ProcessorService.Extras.RESULT_ACTION_EXTRA, mResultAction);
		
		if (bundle != null) {
			service.putExtras(bundle);
		}
		
		mContext.startService(service);
	}

}
