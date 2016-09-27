/**
 * 
 */
package ru.toir.mobile.rfid;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceScreen;

/**
 * @author Dmitriy Logachov
 * 
 */
public abstract class RfidDriverBase implements IRfidDriver {

	public final static int MEMORY_BANK_RESERVED = 0;
	public final static int MEMORY_BANK_EPC = 1;
	public final static int MEMORY_BANK_TID = 2;
	public final static int MEMORY_BANK_USER = 3;
	public static final int RESULT_RFID_SUCCESS = 0;
	public static final int RESULT_RFID_READ_ERROR = 1;
	public static final int RESULT_RFID_INIT_ERROR = 2;
	public static final int RESULT_RFID_CLASS_NOT_FOUND = 3;
	public static final int RESULT_RFID_WRITE_ERROR = 4;
	public static final int RESULT_RFID_CANCEL = 5;
	public static final int RESULT_RFID_TIMEOUT = 6;
	public static final int RESULT_RFID_DISCONNECT = 7;
	public static final String RESULT_RFID_TAG_ID = "tagId";
    protected static final String TAG = "RfidDriverBase";
    // Handler который будет обрабатывать сообщение от драйвера
	protected static Handler sHandler;
	protected Context mContext;
	protected Fragment mFragment;
	protected Activity mActivity;

    public static String getDriverName(String classPath) {
        String driverName;

        if (classPath == null || classPath.equals("")) {
            return null;
        }

        try {
            Class<?> driverClass;
            driverClass = Class.forName(classPath);
            driverName = (String) (driverClass.getDeclaredField("DRIVER_NAME").get(""));
        } catch (Exception e) {
            e.printStackTrace();
            driverName = null;
        }

        return driverName;
    }

	public void setHandler(Handler handler) {
		sHandler = handler;
	}

	public void setContext(Context context) {
		mContext = context;
	}

	@Override
	public void setIntegration(Activity activity) {
		mActivity = activity;
	}

	@Override
	public void setIntegration(Fragment fragment) {
		mFragment = fragment;
	}

	@Override
	public PreferenceScreen getSettingsScreen(PreferenceScreen screen) {
		return null;
	}
}
