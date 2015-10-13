package ru.toir.mobile.rfid.driver;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import ru.toir.mobile.R;
import ru.toir.mobile.camera.CameraPreview;
import ru.toir.mobile.rfid.RFID;
import android.app.DialogFragment;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class RFIDQRcode implements RFIDDriver {

	private String TAG = "RFIDQRcode";
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private TextView scanText;
	private ImageScanner scanner;
	private FrameLayout preview;
	private Image codeImage;
	private String lastScannedCode;
	private Handler mHandler;
	private View mView;

	static {
		System.loadLibrary("iconv");
	};

	/**
	 * <p>
	 * Инициализируем драйвер
	 * </p>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean init(byte type) {

		return true;
	}

	/**
	 * <p>
	 * Считываем метку
	 * </p>
	 * <p>
	 * Здесь нужно запустить отдельную задачу в которой пытаемся считать метку
	 * </p>
	 * <p>
	 * Расчитано на вызов метода Callback() объекта {@link TOIRCallback} в
	 * onPostExecute() и onCancelled() объекта {@link AsyncTask}
	 * </p>
	 */
	@Override
	public void read(byte type) {
		preview = (FrameLayout) mView.findViewById(R.id.cameraPreview);
		scanText = (TextView) mView.findViewById(R.id.code_from_bar);
		// запускаем отдельную задачу для считывания метки
		releaseCamera();
		resumeCamera();
		// mTask = (ReadTagAsyncTask)new ReadTagAsyncTask().execute();
	}

	/**
	 * <p>
	 * Устанавливаем тип операции
	 * </p>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean SetOperationType(byte type) {

		return true;
	}

	/**
	 * <p>
	 * Записываем в метку
	 * </p>
	 * 
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer) {
		return false;
	}

	/**
	 * <p>
	 * Завершаем работу драйвера
	 * </p>
	 */
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
		mPreview = new CameraPreview(
				mView.getContext().getApplicationContext(), mCamera, previewCb,
				autoFocusCB);
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
						message.arg1 = RFID.RESULT_RFID_SUCCESS;
						Bundle bundle = new Bundle();
						bundle.putString(RFID.RESULT_RFID_TAG_ID,
								lastScannedCode);
						message.setData(bundle);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.toir.mobile.rfid.driver.RFIDDriver#getView(android.view.LayoutInflater
	 * , android.view.ViewGroup)
	 */
	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		mView = inflater.inflate(R.layout.qr_read, viewGroup);
		Button button = (Button) mView.findViewById(R.id.cancelButton);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Message message = new Message();
				message.arg1 = RFID.RESULT_RFID_CANCEL;
				mHandler.sendMessage(message);
			}
		});

		return mView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#setHandler(android.os.Handler)
	 */
	@Override
	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	/*
	 * (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#setActivity(android.app.DialogFragment)
	 */
	@Override
	public void setDialogFragment(DialogFragment fragment) {

	}

}
