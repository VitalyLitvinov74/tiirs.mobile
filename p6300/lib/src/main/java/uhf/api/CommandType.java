/*
 * Decompiled with CFR 0_118.
 */
package uhf.api;

public class CommandType {
    public static final char SET_POWER = '\u0000';
    public static final char SET_GPIO_LEVEL = '\u0001';
    public static final char SET_OUTPUT_FREQUENCY = '\u0002';
    public static final char SET_RF_LINK = '\u0003';
    public static final char SET_REGISTER = '\u0004';
    public static final char SET_ANTENNA_CARRIER = '\u0005';
    public static final char GET_REGISTER_DATA = '\u0006';
    public static final char SET_GEN2_PARAM = '\u0007';
    public static final char SET_WORK_ANTANNE = '\b';
    public static final char SET_FREQUENCY_REGION = '\t';
    public static final char GET_HARDWARE_VERSION = '\n';
    public static final char GET_FIRMWARE_VERSION = '\u000b';
    public static final char GET_POWER = '\f';
    public static final char GET_FREQUENCY_STATE = '\r';
    public static final char GET_RF_LINK = '\u000e';
    public static final char GET_ANTENNA_CARRIER = '\u000f';
    public static final char GET_WORK_ANTANNE = '\u0010';
    public static final char GET_FREQUENCY_REGION = '\u0011';
    public static final char GET_MODULE_TEMPERATURE = '\u0012';
    public static final char GET_GPIO_LEVEL = '\u0013';
    public static final char GET_GEN2_PARAM = '\u0014';
    public static final char SET_FIRMWARE_UPGRADE_ONLINE = '\u0015';
    public static final char SINGLE_QUERY_TAGS_EPC = '\u0016';
    public static final char MULTI_QUERY_TAGS_EPC = '\u0017';
    public static final char STOP_MULTI_QUERY_TAGS_EPC = '\u0018';
    public static final char READ_TAGS_DATA = '\u0019';
    public static final char WRITE_TAGS_DATA = '\u001a';
    public static final char LOCK_TAGS = '\u001b';
    public static final char KILL_TAGS = '\u001c';
    public static final char SET_MULTI_QUERY_TAGS_INTERVAL = '\u001d';
    public static final char GET_MULTI_QUERY_TAGS_INTERVAL = '\u001e';
    public static final char SET_ANTENNA_WORKTIME_AND_WAITTIME = '\u001f';
    public static final char GET_ANTENNA_WORKTIME_AND_WAITTIME = ' ';
    public static final char SET_FASTID = '!';
    public static final char GET_FASTID = '\"';
    public static final char SET_MODULE_BAUD_RATE = '#';
    public static final char WRITE_TAGS_EPC = '$';
    public static final char GET_MULTI_QUERY_TAGS_EPC = '%';
    public static final char COMMAND_MAX = '&';
    public static final char COMMAND_ERROR_RESPOND = '\u00ff';
    public static final char CMD_NULL = '\u00fe';
    public static char LastCommand = 254;
    public static Boolean CommandOK = false;
    public static long TimeOut = 600;
}
