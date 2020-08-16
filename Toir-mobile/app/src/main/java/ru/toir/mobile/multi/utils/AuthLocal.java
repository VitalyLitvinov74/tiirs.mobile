package ru.toir.mobile.multi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import io.realm.Realm;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.ToirApplication;
import ru.toir.mobile.multi.db.ToirRealm;
import ru.toir.mobile.multi.db.realm.User;
import ru.toir.mobile.multi.rfid.RfidDriverMsg;

import static ru.toir.mobile.multi.utils.MainFunctions.addToJournal;

public class AuthLocal extends BroadcastReceiver {

    public static final String LOG_IN_ACTION = ToirApplication.packageName + ".logIn";
    public static final String EXTRA_ACTION_NAME = "extraAction";
    public static final String ACCESS_DENIED = "accessDenied";
    public static final String ACCESS_ALLOWED = "accessAllowed";
    public static String AUTH_LOCAL_ACTION = ToirApplication.packageName + ".authLocal";

    @Override
    public void onReceive(Context context, Intent intent) {

        AuthorizedUser authUser = AuthorizedUser.getInstance();
        Intent extraActionIntent = new Intent(LOG_IN_ACTION);
        String dbName = User.getUserDbName(context, authUser.getIdentity());
        if (dbName == null) {
            Toast.makeText(context, context.getText(R.string.no_user_found), Toast.LENGTH_LONG)
                    .show();
            extraActionIntent.putExtra(EXTRA_ACTION_NAME, ACCESS_DENIED);
            context.sendBroadcast(extraActionIntent);
            return;
        }

        // инициализируем базу пользователя
        ToirRealm.initDb(context, dbName);

        // проверяем наличие пользователя в локальной базе
        Realm realmDB = Realm.getDefaultInstance();
        User user = realmDB.where(User.class)
                .equalTo("tagId", authUser.getIdentity())
                .or()
                .equalTo("login", authUser.getIdentity())
                .findFirst();
        // в зависимости от результата либо дать работать, либо не дать
        if (user != null && user.isActive() == 1) {
            // сразу выставляем все флаги
            authUser.setDbName(dbName);
            authUser.setLogin(user.getLogin());
            authUser.setUuid(user.getUuid());
            authUser.setOrganizationUuid(user.getOrganization().getUuid());
            authUser.setLocalLogged(true);

            // проверяем пин если входят по нему
            if (authUser.loginType() == RfidDriverMsg.TYPE_LOGIN) {
                String[] split = user.getTagId().split(":");
                if (split[1].equals(MainFunctions.md5(authUser.getPassword()))) {
                    addToJournal("Пользователь " + user.getName() + " с uuid[" + user.getUuid()
                            + "] зарегистрировался на клиенте");
                    extraActionIntent.putExtra(EXTRA_ACTION_NAME, ACCESS_ALLOWED);
                } else {
                    extraActionIntent.putExtra(EXTRA_ACTION_NAME, ACCESS_DENIED);
                    Toast.makeText(context, context.getString(R.string.toast_error_no_access),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                // вошли по метке
                extraActionIntent.putExtra(EXTRA_ACTION_NAME, ACCESS_ALLOWED);
            }
        } else {
            extraActionIntent.putExtra(EXTRA_ACTION_NAME, ACCESS_DENIED);
            Toast.makeText(context, context.getString(R.string.toast_error_no_access),
                    Toast.LENGTH_LONG).show();
        }

        context.sendBroadcast(extraActionIntent);
        realmDB.close();
    }
}
