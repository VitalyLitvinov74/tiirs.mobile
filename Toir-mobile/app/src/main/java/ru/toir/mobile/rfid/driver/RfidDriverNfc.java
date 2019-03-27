package ru.toir.mobile.rfid.driver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.Locale;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.rfid.IRfidDriver;

/**
 * @author Dmitriy Logachev
 *         <p>
 *         Драйвер считывателя RFID для работы с метками NFC
 *         </p>
 *         Created by koputo on 11/8/17.
 */
@SuppressWarnings("unused")
public class RfidDriverNfc extends RfidDriverBase implements IRfidDriver {

    public static final String DRIVER_NAME = "Драйвер NFC";
    public static final String ACTION_NFC = "ru.toir.mobile.NFC_ACTION";
    private static final String TAG = "RfidDriverNfc";
    private int command;
    private NfcAdapter nfcAdapter;
    private Activity activity;
    private BroadcastReceiver receiver;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sHandler.obtainMessage(RfidDriverBase.RESULT_RFID_TIMEOUT).sendToTarget();
        }
    };

    private static NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    public boolean init() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(mContext);

        if (nfcAdapter == null) {
            Toast.makeText(mContext, "NFC not present.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(mContext, "NFC disabled. Turn it on before.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mActivity != null) {
            activity = mActivity;
        } else if (mFragment != null) {
            activity = mFragment.getActivity();
        }

        if (activity == null) {
            return false;
        }

        try {
            Class<?>[] arg = new Class[]{Intent.class};
            activity.getClass().getDeclaredMethod("onNewIntent", arg);
        } catch (Exception e) {
            Toast.makeText(activity, "Not implemented \"onNewIntent\" method in parent activity!",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 777, new Intent(activity,
                activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true)});
        nfcAdapter.setNdefPushMessage(ndefMessage, activity);

        IntentFilter filter = new IntentFilter(ACTION_NFC);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tagId = intent.getStringExtra("tagId");
                if (tagId == null) {
                    sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
                    handler.removeCallbacksAndMessages(null);
                    return;
                }

                tagId = "0000" + tagId;
                switch (command) {
                    case RfidDialog.READER_COMMAND_READ_ID:
                        sHandler.obtainMessage(RESULT_RFID_SUCCESS, tagId).sendToTarget();
                        break;
                    case RfidDialog.READER_COMMAND_READ_MULTI_ID:
                        sHandler.obtainMessage(RESULT_RFID_SUCCESS, new String[]{tagId}).sendToTarget();
                        break;
                    default:
                        sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
                }

                handler.removeCallbacksAndMessages(null);
            }
        };

        mContext.registerReceiver(receiver, filter);

        return true;
    }

    @Override
    public void readTagId() {
        command = RfidDialog.READER_COMMAND_READ_ID;
        handler.postDelayed(runnable, 5000);
    }

    @Override
    public void readMultiplyTagId(String[] tagIds) {
        command = RfidDialog.READER_COMMAND_READ_MULTI_ID;
        handler.postDelayed(runnable, 5000);
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

    @Override
    public void close() {
        if (nfcAdapter != null && activity != null) {
            nfcAdapter.disableForegroundDispatch(activity);
            nfcAdapter.setNdefPushMessage(null, activity);
        }

        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.nfc_read_layout, viewGroup);
    }
}
