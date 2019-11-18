package ru.toir.mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader extends AsyncTask<String, Integer, String> {

    private ProgressDialog dialog;
	Context context;
    private File outputFile;

    Downloader(Context d) {
		context = d;
		dialog = new ProgressDialog(context);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancel(true);
			}
		});
        dialog.setMessage(context.getString(R.string.download_update));
		dialog.setIndeterminate(true);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... params) {

		URL url;
		HttpURLConnection connection = null;
		OutputStream outputStream = null;
		InputStream  inputStream= null;
		
		try {
			url = new URL(params[0]);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				int fileLength = connection.getContentLength();
				outputFile = new File(params[1]);
				outputStream = new FileOutputStream(outputFile);
				inputStream = connection.getInputStream();
				
				byte[] buffer = new byte[1024];
				int readLen = 0;
				long total = 0;
				while ((readLen = inputStream.read(buffer)) != -1) {
					if (isCancelled()) {
						break;
					}
					
					total += readLen;
					outputStream.write(buffer, 0, readLen);
					
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
				if (outputStream != null) {
					outputStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch(IOException e) {
			}
			if (connection != null) {
				connection.disconnect();
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
        dialog.setMax(100);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		dialog.dismiss();
		if (result == null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
			context.startActivity(intent);
		} else {
            Toast.makeText(context, context.getString(R.string.update_error), Toast.LENGTH_LONG).show();
		}

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
