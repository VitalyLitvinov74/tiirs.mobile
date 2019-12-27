/*
 * Decompiled with CFR 0_118.
 */
package android.hardware.p6300.uhf.api;

public class CommandType {
    public static final char SET_POWER = 0x00;
    public static final char SET_GPIO_LEVEL = 0x01;
    public static final char SET_OUTPUT_FREQUENCY = 0x02;
    public static final char SET_RF_LINK = 0x03;
    public static final char SET_REGISTER = 0x04;
    public static final char SET_ANTENNA_CARRIER = 0x05;
    public static final char GET_REGISTER_DATA = 0x06;
    public static final char SET_GEN2_PARAM = 0x07;
    public static final char SET_WORK_ANTANNE = 0x08;
    public static final char SET_FREQUENCY_REGION = 0x09;
    public static final char GET_HARDWARE_VERSION = 0x0A;
    public static final char GET_FIRMWARE_VERSION = 0x0B;
    public static final char GET_POWER = 0x0C;
    public static final char GET_FREQUENCY_STATE = 0x0D;
    public static final char GET_RF_LINK = 0x0E;
    public static final char GET_ANTENNA_CARRIER = 0x0F;
    public static final char GET_WORK_ANTANNE = 0x10;
    public static final char GET_FREQUENCY_REGION = 0x11;
    public static final char GET_MODULE_TEMPERATURE = 0x12;
    public static final char GET_GPIO_LEVEL = 0x13;
    public static final char GET_GEN2_PARAM = 0x14;
    public static final char SET_FIRMWARE_UPGRADE_ONLINE = 0x15;
    public static final char SINGLE_QUERY_TAGS_EPC = 0x16;
    public static final char MULTI_QUERY_TAGS_EPC = 0x17;
    public static final char STOP_MULTI_QUERY_TAGS_EPC = 0x18;
    public static final char READ_TAGS_DATA = 0x19;
    public static final char WRITE_TAGS_DATA = 0x1A;
    public static final char LOCK_TAGS = 0x1B;
    public static final char KILL_TAGS = 0x1C;
    public static final char SET_MULTI_QUERY_TAGS_INTERVAL = 0x1D;
    public static final char GET_MULTI_QUERY_TAGS_INTERVAL = 0x1E;
    public static final char SET_ANTENNA_WORKTIME_AND_WAITTIME = 0x1F;
    public static final char GET_ANTENNA_WORKTIME_AND_WAITTIME = 0x20;
    public static final char SET_FASTID = 0x21;
    public static final char GET_FASTID = 0x22;
    public static final char SET_MODULE_BAUD_RATE = 0x23;
    public static final char WRITE_TAGS_EPC = 0x24;
    public static final char GET_MULTI_QUERY_TAGS_EPC = 0x25;
    public static final char COMMAND_MAX = 0x27;
    public static final char COMMAND_ERROR_RESPOND = 0xFF;
    public static final char CMD_NULL = 0xFE;
    public static char LastCommand = 0xFE;
    public static Boolean CommandOK = false;
    public static Boolean CommandResend = false;
    public static long TimeOut = 5000;
}
