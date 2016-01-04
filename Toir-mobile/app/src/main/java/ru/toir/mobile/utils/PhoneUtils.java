package ru.toir.mobile.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneUtils {

public static String getIMEI(Context context){
    TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
    String imei = mngr.getDeviceId();
    return imei;
 }

}
