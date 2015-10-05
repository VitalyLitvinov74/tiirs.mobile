package ru.toir.mobile.rfid.driver;


import ru.toir.mobile.RFIDActivity;
import android.app.Activity;
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
	
	ReadTagAsyncTask mTask;
	Activity mActivity;

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
		return true;
	}
	
	/**
	 * <p>Считываем метку</p>
	 * <p>Здесь нужно запустить отдельную задачу в которой пытаемся считать метку</p>
	 * <p>Расчитано на вызов метода Callback() объекта {@link TOIRCallback} в onPostExecute() и onCancelled() объекта {@link AsyncTask}</p>
	 */
	@Override
	public void read(byte type) {
		// запускаем отдельную задачу для считывания метки
		mTask = (ReadTagAsyncTask)new ReadTagAsyncTask().execute();
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
	 * <p>Устанавливаем тип операции</p>
	 * @return boolean
	 */
	@Override
	public boolean SetOperationType(byte type) {
		types=type;
		return true;
	}

	/** 
	 * <p>Добавляем в переданное меню элементы которые будут отвечать за считывание меток</p>
	 */
	@Override
	public void getMenu(Menu menu) {
		String[] tags = {"01234567","00000001","00000002","00000003","00000004", "00000005"};
		MenuItem item;
		int i = 0;
		
		for (String tag: tags) {
			item = menu.add(0, i, i + 1, tag);
			item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					String tag = String.valueOf(item.getTitle());
					Log.d("test", tag);
					// отменяем выполнение задачи
					mTask.cancel(true);
					//mCallback.Callback(tag);
					((RFIDActivity)mActivity).Callback(tag);
					return true;
				}
			});
			i++;
		}
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
