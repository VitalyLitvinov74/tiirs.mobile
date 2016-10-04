package ru.toir.mobile.rest;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.deserializer.DateTypeDeserializer;
import ru.toir.mobile.rest.interfaces.ICriticalType;
import ru.toir.mobile.rest.interfaces.ITokenService;
import ru.toir.mobile.rest.interfaces.IUserService;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public class ToirAPIFactory {
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;

    private static final OkHttpClient CLIENT = new OkHttpClient();

    static {
        CLIENT.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        CLIENT.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        CLIENT.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @NonNull
    public static ITokenService getTokenService() {
        return getRetrofit().create(ITokenService.class);
    }

    @NonNull
    public static IUserService getUserService() {
        return getRetrofit().create(IUserService.class);
    }

    @NonNull
    public static ICriticalType getCriticalTypeService() {
        return getRetrofit().create(ICriticalType.class);
    }

    @NonNull
    private static Retrofit getRetrofit() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeDeserializer())
                .create();
        return new Retrofit.Builder()
                .baseUrl(ToirApplication.serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(CLIENT)
                .build();
    }

    public static final class Actions {
        public static final String ACTION_GET_TOKEN = "action_get_token";
        public static final String ACTION_GET_USER = "action_get_user";
        public static final String ACTION_GET_ALL_REFERENCE = "action_get_all_reference";
    }

}
