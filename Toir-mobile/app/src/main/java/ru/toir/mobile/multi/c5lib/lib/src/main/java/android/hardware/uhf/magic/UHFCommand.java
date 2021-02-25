package ru.toir.mobile.multi.c5lib.lib.src.main.java.android.hardware.uhf.magic;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 28.12.16.
 */

public class UHFCommand {
    public static final class Command {
        public static final byte ERROR = (byte) 0xFF;
        public static final byte INVENTORY = (byte) 0x22;
        public static final byte MULTI_INVENTORY = (byte) 0x27;
        public static final byte STOP_MULTI_INVENTORY = (byte) 0x28;
        public static final byte READ_TAG_DATA = (byte) 0x39;
        public static final byte WRITE_TAG_DATA = (byte) 0x49;
        public static final byte LOCK_TAG = (byte) 0x82;
        public static final byte KILL_TAG = (byte) 0x65;
    }

    public byte command;
    public Object data;

    /**
     *
     * @param command    byte
     */
    public UHFCommand(byte command) {
        this.command = command;
    }

    /**
     *
     * @param command    byte
     * @param data       Object
     */
    public UHFCommand(byte command, Object data) {
        this.command = command;
        this.data = data;
    }
}
