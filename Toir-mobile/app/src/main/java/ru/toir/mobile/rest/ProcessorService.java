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

    private final Context mContext = this;
    private final HashMap<String, AsyncServiceTask> mTasks = new HashMap<>();
    private Integer lastStarId;

    private IServiceProvider GetProvider(int provider) {

        switch (provider) {
            case Providers.TASK_PROVIDER:
                return new TaskServiceProvider(this);
            case Providers.REFERENCE_PROVIDER:
                return new ReferenceServiceProvider(this);
        }
        return null;
    }

    private String getTaskIdentifier(Bundle extras) {

        // получаем список имён всех параметров запроса, для создания "хэша"
        String[] keys = extras.keySet().toArray(new String[]{""});
        java.util.Arrays.sort(keys);
        StringBuilder identifier = new StringBuilder();

        for (String key : keys) {
            /*
			 * для идентификации запроса значение фильтра для сообщений не
			 * добавляем, так как ответ на один запрос могут ждать разные части
			 * приложения
			 */
            if (key.equals(Extras.RESULT_ACTION_EXTRA)) {
                continue;
            }

            identifier.append("{");
            identifier.append(key);
            identifier.append(":");

            Object data = extras.get(key);
            if (data == null) {
                identifier.append("");
            } else {
                identifier.append(data.toString());
            }

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
            if (resultAction != null && !resultAction.equals("")) {
                task.addResultAction(resultAction);
            }
        }
        return START_STICKY;
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

    public static class Extras {
        public static final String PROVIDER_EXTRA = "PROVIDER_EXTRA";
        public static final String METHOD_EXTRA = "METHOD_EXTRA";
        public static final String RESULT_ACTION_EXTRA = "RESULT_ACTION_EXTRA";
        public static final String RESULT_EXTRA = "RESULT_EXTRA";
        public static final String RESULT_BUNDLE = "RESULT_BUNDLE";
    }

    public class Providers {
        public static final int TASK_PROVIDER = 3;
        public static final int REFERENCE_PROVIDER = 4;
    }

    public class AsyncServiceTask extends AsyncTask<Void, Integer, Bundle> {

        private final Bundle mExtras;
        private final ArrayList<String> mResultAction = new ArrayList<>();
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
        protected Bundle doInBackground(Void... params) {

            Bundle result;

            final int providerId = mExtras.getInt(Extras.PROVIDER_EXTRA);
            final int methodId = mExtras.getInt(Extras.METHOD_EXTRA);

            if (providerId != 0 && methodId != 0) {
                final IServiceProvider provider = GetProvider(providerId);
                if (provider != null) {
                    try {
                        return provider.RunTask(methodId, mExtras);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            synchronized (mTasks) {
                // бежим по списку строк фильтров для broadcastreceiver`ов
                // которым нужно отправить уведомление о завершении операции
                for (int i = 0; i < mResultAction.size(); i++) {
                    Intent resultIntent = new Intent(mResultAction.get(i));
                    boolean success = result
                            .getBoolean(IServiceProvider.RESULT);
                    resultIntent.putExtra(Extras.RESULT_EXTRA, success);
                    resultIntent.putExtra(Extras.RESULT_BUNDLE, result);
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
}
