/**
 * 
 */
package ru.toir.mobile.rest;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 *
 */
public class ProcessorService extends Service {
	private Integer lastStarId;
	private final Context mContext = this;
	public static class Extras {
		public static final String PROVIDER_EXTRA = "PROVIDER_EXTRA";
		public static final String METHOD_EXTRA = "METHOD_EXTRA";
		public static final String RESULT_ACTION_EXTRA = "RESULT_ACTION_EXTRA";
		public static final String RESULT_EXTRA = "RESULT_EXTRA";
	}
	private final HashMap<String, AsyncServiceTask> mTasks = new  HashMap<String, AsyncServiceTask>();
	public class Providers {
		public static final int USERS_PROVIDER = 1;
		public static final int TOKEN_PROVIDER = 2;
		public static final int TASK_PROVIDER = 3;
		public static final int EQUIPMENT_OPERATION_RESULT_PROVIDER = 4;
	}
	
	private IServiceProvider GetProvider(int provider) {
		switch (provider) {
		case Providers.USERS_PROVIDER:
			return new UsersServiceProvider(this);
		case Providers.TOKEN_PROVIDER:
			return new TokenServiceProvider(this);
		case Providers.TASK_PROVIDER:
			return new TaskServiceProvider(this);
		case Providers.EQUIPMENT_OPERATION_RESULT_PROVIDER:
			return new EquipmentOperationResultServiceProvider(this);
		}
		return null;
	}

	private String getTaskIdentifier(Bundle extras) {
		// TODO разобраться что делает это код!
		String[] keys = extras.keySet().toArray(new String[0]);
		java.util.Arrays.sort(keys);
		StringBuilder identifier = new StringBuilder();
		
		for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
			String key = keys[keyIndex];
			// TODO разобраться в этом коде!
			if (key.equals(Extras.RESULT_ACTION_EXTRA)) {
				continue;
			}
			
			identifier.append("{");
			identifier.append(key);
			identifier.append(":");
			identifier.append(extras.get(key).toString());
			identifier.append("}");
		}
		Log.d("test", identifier.toString());
		return identifier.toString();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		synchronized (mTasks) {
			lastStarId = startId;
			Bundle extras = intent.getExtras();
			String taskIdentifier = getTaskIdentifier(extras);
			AsyncServiceTask task = mTasks.get(taskIdentifier);
			
			if (task == null) {
				task = new AsyncServiceTask(taskIdentifier, extras);
				mTasks.put(taskIdentifier, task);
				task.execute((Void[]) null);
			}
			
			// в данном случае это строка для фильтра broadcastreceiver
			String resultAction = extras.getString(Extras.RESULT_ACTION_EXTRA);
			if (resultAction != "") {
				task.addResultAction(resultAction);
			}
		}
		return START_STICKY;
	}
	
	public class AsyncServiceTask extends AsyncTask<Void, Integer, Boolean> {
		private final Bundle mExtras;
		private final ArrayList<String> mResultAction = new ArrayList<String>();
		private final String mTaskIdentifier;
		
		public AsyncServiceTask(String identifier, Bundle extras) {
			mExtras = extras;
			mTaskIdentifier = identifier;
		}
		
		public void addResultAction(String resultAction) {
			if (!mResultAction.contains(resultAction)) {
				mResultAction.add(resultAction);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = false;
			final int providerId = mExtras.getInt(Extras.PROVIDER_EXTRA);
			final int methodId = mExtras.getInt(Extras.METHOD_EXTRA);
			
			if (providerId != 0 && methodId != 0) {
				final IServiceProvider provider = GetProvider(providerId);
				if (provider != null) {
					try {
						result = provider.RunTask(methodId, mExtras);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			synchronized (mTasks) {
				// бежим по списку строк фильтров для broadcastreceiver`ов которым нужно отправить уведомление о завершении операции
				for (int i = 0; i < mResultAction.size(); i++) {
					Intent resultIntent = new Intent(mResultAction.get(i));
					resultIntent.putExtra(Extras.RESULT_EXTRA, result.booleanValue());
					resultIntent.putExtras(mExtras);
					resultIntent.setPackage(mContext.getPackageName());
					mContext.sendBroadcast(resultIntent);
				}
				
				mTasks.remove(mTaskIdentifier);
				
				if (mTasks.size() < 1) {
					stopSelf(lastStarId);
				}
			}
		}
		
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("test", "ProcessorService onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("test", "ProcessorService onDestroy");
	}
}
