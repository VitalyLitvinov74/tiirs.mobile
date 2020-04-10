package ru.toir.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;

import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.serverapi.IDownloadComplete;

public class Downloader extends AsyncTask<String, Integer, String> {

    private ProgressDialog dialog;
    private File outputFile;
    private Activity context;

    public void setContext(Activity contextf) {
        context = contextf;
    }

    public Downloader(ProgressDialog dialog) {
        this.dialog = dialog;
        HttpsURLConnection.setDefaultSSLSocketFactory(ToirAPIFactory.sslsf);
    }

    public static boolean isAPK(File file) {
        FileInputStream fis = null;
        ZipInputStream zipIs = null;
        ZipEntry zEntry = null;
        String dexFile = "classes.dex";
        String manifestFile = "AndroidManifest.xml";
        boolean hasDex = false;
        boolean hasManifest = false;

        try {
            fis = new FileInputStream(file);
            zipIs = new ZipInputStream(new BufferedInputStream(fis));
            while ((zEntry = zipIs.getNextEntry()) != null) {
                if (zEntry.getName().equalsIgnoreCase(dexFile)) {
                    hasDex = true;
                } else if (zEntry.getName().equalsIgnoreCase(manifestFile)) {
                    hasManifest = true;
                }
                if (hasDex && hasManifest) {
                    zipIs.close();
                    fis.close();
                    return true;
                }
            }
            zipIs.close();
            fis.close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(String... params) {

        URL url;
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
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
                        int progress = (int) ((total * 100) / fileLength);
//                        Log.d("xxxx", "total: " + total + ", progress: " + progress);
                        publishProgress(progress);
                    }
                }

                return null;
            } else {
                return String.valueOf(HttpURLConnection.HTTP_OK);
            }
        } catch (IOException e) {
            return e.toString();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialog.dismiss();
        if (result == null && isAPK(outputFile)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
                context.startActivity(intent);
            } else {
                Uri apk = FileProvider.getUriForFile(dialog.getContext(), dialog.getContext().getPackageName() + ".provider", outputFile);
                Intent intent = new Intent(context, MainActivity.class);
                //intent.setDataAndType(apk, "application/vnd.android" + ".package-archive");
                intent.setData(apk);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setAction(Intent.ACTION_RUN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    //context.startActivity(Intent.createChooser(intent, "Open File"));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(dialog.getContext(), dialog.getContext().getString(R.string.update_error), Toast.LENGTH_LONG).show();
                }
            }
            //intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
        } else {
            Toast.makeText(dialog.getContext(), dialog.getContext().getString(R.string.update_error), Toast.LENGTH_LONG).show();
        }
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMax(100);
        dialog.setProgress(values[0]);
    }
}
