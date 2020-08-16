package ru.toir.mobile.multi.serverapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.ToirApplication;
import ru.toir.mobile.multi.db.realm.User;
import ru.toir.mobile.multi.rest.ToirAPIFactory;
import ru.toir.mobile.multi.rfid.RfidDriverMsg;
import ru.toir.mobile.multi.utils.AuthLocal;
import ru.toir.mobile.multi.utils.MainFunctions;

import static ru.toir.mobile.multi.utils.MainFunctions.addToJournal;
import static ru.toir.mobile.multi.utils.RoundedImageView.getResizedBitmap;

public class GetUser extends BroadcastReceiver {

    public static final String GET_USER_ACTION = ToirApplication.packageName + ".getUser";

    @Override
    public void onReceive(Context context, Intent intent) {
        // запрашиваем актуальную информацию по пользователю
        Call<User> call = ToirAPIFactory.getUserService().user();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> userCall, Response<User> response) {

                AuthorizedUser authUser = AuthorizedUser.getInstance();
                int code = response.code();
                if (code != 200) {
                    String message = response.message() != null && !response.message().isEmpty()
                            ? response.message() : context.getString(R.string.toast_error_no_user);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

                User user = response.body();
                if (user != null) {
                    Realm realm = Realm.getDefaultInstance();

                    // получаем текущее "состояние" пользователя, если он есть в базе
                    User localUser = realm.where(User.class)
                            .equalTo("login", authUser.getLogin())
                            .findFirst();
                    if (localUser != null) {
                        localUser = realm.copyFromRealm(localUser);
                    }

                    // пинкод с сервера не передаём
                    // сохраняем хеш пинкода если пользователь получил токен
                    // для возможности проверить его ввод в отсутствии связи
                    if (authUser.loginType() == RfidDriverMsg.TYPE_LOGIN) {
                        user.setTagId("PIN:" + MainFunctions.md5(authUser.getPassword()));
                    }

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(user);
                    realm.commitTransaction();
                    realm.close();

                    authUser.setUuid(user.getUuid());
                    authUser.setOrganizationUuid(user.getOrganization().getUuid());
                    authUser.setLogin(user.getLogin());
                    addToJournal("Пользователь " + user.getName() + " с uuid["
                            + user.getUuid() + "] зарегистрировался на клиенте и получил токен");

                    // проверяем, пользователь уже работает или на экране входа
                    if (!authUser.isLogged()) {
                        // "впускаем" пользователя если он активен
                        Intent extraActionIntent = new Intent(AuthLocal.LOG_IN_ACTION);
                        extraActionIntent.putExtra(AuthLocal.EXTRA_ACTION_NAME,
                                user.isActive() == 1 ? AuthLocal.ACCESS_ALLOWED : AuthLocal.ACCESS_DENIED);
                        context.sendBroadcast(extraActionIntent);
                    }

                    // сохраняем информацию о пользователе в списке логинов для входа
                    HashMap<String, String> loginList = User.getLoginList(context);
                    loginList.put(user.getLogin(), user.getName());
                    User.saveLoginList(context, loginList);

                    // тянем изображение пользователя если нужно
                    downloadUserImage(context, user, localUser);
                } else {
                    // пользователя не получили с сервера
                    String message = context.getString(R.string.toast_error_no_user);
                    addToJournal("Информация о пользователе с ID "
                            + AuthorizedUser.getInstance().getIdentity()
                            + " не получена с сервера.");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if (!authUser.isLocalLogged()) {
                        Intent nextActionIntent = new Intent(AuthLocal.AUTH_LOCAL_ACTION);
                        context.sendBroadcast(nextActionIntent);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> userCall, Throwable t) {
                AuthorizedUser authUser = AuthorizedUser.getInstance();
                // сообщаем описание неудачи
                // TODO нужен какой-то механизм уведомления о причине не успеха
                String message = context.getString(R.string.toast_error_no_user_received);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if (!authUser.isLocalLogged()) {
                    Intent nextActionIntent = new Intent(AuthLocal.AUTH_LOCAL_ACTION);
                    context.sendBroadcast(nextActionIntent);
                }
            }
        });
    }

    /**
     * Запрашиваем файл изображения пользователя с сервера при необходимости
     *
     * @param context    Context
     * @param serverUser User
     * @param localUser  User
     */
    private void downloadUserImage(Context context, User serverUser, User localUser) {

        final String fileName = serverUser.getImage();
        File localImageFile;
        boolean needDownloadImage = false;
        if (localUser != null) {
            Date serverDate = serverUser.getChangedAt();
            Date localDate = localUser.getChangedAt();
            File fileDir = context.getExternalFilesDir(localUser.getImageFilePath() + "/");
            localImageFile = new File(fileDir, localUser.getImage());
            if (localDate.getTime() < serverDate.getTime() || !localImageFile.exists()) {
                needDownloadImage = true;
            }
        } else {
            needDownloadImage = true;
        }

        // получаем изображение пользователя
        if (needDownloadImage && !fileName.equals("")) {
            String url = ToirApplication.serverUrl + "/"
                    + serverUser.getImageFileUrl(serverUser.getLogin()) + "/"
                    + serverUser.getImage();
            Call<ResponseBody> callFile = ToirAPIFactory.getFileDownload().get(url);
            callFile.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> responseBodyCall, Response<ResponseBody> response) {
                    ResponseBody fileBody = response.body();
                    if (fileBody == null) {
                        return;
                    }

                    File filePath = context.getExternalFilesDir("/" + User.getImageRoot());
                    if (filePath == null) {
                        // нет доступа к внешнему накопителю
                        return;
                    }

                    File file = new File(filePath, fileName);
                    if (!file.getParentFile().exists()) {
                        if (!file.getParentFile().mkdirs()) {
                            return;
                        }
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(fileBody.bytes());
                        fos.close();
                        // принудительно масштабируем изображение пользоваетеля
                        String path = filePath + File.separator;
                        Bitmap user_bitmap = getResizedBitmap(path, fileName, 0, 600, Long.MAX_VALUE);
                        if (user_bitmap == null) {
                            // По какой-то причине не смогли получить
                            // уменьшенное изображение
                            Log.e("GetUser", context.getString(R.string.toast_error_picture));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> responseBodyCall, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}
