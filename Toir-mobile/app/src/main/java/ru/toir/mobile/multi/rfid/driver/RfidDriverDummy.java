package ru.toir.mobile.multi.rfid.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.toir.mobile.multi.rfid.RfidDriverBase;
import ru.toir.mobile.multi.rfid.RfidDriverMsg;
import ru.toir.mobile.multi.rfid.Tag;

public class RfidDriverDummy extends RfidDriverBase {
    public static final String DRIVER_NAME;

    static {
        DRIVER_NAME = "Dummy";
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void readTagId() {
        // всегда "считываем" ид метки
        RfidDriverMsg msg = RfidDriverMsg.tagMsg("0000" + Tag.Type.TAG_TYPE_DUMMY + ":");
        sHandler.obtainMessage(RESULT_RFID_SUCCESS, msg).sendToTarget();
    }

    @Override
    public void readMultiplyTagId(final String[] tagIds) {
        for (int i = 0; i < tagIds.length; i++) {
            tagIds[i] = Tag.getTagId(tagIds[i]);
        }

        // всегда "считываем" ид меток
        RfidDriverMsg[] returnTags = new RfidDriverMsg[tagIds.length];
        for (int i = 0; i < tagIds.length; i++) {
            returnTags[i] = RfidDriverMsg.tagMsg("0000" + Tag.Type.TAG_TYPE_DUMMY + ":" + tagIds[i]);
        }

        sHandler.obtainMessage(RESULT_RFID_SUCCESS, returnTags).sendToTarget();
    }

    @Override
    public void close() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void readTagData(String password, int memoryBank, int address, int count) {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void readTagData(String password, String tagId, int memoryBank, int address, int count) {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, int memoryBank, int address, String data) {
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, String tagId, int memoryBank, int address, String data) {
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
    }
}
