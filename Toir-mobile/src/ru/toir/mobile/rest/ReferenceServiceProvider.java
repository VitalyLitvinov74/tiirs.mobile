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
		public static final int GET_REFERENCE = 1;
		public static final String GET_REFERENCE_PARAMETER_NAME = "name";
		
		public static final int GET_OPERATION_PATTERN = 2;
		public static final String GET_OPERATION_PATTERN_PARAMETER_UUID = "uuid";
	}
	
	public static class Actions {
		public static final String ACTION_GET_REFERENCE = "action_get_reference";
		public static final String ACTION_GET_OPERATION_PATTERN = "action_get_operation_pattern";
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
	public boolean RunTask(int method, Bundle extras) {
		switch (method) {
		case Methods.GET_REFERENCE:
			return getReference(extras);
		case Methods.GET_OPERATION_PATTERN:
			return getOperationPattern(extras);
		}
		return false;
	}

	/**
	 * 
	 */
	private boolean getReference(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getReference(extras);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 */
	private boolean getOperationPattern(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getOperationPattern(extras);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
