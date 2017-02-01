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
import ru.toir.mobile.rest.interfaces.IAlertType;
import ru.toir.mobile.rest.interfaces.IClients;
import ru.toir.mobile.rest.interfaces.ICriticalType;
import ru.toir.mobile.rest.interfaces.IDocumentation;
import ru.toir.mobile.rest.interfaces.IDocumentationType;
import ru.toir.mobile.rest.interfaces.IEquipment;
import ru.toir.mobile.rest.interfaces.IEquipmentModel;
import ru.toir.mobile.rest.interfaces.IEquipmentStatus;
import ru.toir.mobile.rest.interfaces.IEquipmentType;
import ru.toir.mobile.rest.interfaces.IMeasureType;
import ru.toir.mobile.rest.interfaces.IMeasuredValue;
import ru.toir.mobile.rest.interfaces.IOperation;
import ru.toir.mobile.rest.interfaces.IOperationStatus;
import ru.toir.mobile.rest.interfaces.IOperationType;
import ru.toir.mobile.rest.interfaces.IOrders;
import ru.toir.mobile.rest.interfaces.ITaskStatus;
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
    public static IAlertType getAlertTypeService() {
        return getRetrofit().create(IAlertType.class);
    }

    @NonNull
    public static ICriticalType getCriticalTypeService() {
        return getRetrofit().create(ICriticalType.class);
    }

    @NonNull
    public static IClients getClientsService() {
        return getRetrofit().create(IClients.class);
    }

    @NonNull
    public static IDocumentation getDocumentationService() {
        return getRetrofit().create(IDocumentation.class);
    }

    @NonNull
    public static IDocumentationType getDocumentationTypeService() {
        return getRetrofit().create(IDocumentationType.class);
    }

    @NonNull
    public static IEquipment getEquipmentService() {
        return getRetrofit().create(IEquipment.class);
    }

    @NonNull
    public static IEquipmentModel getEquipmentModelService() {
        return getRetrofit().create(IEquipmentModel.class);
    }

    @NonNull
    public static IEquipmentStatus getEquipmentStatusService() {
        return getRetrofit().create(IEquipmentStatus.class);
    }

    @NonNull
    public static IEquipmentType getEquipmentTypeService() {
        return getRetrofit().create(IEquipmentType.class);
    }

    @NonNull
    public static IMeasuredValue getMeasuredValueService() {
        return getRetrofit().create(IMeasuredValue.class);
    }

    @NonNull
    public static IMeasureType getMeasureTypeService() {
        return getRetrofit().create(IMeasureType.class);
    }

    @NonNull
    public static IOperation getOperationService() {
        return getRetrofit().create(IOperation.class);
    }

    @NonNull
    public static IOperationStatus getOperationStatus() {
        return getRetrofit().create(IOperationStatus.class);
    }

    @NonNull
    public static IOperationType getOperationType() {
        return getRetrofit().create(IOperationType.class);
    }

    @NonNull
    public static ITaskStatus getTaskStatus() {
        return getRetrofit().create(ITaskStatus.class);
    }

    @NonNull
    public static IOrders getOrderService() {
        return getRetrofit().create(IOrders.class);
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
