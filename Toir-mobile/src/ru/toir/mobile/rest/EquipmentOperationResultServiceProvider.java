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
public class EquipmentOperationResultServiceProvider implements
		IServiceProvider {
	private final Context mContext;
	
	public static class Methods {
		public static final int SEND_RESULT = 1;
		public static final String SEND_RESULT_PARAMETER_USER_TAG = "tag_id";
		public static final String SEND_RESULT_PARAMETER_TOKEN = "token";
	}

	/**
	 * 
	 */
	public EquipmentOperationResultServiceProvider(Context context) {
		mContext = context;
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rest.IServiceProvider#RunTask(int, android.os.Bundle)
	 */
	@Override
	public boolean RunTask(int method, Bundle extras) {
		switch (method) {
		case Methods.SEND_RESULT:
			return SendResult(extras);
		}
		return false;
	}
	
	private boolean SendResult(Bundle extras) {
		try {
			return new EquipmentOperationResultProcessor(mContext).SendResult(extras);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
