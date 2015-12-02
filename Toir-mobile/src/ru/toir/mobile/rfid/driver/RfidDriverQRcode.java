package ru.toir.mobile.rfid.driver;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import ru.toir.mobile.R;
import ru.toir.mobile.camera.CameraPreview;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author koputo
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
public class RfidDriverQRcode extends RfidDriverBase implements IRfidDriver {

	public static final String DRIVER_NAME = "Драйвер QR кодов";
	private String TAG = "RFIDQRcode";
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private TextView scanText;
	private ImageScanner scanner;
	private FrameLayout preview;
	private Image codeImage;
	private String lastScannedCode;
	private Context mContext;

	static {
		System.loadLibrary("iconv");
	};

	public RfidDriverQRcode(Handler handler) {
		super(handler);
	}

	public RfidDriverQRcode(Handler handler, Activity activity) {
		super(handler);
	}

	public RfidDriverQRcode(Handler handler, DialogFragment dialogFragment) {
		super(handler);
	}

	@Override
	public boolean init() {

		return true;
	}

	@Override
	public void readTagId() {

		releaseCamera();
		resumeCamera();
	}

	@Override
	public void close() {

	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.cancelAutoFocus();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	private void resumeCamera() {
		autoFocusHandler = new Handler();
		/* Instance barcode scanner */
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		mCamera = getCameraInstance();
		mPreview = new CameraPreview(mContext, mCamera, previewCb, autoFocusCB);
		preview.removeAllViews();
		preview.addView(mPreview);
		if (mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			Size size = parameters.getPreviewSize();
			codeImage = new Image(size.width, size.height, "Y800");
			mPreview.refreshDrawableState();
		}
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
			//
		}
		return c;
	}

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			codeImage.setData(data);
			int result = scanner.scanImage(codeImage);
			if (result != 0) {
				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					lastScannedCode = sym.getData();
					if (lastScannedCode != null) {
						Log.d(TAG, "прочитано: " + lastScannedCode);
						scanText.setText("Результат сканирования: "
								+ lastScannedCode);

						Message message = new Message();
						message.what = RESULT_RFID_SUCCESS;
						message.obj = lastScannedCode;
						mHandler.sendMessage(message);
						releaseCamera();
					}
				}
			}
			camera.addCallbackBuffer(data);
		}
	};

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (mCamera != null) {
				mCamera.autoFocus(autoFocusCB);
			}
		}
	};

	// Mimic continuous auto-focusing
	final AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		mContext = inflater.getContext();

		View view = inflater.inflate(R.layout.qr_read, viewGroup);

		Button button = (Button) view.findViewById(R.id.cancelButton);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Message message = new Message();
				message.what = RESULT_RFID_CANCEL;
				mHandler.sendMessage(message);
			}
		});

		preview = (FrameLayout) view.findViewById(R.id.cameraPreview);
		scanText = (TextView) view.findViewById(R.id.code_from_bar);

		return view;
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {

		Message message = new Message();
		message.what = RESULT_RFID_READ_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

		Message message = new Message();
		message.what = RESULT_RFID_READ_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {

		Message message = new Message();
		message.what = RESULT_RFID_WRITE_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {

		Message message = new Message();
		message.what = RESULT_RFID_WRITE_ERROR;
		mHandler.sendMessage(message);
	}

}
