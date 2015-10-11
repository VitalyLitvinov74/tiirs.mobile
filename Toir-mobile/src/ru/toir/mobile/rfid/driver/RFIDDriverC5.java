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
	public final static int READ_EQUIPMENT_LABLE_ID = 2;
	public final static int READ_EQUIPMENT_OPERATION_LABLE_ID = 3;
	public final static int READ_EQUIPMENT_MEMORY = 4;
	public final static int READ_EQUIPMENT_OPERATION_MEMORY = 5;
	public final static int WRITE_EQUIPMENT_OPERATION_MEMORY = 6;
	public final static int WRITE_EQUIPMENT_MEMORY = 7;
	public final static int WRITE_USER_MEMORY = 8;
	
	public final static int USER_MEMORY_BANK = 3;

	static Activity mActivity;	
	private Handler mHandler = new MainHandler();
	static String m_strresult="";
	static int m_nCount=0;
	CheckBox m_check;  
	EditText m_address;
	static byte types=0;
	static String mPCEPC="";
		
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
			 //mActivity.setContentView(R.layout.bar2d_read);			 
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
	 * <p>Устанавливаем тип операции</p>
	 * @return boolean
	 */
	@Override
	public boolean SetOperationType(byte type) {
		types=type;
		return true;
	}

	/**
	 * <p>Считываем метку</p>
	 * <p>Здесь нужно запустить отдельную задачу в которой пытаемся считать метку</p>
	 * <p>Расчитано на вызов метода Callback() объекта {@link TOIRCallback} в onPostExecute() и onCancelled() объекта {@link AsyncTask}</p>
	 */
	@Override
	public void read(byte type) {
		types=type;
        //scanText = (TextView) mActivity.findViewById(R.id.code_from_bar);        
		if (type <= READ_USER_LABLE)			
			android.hardware.uhf.magic.reader.InventoryLablesLoop();
		if (type == READ_EQUIPMENT_LABLE_ID || type == READ_EQUIPMENT_OPERATION_LABLE_ID)
			android.hardware.uhf.magic.reader.InventoryLables();
		if (type == READ_EQUIPMENT_MEMORY || type == READ_EQUIPMENT_OPERATION_MEMORY)
			{
			 reader.m_strPCEPC=mPCEPC;
			 byte[] epc = reader.stringToBytes(reader.m_strPCEPC);
			 byte memoryBank = USER_MEMORY_BANK;	// user memory
			 int address = 0;						// читаем всегда с начала
			 int dataLength = 32;					// длина памяти данных
			 String passwordString = "00000000";	// пароль
			 byte[] password = reader.stringToBytes(passwordString);
			 try {
				 Thread.sleep(690);
		 		} catch (InterruptedException e) {
		 			e.printStackTrace();
		 		}
			 m_strresult="";
			 reader.ReadLables(password, epc.length, epc, memoryBank, address, dataLength);
			}
		//reader.m_strPCEPC = "";
	}

	/**
	 * <p>Записываем в метку</p>
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer){		
		if (types==WRITE_EQUIPMENT_OPERATION_MEMORY || types==WRITE_EQUIPMENT_MEMORY || types==WRITE_USER_MEMORY)
			{
			 //byte[] epc = reader.stringToBytes(reader.m_strPCEPC);
			 byte[] epc = reader.stringToBytes(mPCEPC);
			 byte memoryBank = USER_MEMORY_BANK;		// user memory
			 int address = 0;							// пишем буфер с начала
			 //int dataLength = 32;						// длина памяти данных
			 byte[] dataForWrite = new byte[50];
			 //int dataLength = Integer.valueOf(outBuffer.length) * 2;
			 String passwordString = "00000000";		// пароль
			 byte[] password = reader.stringToBytes(passwordString);
			 
			 int rc = reader.Writelables(password, epc.length, epc,
						memoryBank, (byte) address, (byte) 24,
						outBuffer);
			 System.arraycopy(outBuffer, 24, dataForWrite, 0, 24);
			 
			 rc = reader.Writelables(password, epc.length, epc,
						memoryBank, (byte) address+48, (byte) 12,
						dataForWrite);
			 /*
			 int rc = reader.Writelables(password, epc.length, epc,
					memoryBank, (byte) address, (byte) realDataLength,
					dataForWrite);
				*/		 
			 if (rc>=0) return true;
			 else return false;
			 
			}
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
    				// возврат при чтении метки пользователя
					if (types == READ_USER_LABLE || types==0)
						{						 
						 //m_strresult = "01234567";					
						 reader.StopLoop();						 
						 ((RFIDActivity)mActivity).Callback(m_strresult);
						}
					// возврат при чтении метки оборудования
					if (types==READ_EQUIPMENT_LABLE_ID || types==READ_EQUIPMENT_OPERATION_LABLE_ID)				
						{
						 reader.m_strPCEPC = m_strresult;
						 mPCEPC = m_strresult;
						 if (types==READ_EQUIPMENT_LABLE_ID)
							 ((EquipmentInfoActivity)mActivity).CallbackOnReadLable(m_strresult);
						 if (types==READ_EQUIPMENT_OPERATION_LABLE_ID)
							 ((OperationActivity)mActivity).CallbackOnReadLable(m_strresult);
						}
					// возврат при чтении памяти оборудования
					if (types==READ_EQUIPMENT_MEMORY || types==READ_EQUIPMENT_OPERATION_MEMORY)				
						{
						 if (mPCEPC!=null && !mPCEPC.equals(""))
						 	{							 
							 if (types==READ_EQUIPMENT_MEMORY)
								 ((EquipmentInfoActivity)mActivity).Callback(m_strresult);
							 if (types==READ_EQUIPMENT_OPERATION_MEMORY)
								 ((OperationActivity)mActivity).Callback(m_strresult);
						 	}
						}
					// возврат при записи памяти оборудования
					if (types==WRITE_EQUIPMENT_OPERATION_MEMORY)				
						 ((OperationActivity)mActivity).CallbackOnWrite(m_strresult);
					// возврат при записи памяти оборудования
					if (types==WRITE_EQUIPMENT_MEMORY || types==WRITE_USER_MEMORY)
						 ((EquipmentInfoActivity)mActivity).CallbackOnWrite(m_strresult);						
					//m_strresult="";
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
