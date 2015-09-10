package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import ru.toir.mobile.rfid.Scanner;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;

/**
 * @author olejek
 * <p>Драйвер считывателя RFID который "считывает" содержимое меток из текстового файла.</p>
 */
public class RFIDBarcode2D implements RFIDDriver{
	private Handler mHandler = new MainHandler();
	Activity mActivity;
	static private TextView scanText;
	
    static private class MainHandler extends Handler {
    	@Override          
    	public void handleMessage(Message msg) {
    	switch (msg.what) {   
	    	case Scanner.BARCODE_READ: {
	    		scanText.setText((String)msg.obj);
	    		break;
	    	}
	    	case Scanner.BARCODE_NOREAD:{   
	    		break;
	    	}	
	    	default:
	    	break;
	    	}  
	    }}; 

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
		Scanner.m_handler=mHandler;  
		//initialize the scanner
		Scanner.InitSCA();  		
		mActivity.setContentView(R.layout.bar2d_read);
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
        scanText = (TextView) mActivity.findViewById(R.id.code_from_bar);        
		// запускаем отдельную задачу для считывания метки
        Scanner.Read();
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
}
