package ru.toir.mobile.multi.rfid.driver;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.camera.CameraPreview;
import ru.toir.mobile.multi.rfid.IRfidDriver;
import ru.toir.mobile.multi.rfid.RfidDialog;
import ru.toir.mobile.multi.rfid.RfidDriverBase;

/**
 * @author Dmitriy Logachev
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из QR кода.
 *         </p>
 */
public class RfidDriverQRcode extends RfidDriverBase implements IRfidDriver {

    @SuppressWarnings("unused")
    public static final String DRIVER_NAME = "Драйвер QR кодов";
    private static final String TAG = "RFIDQRcode";

    static {
        System.loadLibrary("iconv");
    }

    private Camera mCamera;
    private Context mContext;
    private TextView scanText;
    private ImageScanner scanner;
    private FrameLayout preview;
    private Image codeImage;
    private int command;
    private PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            String lastScannedCode;

            codeImage.setData(data);
            int result = scanner.scanImage(codeImage);
            if (result != 0) {
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    lastScannedCode = sym.getData();
                    if (lastScannedCode != null) {
                        Log.d(TAG, "прочитано: " + lastScannedCode);
                        scanText.setText(lastScannedCode);
                        String tagId = "0000" + lastScannedCode;
                        switch (command) {
                            case RfidDialog.READER_COMMAND_READ_ID:
                                sHandler.obtainMessage(RESULT_RFID_SUCCESS, tagId).sendToTarget();
                                break;
                            case RfidDialog.READER_COMMAND_READ_MULTI_ID:
                                sHandler.obtainMessage(RESULT_RFID_SUCCESS, new String[]{tagId}).sendToTarget();
                                break;
                            default:
                                sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
                                break;
                        }
                        releaseCamera();
                    }
                }
            }
            camera.addCallbackBuffer(data);
        }
    };

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            //
        }

        return c;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void readTagId() {
        command = RfidDialog.READER_COMMAND_READ_ID;
        releaseCamera();
        resumeCamera();
    }

    @Override
    public void readMultiplyTagId(final String[] tagIds) {
        command = RfidDialog.READER_COMMAND_READ_MULTI_ID;
        releaseCamera();
        resumeCamera();
    }

    @Override
    public void close() {
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.cancelAutoFocus();
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (RuntimeException e) {
                Log.d(TAG, "autofocus error");
            }
        }
    }

    private void resumeCamera() {
        CameraPreview cameraSurface;

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        mCamera = getCameraInstance();
        cameraSurface = new CameraPreview(mContext, mCamera, previewCb, null);

        preview.removeAllViews();
        preview.addView(cameraSurface);
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            Size size = parameters.getPreviewSize();
            codeImage = new Image(size.width, size.height, "Y800");
            cameraSurface.refreshDrawableState();
        }
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        mContext = inflater.getContext();
        View view = inflater.inflate(R.layout.qr_read, viewGroup);

        Button button = view.findViewById(R.id.cancelButton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
            }
        });

        preview = view.findViewById(R.id.cameraPreview);
        scanText = view.findViewById(R.id.code_from_bar);
        return view;
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
