package ru.toir.mobile.multi.serverapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.ToirApplication;
import ru.toir.mobile.multi.db.ToirRealm;
import ru.toir.mobile.multi.db.realm.User;
import ru.toir.mobile.multi.rest.ToirAPIFactory;
import ru.toir.mobile.multi.rfid.RfidDriverMsg;
import ru.toir.mobile.multi.utils.AuthLocal;

public class GetToken extends BroadcastReceiver {

    public static final String GET_TOKEN_ACTION = ToirApplication.packageName + ".getToken";

    @Override
    public void onReceive(Context context, Intent intent) {
        AuthorizedUser authUser = AuthorizedUser.getInstance();

        // запрашиваем токен
        Call<TokenSrv> call;
        if (authUser.loginType() == RfidDriverMsg.TYPE_TAG) {
            call = ToirAPIFactory.getTokenService().getByLabel(authUser.getIdentity(),
                    TokenSrv.Type.LABEL);
        } else {
            call = ToirAPIFactory.getTokenService()
                    .getByPassword(authUser.getIdentity(), authUser.getPassword(),
                            TokenSrv.Type.PASSWORD);
        }

        call.enqueue(new Callback<TokenSrv>() {
            @Override
            public void onResponse(Call<TokenSrv> tokenSrvCall, Response<TokenSrv> response) {
                int code = response.code();
                if (code != 200) {
                    // токен не получили, аутентификация не прошла
                    if (!authUser.isLocalLogged()) {
                        // пытаемся аутентифицировать в локальной базе
                        Intent intent = new Intent(AuthLocal.AUTH_LOCAL_ACTION);
                        context.sendBroadcast(intent);
                    }

                    String message = response.message() != null && !response.message().isEmpty()
                            ? response.message() : context.getString(R.string.auth_error);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                } else {
                    // ответ от сервера получен
                    TokenSrv token = response.body();
                    if (token == null) {
                        // токен не получили, аутентификация не прошла
                        if (!authUser.isLocalLogged()) {
                            // пытаемся аутентифицировать в локальной базе
                            Intent intent = new Intent(AuthLocal.AUTH_LOCAL_ACTION);
                            context.sendBroadcast(intent);
                        }
                    } else {
                        // токен получили
                        authUser.setToken(token.getAccessToken());
                        authUser.setDbName(token.getDb());
                        authUser.setLogin(token.getLogin());
                        authUser.setServerLogged(true);
                        authUser.setLocalLogged(true);

                        // создаём локальную связь пользователя с базой
                        HashMap<String, String> usersDbLinks = User.getUsersDbLinks(context);
                        usersDbLinks.put(authUser.getIdentity(), token.getDb());
                        User.saveUsersDbLinks(context, usersDbLinks);

                        // инициализируем базу пользователя
                        if (!authUser.isLocalLogged()) {
                            ToirRealm.initDb(context, token.getDb());
                        }

                        Toast.makeText(context, context.getString(R.string.toast_token_received),
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(GetUser.GET_USER_ACTION);
                        context.sendBroadcast(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenSrv> tokenSrvCall, Throwable t) {
                // TODO нужен какой-то механизм уведомления о причине не успеха
                // String message = bundle.getString(IServiceProvider.MESSAGE);
                String message = context.getString(R.string.toast_error_no_token_received);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                // TODO реализовать проверку на то что пользователя нет на сервере
                // токен не получен, сервер не ответил...

                if (!authUser.isLocalLogged()) {
                    Intent nextActionIntent = new Intent(AuthLocal.AUTH_LOCAL_ACTION);
                    context.sendBroadcast(nextActionIntent);
                }
            }
        });

    }
}
