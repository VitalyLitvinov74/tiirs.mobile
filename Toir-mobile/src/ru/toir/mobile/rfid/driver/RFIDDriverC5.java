package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.RFIDActivity;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.uhf.magic.reader;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author koputo
 * <p>Драйвер считывателя RFID который "считывает" содержимое меток из текстового файла.</p>
 */
public class RFIDDriverC5 implements RFIDDriver{
	static Activity mActivity;	
	private Handler mHandler = new MainHandler();
	static String m_strresult="";
	static int m_nCount=0;
	CheckBox m_check;  
	EditText m_address;
	
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
		mActivity.setContentView(R.layout.bar2d_read);
		reader.m_handler=mHandler;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
        android.hardware.uhf.magic.reader.init("/dev/ttyMT1");
        android.hardware.uhf.magic.reader.Open("/dev/ttyMT1");
        Log.e("7777777777","111111111111111111111111111111111111");
        if(reader.SetTransmissionPower(1950)!=0x11)
        {
	        if(reader.SetTransmissionPower(1950)!=0x11)
	        {
	        	reader.SetTransmissionPower(1950);
	        }
        }
		return true;
	}

	/**
	 * <p>Считываем метку</p>
	 * <p>Здесь нужно запустить отдельную задачу в которой пытаемся считать метку</p>
	 * <p>Расчитано на вызов метода Callback() объекта {@link TOIRCallback} в onPostExecute() и onCancelled() объекта {@link AsyncTask}</p>
	 */
	@Override
	public void read() {
        //scanText = (TextView) mActivity.findViewById(R.id.code_from_bar);        
        android.hardware.uhf.magic.reader.InventoryLablesLoop();
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
		reader.StopLoop();
	}

	/** 
	 * <p>Добавляем в переданное меню элементы которые будут отвечать за считывание меток</p>
	 */
	@Override
	public void getMenu(Menu menu) {
	}
	
	static private class MainHandler extends Handler {  
    	@Override           
    	public void handleMessage(Message msg) {
    		if(msg.what!=0)
    		{  
    			if(m_strresult.indexOf((String)msg.obj)<0)
    			{  
    				Log.e("8888888888",(String)msg.obj+"\r\n");
					m_strresult +=(String)msg.obj;
    				Toast.makeText(mActivity.getApplicationContext(),
							"Код: " + m_strresult,
							Toast.LENGTH_LONG).show();					
					m_strresult+="\r\n";
					m_strresult = "01234567";
					reader.StopLoop();
					((RFIDActivity)mActivity).Callback(m_strresult);					
    			}  
    			m_nCount++;
    			//Log.e("8888888888",m_nCount+"\r\n");
    			if(m_nCount>65534)
    			{
    				m_nCount=0;
    			}
			 // android.hardware.uhf.magic.reader.StopLoop();
    		}
  
    	}
    }; 
	
}
