package ru.toir.mobile.multi.rfid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.rfid.driver.RfidDriverDummy;
import ru.toir.mobile.multi.rfid.driver.RfidDriverNfc;
import ru.toir.mobile.multi.rfid.driver.RfidDriverPin;
import ru.toir.mobile.multi.rfid.driver.RfidDriverQRcode;

public class Tag {
    private String type;
    private String tagId;

    public Tag() {
        type = Type.TAG_TYPE_DUMMY;
        tagId = "";
    }

    public static boolean checkType(String type) {
        String[] types = new String[]{
                Type.TAG_TYPE_PIN,
                Type.TAG_TYPE_GRAPHIC_CODE,
                Type.TAG_TYPE_NFC,
                Type.TAG_TYPE_UHF,
                Type.TAG_TYPE_DUMMY
        };

        return Arrays.asList(types).contains(type);
    }

    public boolean loadData(String tagIdStr) {
        Pattern p = Pattern.compile("([a-z]*):([a-z0-9-]*)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(tagIdStr);
        boolean b = m.matches();
        MatchResult r = m.toMatchResult();
        if (b && m.groupCount() == 2) {
            if (!checkType(m.group(1))) {
                return false;
            }

            type = m.group(1);
            tagId = m.group(2);
            return true;
        } else {
            return false;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    @Override
    public String toString() {
        return type + ':' + tagId;
    }

    public String getTagDriver(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        HashMap<String, String> type2drv = new HashMap<>();
        type2drv.put(Type.TAG_TYPE_PIN, RfidDriverPin.class.getCanonicalName());
        type2drv.put(Type.TAG_TYPE_GRAPHIC_CODE, RfidDriverQRcode.class.getCanonicalName());
        type2drv.put(Type.TAG_TYPE_NFC, RfidDriverNfc.class.getCanonicalName());
        type2drv.put(Type.TAG_TYPE_UHF,
                preferences.getString(context.getString(R.string.default_uhf_driver_key), null));
        type2drv.put(Type.TAG_TYPE_DUMMY, RfidDriverDummy.class.getCanonicalName());

        return type2drv.get(type);
    }

    public static class Type {
        public static String TAG_TYPE_PIN = "PIN";
        public static String TAG_TYPE_GRAPHIC_CODE = "GCODE";
        public static String TAG_TYPE_NFC = "NFC";
        public static String TAG_TYPE_UHF = "UHF";
        public static String TAG_TYPE_DUMMY = "DUMMY";
    }
}