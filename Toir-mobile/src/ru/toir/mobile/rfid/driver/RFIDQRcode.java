package ru.toir.mobile.rfid.driver;


import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import ru.toir.mobile.R;
import ru.toir.mobile.RFIDActivity;
import ru.toir.mobile.camera.CameraPreview;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author koputo
 * <p>Драйвер считывателя RFID который "считывает" содержимое меток из текстового файла.</p>
 */
public class RFIDQRcode implements RFIDDriver{
	
	ReadTagAsyncTask mTask;
	Activity mActivity;
	
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private TextView scanText;
	private ImageScanner scanner;
	private FrameLayout preview;
	private Image codeImage;
	private String lastScannedCode;
	
	static {
        System.loadLibrary("iconv");
		};
	
	@Override
	public void setActivity(Activity activity) {
		mActivity = activity;
	}
	
	/**
	 * <p>Инициализируем драйвер</p>
	 * @return boolean
	 */
	@Override
	public boolean init() {
		mActivity.setContentView(R.layout.qr_read);
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		return true;
	}

	/**
	 * <p>Считываем метку</p>
	 * <p>Здесь нужно запустить отдельную задачу в которой пытаемся считать метку</p>
	 * <p>Расчитано на вызов метода Callback() объекта {@link TOIRCallback} в onPostExecute() и onCancelled() объекта {@link AsyncTask}</p>
	 */
	@Override
	public void read() {
		preview = (FrameLayout) mActivity.findViewById(R.id.cameraPreview);       
        scanText = (TextView) mActivity.findViewById(R.id.code_from_bar);        
		// запускаем отдельную задачу для считывания метки
        releaseCamera();
		resumeCamera();
		//mTask = (ReadTagAsyncTask)new ReadTagAsyncTask().execute();
	}
	
	/**
	 * <p>Записываем в метку</p>
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer){
		return false;
	}

	/**
	 * <p>Завершаем работу драйвера</p>
	 */
	@Override
	public void close() {
	}

	/** 
	 * <p>Добавляем в переданное меню элементы которые будут отвечать за считывание меток</p>
	 */
	@Override
	public void getMenu(Menu menu) {
	}
	
	public class ReadTagAsyncTask extends AsyncTask<String, Integer, String> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... params) {
			// сканируем
			while (true) {
				try {
					if (isCancelled()) {
						break;
					}
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			releaseCamera();			
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// в данном случае мы не вызываем callback, так как реального считывания не происходит и данные мы вернём из обработчика меню
			// но в реальных условиях этот метод вызывается как только происходит выход из doInBackground 
			//callback.Callback(result);
			// add 18.08
			releaseCamera();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {
			super.onCancelled();
			// чтение отменено, возвращаем null
			// в данном случае мы не вызываем callback, так как нам необходимо обязательно остановить выполнение задачи,
			// а данные мы вернём из обработчика меню
			//callback.Callback(null);
			// add 18.08
			releaseCamera();
		}
		
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
        mPreview = new CameraPreview(mActivity.getApplicationContext(), mCamera, previewCb, autoFocusCB);
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
                    scanText.setText("Результат сканирования: " + lastScannedCode);
                    // !!!! hardcoded
                    lastScannedCode = "01234567";
                    ((RFIDActivity)mActivity).Callback(lastScannedCode);
        			releaseCamera();                    
                }
            }
        }
        camera.addCallbackBuffer(data);
    }};

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
}
