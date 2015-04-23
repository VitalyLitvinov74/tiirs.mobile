package ru.toir.mobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class Downloader extends AsyncTask<String, Void, String>{
	
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
				File of = new File(params[1]);
				fos = new FileOutputStream(of);
				fis = c.getInputStream();
				
				byte[] buffer = new byte[1024];
				int readLen = 0;
				while ((readLen = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, readLen);
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
}
