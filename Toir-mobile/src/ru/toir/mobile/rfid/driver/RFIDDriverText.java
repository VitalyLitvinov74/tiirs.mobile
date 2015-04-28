package ru.toir.mobile.rfid.driver;


import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

/**
 * @author koputo
 * <p>Драйвер считывателя RFID который "считывает" содержимое меток из текстового файла.</p>
 */
public class RFIDDriverText implements RFIDDriver{
	
	TOIRCallback callback;
	ReadTagAsyncTask task;
	
	/**
	 * <p>Инициализируем драйвер</p>
	 * @param TOIRCallback
	 * @return boolean
	 */
	@Override
	public boolean init(TOIRCallback callback) {
		this.callback = callback;
		return true;
	}
	
	/**
	 * <p>Считываем метку</p>
	 * <p>Здесь нужно запустить отдельную задачу в которой пытаемся считать метку</p>
	 * <p>Расчитано на вызов метода Callback() объекта {@link TOIRCallback} в onPostExecute() и onCancelled() объекта {@link AsyncTask}</p>
	 */
	@Override
	public void read() {
		// запускаем отдельную задачу для считывания метки
		task = (ReadTagAsyncTask)new ReadTagAsyncTask().execute();
	}
	
	/**
	 * <p>Записываем в метку</p>
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer){
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * <p>Завершаем работу драйвера</p>
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	/** 
	 * <p>Добавляем в переданное меню элементы которые будут отвечать за считывание меток</p>
	 */
	@Override
	public void getMenu(Menu menu) {
		
		MenuItem item = menu.add(0, 0, 1, "Метка 01234567");
		
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.d("test", "01234567");
				// отменяем выполнение задачи
				task.cancel(true);
				callback.Callback("01234567");
				return true;
			}
		});
		
		item = menu.add(0, 0, 1, "Метка 00000001");
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.d("test", "00000001");
				// отменяем выполнение задачи
				task.cancel(true);
				callback.Callback("00000001");
				return true;
			}
		});
	}
	
	public class ReadTagAsyncTask extends AsyncTask<String, Integer, String> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... params) {
			// симулируем считывание метки
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
		}
		
	}
}
