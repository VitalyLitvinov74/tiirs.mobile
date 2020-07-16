package ru.toir.mobile.rest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.ResponseBody;
import retrofit2.*;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.ToirApplication;

/**
 * @author Dmitriy Loagachev
 *         Created by koputo on 3/22/18.
 */

public class GetDocumentationAsyncTask extends AsyncTask<String, Void, String> {

    private static final String TAG = GetDocumentationAsyncTask.class.getSimpleName();
    private ProgressDialog dialog;
    private File extDir;

    public GetDocumentationAsyncTask(ProgressDialog d, File e) {
        dialog = d;
        extDir = e;
    }

    @Override
    protected String doInBackground(String... params) {
        String url = ToirApplication.serverUrl + "/" + params[2] + "/" + params[0];
        Call<ResponseBody> call1 = ToirAPIFactory.getFileDownload().get(url);
        try {
            retrofit2.Response<ResponseBody> r = call1.execute();
            ResponseBody trueImgBody = r.body();
            if (trueImgBody == null) {
                return null;
            }

            File file = new File(extDir + "/" + params[1] + "/" + params[0]);
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    Log.e(TAG, "Не удалось создать папку " +
                            file.getParentFile().toString() +
                            " для сохранения файла изображения!");
                    return null;
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(trueImgBody.bytes());
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String filePath) {
        super.onPostExecute(filePath);
        if (filePath != null) {
            Toast.makeText(dialog.getContext(),
                    "Файл загружен успешно и готов к просмотру.",
                    Toast.LENGTH_LONG).show();
        } else {
            // сообщаем описание неудачи
            Toast.makeText(dialog.getContext(), "Ошибка при получении файла.",
                    Toast.LENGTH_LONG).show();
        }

        if (dialog != null) {
            dialog.dismiss();
        }

    }
}
