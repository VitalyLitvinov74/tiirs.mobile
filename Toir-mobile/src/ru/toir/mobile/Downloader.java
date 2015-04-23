package ru.toir.mobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

public class Downloader extends AsyncTask<String, Integer, String> {
	
	ProgressDialog dialog;
	Context context;
	
	public Downloader(Context d) {
		context = d;
		dialog = new ProgressDialog(context);
		dialog.setMessage("Загрузка обновления");
		dialog.setIndeterminate(true);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    @Override
		    public void onCancel(DialogInterface dialog) {
		    	Log.d("test", "onCancel");
		    }
		});
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... params) {

		URL url;
		HttpURLConnection c = null;
		FileOutputStream fos = null;
		InputStream fis = null;
		
		try {
			url = new URL(params[0]);
			c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				int fileLength = c.getContentLength();
				Log.d("test", "fileLength = " + fileLength);
				File of = new File(params[1]);
				fos = new FileOutputStream(of);
				fis = c.getInputStream();
				
				byte[] buffer = new byte[1024];
				int readLen = 0;
				long total = 0;
				while ((readLen = fis.read(buffer)) != -1) {
					if (isCancelled()) {
						fis.close();
						return "Canceled";
					}
					
					total += readLen;
					fos.write(buffer, 0, readLen);
					
					if (fileLength > 0) {
	                    publishProgress((int) (total * 100 / fileLength));
					}
				}
				
				return null;
			} else {
				return String.valueOf(HttpURLConnection.HTTP_OK);
			}
		} catch(IOException e) {
			return e.toString();
		}
		finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch(IOException e) {
				
			}
			if (c != null) {
				c.disconnect();
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog.show();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		dialog.dismiss();
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgress(values[0]);
	}
}
