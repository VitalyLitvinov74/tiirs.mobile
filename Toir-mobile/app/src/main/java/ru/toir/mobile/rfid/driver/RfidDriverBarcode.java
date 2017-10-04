package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;

import com.google.zxing.integration.android.IntentIntegrator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 */
public class RfidDriverBarcode extends RfidDriverBase implements IRfidDriver {

    public static final String DRIVER_NAME = "Драйвер штрихкодов Barcode";
    private IntentIntegrator mIntegrator;

    @Override
    public boolean init() {
        return !(mActivity == null && mFragment == null);
    }

    @Override
    public void readTagId() {
        mIntegrator.initiateScan();
    }

    @Override
    public void readMultiplyTagId(final String[] tagIds) {
        mIntegrator.initiateScan();
    }

    @Override
    public void close() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        if (mActivity != null) {
            mIntegrator = new IntentIntegrator(mActivity);
        } else if (mFragment != null) {
            mIntegrator = new IntentIntegrator(mFragment);
        }

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
