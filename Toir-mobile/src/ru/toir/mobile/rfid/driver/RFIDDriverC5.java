package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.OperationActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.RFIDActivity;
import ru.toir.mobile.EquipmentInfoActivity;
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
	public final static int READ_USER_LABLE = 1;
	public final static int READ_EQUIPMENT_LABLE = 2;
	public final static int RW_OPERATION_LABLE = 3;

	static Activity mActivity;	
	private Handler mHandler = new MainHandler();
	static String m_strresult="";
	static int m_nCount=0;
	CheckBox m_check;  
	EditText m_address;
	static byte types=0;	
		
	@Override
	public void setActivity(Activity activity) {
		mActivity = activity;
	}
	
	/**
	 * <p>Инициализируем драйвер</p>
	 * @return boolean
	 */
	@Override
	public boolean init(byte type) {
		types=type;
		if (type==READ_USER_LABLE)
			{
			 mActivity.setContentView(R.layout.bar2d_read);			 
			 mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		reader.m_handler=mHandler;
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
	public void read(byte type) {
        //scanText = (TextView) mActivity.findViewById(R.id.code_from_bar);        
		if (type <= READ_USER_LABLE)			
			android.hardware.uhf.magic.reader.InventoryLablesLoop();
		else
			android.hardware.uhf.magic.reader.InventoryLables();
		reader.m_strPCEPC = "";
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
    				//Log.e("8888888888",(String)msg.obj+"\r\n");
					m_strresult +=(String)msg.obj;
    				Toast.makeText(mActivity.getApplicationContext(),"Код: " + m_strresult,Toast.LENGTH_LONG).show();					
					//m_strresult+="\r\n";
					if (types < READ_USER_LABLE)
						{
						 m_strresult = "01234567";					
						 reader.StopLoop();
						}
					if (types==READ_USER_LABLE)
						{
						 //m_strresult+="\r\n";
						 ((RFIDActivity)mActivity).Callback(m_strresult);
						}
					if (types>READ_USER_LABLE)				
						{
						 if (reader.m_strPCEPC!=null && !reader.m_strPCEPC.equals(""))							 
						 	{
							 if (!reader.m_strPCEPC.equals(m_strresult))
							 	{
								 if (types==READ_EQUIPMENT_LABLE) ((EquipmentInfoActivity)mActivity).Callback(m_strresult);
								 if (types==RW_OPERATION_LABLE) ((OperationActivity)mActivity).Callback(m_strresult);
							 	}
						 	}
						 else
						 	{
							 reader.m_strPCEPC = m_strresult;						 						 
							 if (reader.m_strPCEPC!=null && !reader.m_strPCEPC.equals(""))
						 		{								 	
								 byte[] epc = reader.stringToBytes(reader.m_strPCEPC);
								 byte memoryBank = 3; 		// user memory
								 int address = 0;			// читаем всегда с начала
								 int dataLength = 32;		// длина памяти данных
								 String passwordString = "00000000";		// пароль
								 byte[] password = reader.stringToBytes(passwordString);
								 try {
									Thread.sleep(690);
								 	} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									 e.printStackTrace();
								 	}
								 m_strresult="";
								 reader.ReadLables(password, epc.length, epc, memoryBank, address, dataLength);
						 		}
						 	}
						}
					m_strresult="";
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
