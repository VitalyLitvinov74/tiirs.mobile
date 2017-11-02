package ru.toir.mobile.rest;

import io.realm.Realm;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.BuildConfig;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.deserializer.DateTypeDeserializer;
import ru.toir.mobile.rest.interfaces.IAlertType;
import ru.toir.mobile.rest.interfaces.IContragent;
import ru.toir.mobile.rest.interfaces.ICriticalType;
import ru.toir.mobile.rest.interfaces.IDefect;
import ru.toir.mobile.rest.interfaces.IDefectType;
import ru.toir.mobile.rest.interfaces.IDocumentation;
import ru.toir.mobile.rest.interfaces.IDocumentationType;
import ru.toir.mobile.rest.interfaces.IEquipment;
import ru.toir.mobile.rest.interfaces.IEquipmentModel;
import ru.toir.mobile.rest.interfaces.IEquipmentStatus;
import ru.toir.mobile.rest.interfaces.IEquipmentType;
import ru.toir.mobile.rest.interfaces.IFileDownload;
import ru.toir.mobile.rest.interfaces.IGpsTrack;
import ru.toir.mobile.rest.interfaces.IMeasureType;
import ru.toir.mobile.rest.interfaces.IMeasuredValue;
import ru.toir.mobile.rest.interfaces.IObjectType;
import ru.toir.mobile.rest.interfaces.IObjects;
import ru.toir.mobile.rest.interfaces.IOperation;
import ru.toir.mobile.rest.interfaces.IOperationStatus;
import ru.toir.mobile.rest.interfaces.IOperationTemplate;
import ru.toir.mobile.rest.interfaces.IOperationTool;
import ru.toir.mobile.rest.interfaces.IOperationType;
import ru.toir.mobile.rest.interfaces.IOperationVerdict;
import ru.toir.mobile.rest.interfaces.IOrderLevel;
import ru.toir.mobile.rest.interfaces.IOrderStatus;
import ru.toir.mobile.rest.interfaces.IOrderVerdict;
import ru.toir.mobile.rest.interfaces.IOrders;
import ru.toir.mobile.rest.interfaces.IRepairPart;
import ru.toir.mobile.rest.interfaces.IRepairPartType;
import ru.toir.mobile.rest.interfaces.IJournal;
import ru.toir.mobile.rest.interfaces.ITaskStageList;
import ru.toir.mobile.rest.interfaces.ITaskStageOperationList;
import ru.toir.mobile.rest.interfaces.ITaskStageStatus;
import ru.toir.mobile.rest.interfaces.ITaskStageTemplate;
import ru.toir.mobile.rest.interfaces.ITaskStageType;
import ru.toir.mobile.rest.interfaces.ITaskStageVerdict;
import ru.toir.mobile.rest.interfaces.ITaskStages;
import ru.toir.mobile.rest.interfaces.ITaskStatus;
import ru.toir.mobile.rest.interfaces.ITaskTemplate;
import ru.toir.mobile.rest.interfaces.ITaskType;
import ru.toir.mobile.rest.interfaces.ITaskVerdict;
import ru.toir.mobile.rest.interfaces.ITasks;
import ru.toir.mobile.rest.interfaces.ITokenService;
import ru.toir.mobile.rest.interfaces.ITool;
import ru.toir.mobile.rest.interfaces.IToolType;
import ru.toir.mobile.rest.interfaces.IUserService;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public class
ToirAPIFactory {
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;

    private static final OkHttpClient CLIENT = new OkHttpClient()
            .newBuilder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request origRequest = chain.request();
                    Headers origHeaders = origRequest.headers();
                    AuthorizedUser user = AuthorizedUser.getInstance();
                    Headers newHeaders = origHeaders.newBuilder().add("Authorization", user.getBearer()).build();

                    String login = user.getLogin();
                    if (login == null) {
                        login = "";
                    }

                    HttpUrl url = origRequest.url().newBuilder().addQueryParameter("apiuser", login).build();
                    Request.Builder requestBuilder = origRequest.newBuilder().headers(newHeaders).url(url);
                    Request newRequest = requestBuilder.build();
                    return chain.proceed(newRequest);
                }
            })
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    if (BuildConfig.DEBUG) {
                        Request request = chain.request();
                        HttpUrl url = request.url()
                                .newBuilder()
                                .addQueryParameter("XDEBUG_SESSION_START", "xdebug")
                                .build();
                        Request.Builder requestBuilder = request.newBuilder().url(url);
                        Request newRequest = requestBuilder.build();
                        return chain.proceed(newRequest);
                    } else {
                        return chain.proceed(chain.request());
                    }
                }
            })
            .build();

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
    public static IContragent getContragentService() {
        return getRetrofit().create(IContragent.class);
    }

    @NonNull
    public static IDefect getDefectService() {
        return getRetrofit().create(IDefect.class);
    }

    @NonNull
    public static IDefectType getDefectTypeService() {
        return getRetrofit().create(IDefectType.class);
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
    public static IObjects getObjectService() {
        return getRetrofit().create(IObjects.class);
    }

    @NonNull
    public static IObjectType getObjectTypeService() {
        return getRetrofit().create(IObjectType.class);
    }

    @NonNull
    public static IOperation getOperationService() {
        return getRetrofit().create(IOperation.class);
    }

    @NonNull
    public static IOperationStatus getOperationStatusService() {
        return getRetrofit().create(IOperationStatus.class);
    }

    @NonNull
    public static IOperationTemplate getOperationTemplateService() {
        return getRetrofit().create(IOperationTemplate.class);
    }

    @NonNull
    public static IOperationTool getOperationToolService() {
        return getRetrofit().create(IOperationTool.class);
    }

    @NonNull
    public static IOperationType getOperationTypeService() {
        return getRetrofit().create(IOperationType.class);
    }

    @NonNull
    public static IOperationVerdict getOperationVerdictService() {
        return getRetrofit().create(IOperationVerdict.class);
    }

    @NonNull
    public static IOrderLevel getOrderLevelService() {
        return getRetrofit().create(IOrderLevel.class);
    }

    @NonNull
    public static IOrders getOrdersService() {
        return getRetrofit().create(IOrders.class);
    }

    @NonNull
    public static IOrderStatus getOrderStatusService() {
        return getRetrofit().create(IOrderStatus.class);
    }

    @NonNull
    public static IOrderVerdict getOrderVerdictService() {
        return getRetrofit().create(IOrderVerdict.class);
    }

    @NonNull
    public static IRepairPart getRepairPartService() {
        return getRetrofit().create(IRepairPart.class);
    }

    @NonNull
    public static IRepairPartType getRepairPartTypeService() {
        return getRetrofit().create(IRepairPartType.class);
    }

    @NonNull
    public static ITasks getTasksService() {
        return getRetrofit().create(ITasks.class);
    }

    @NonNull
    public static ITaskStageList getTaskStageListService() {
        return getRetrofit().create(ITaskStageList.class);
    }

    @NonNull
    public static ITaskStageOperationList getTaskStageOperationListService() {
        return getRetrofit().create(ITaskStageOperationList.class);
    }

    @NonNull
    public static ITaskStages getTaskStagesService() {
        return getRetrofit().create(ITaskStages.class);
    }

    @NonNull
    public static ITaskStageStatus getTaskStageStatusService() {
        return getRetrofit().create(ITaskStageStatus.class);
    }

    @NonNull
    public static ITaskStageTemplate getTaskStageTemplateService() {
        return getRetrofit().create(ITaskStageTemplate.class);
    }

    @NonNull
    public static ITaskStageVerdict getTaskStageVerdictService() {
        return getRetrofit().create(ITaskStageVerdict.class);
    }

    @NonNull
    public static ITaskStatus getTaskStatusService() {
        return getRetrofit().create(ITaskStatus.class);
    }

    @NonNull
    public static ITaskStageType getTaskStageTypeService() {
        return getRetrofit().create(ITaskStageType.class);
    }

    @NonNull
    public static ITaskTemplate getTaskTemplateService() {
        return getRetrofit().create(ITaskTemplate.class);
    }

    @NonNull
    public static ITaskType getTaskTypeService() {
        return getRetrofit().create(ITaskType.class);
    }

    @NonNull
    public static ITaskVerdict getTaskVerdictService() {
        return getRetrofit().create(ITaskVerdict.class);
    }

    @NonNull
    public static ITool getToolService() {
        return getRetrofit().create(ITool.class);
    }

    @NonNull
    public static IToolType getToolTypeService() {
        return getRetrofit().create(IToolType.class);
    }

    @NonNull
    public static IJournal getJournalService() {
        return getRetrofit().create(IJournal.class);
    }

    @NonNull
    public static IGpsTrack getGpsTrackService() {
        return getRetrofit().create(IGpsTrack.class);
    }

    @NonNull
    public static IFileDownload getFileDownload() {
        return getRetrofit().create(IFileDownload.class);
    }

    @NonNull
    private static Retrofit getRetrofit() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeDeserializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
        return new Retrofit.Builder()
                .baseUrl(ToirApplication.serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(CLIENT)
                .build();
    }

//    public static final class Actions {
//        public static final String ACTION_GET_TOKEN = "action_get_token";
//        public static final String ACTION_GET_USER = "action_get_user";
//        public static final String ACTION_GET_ALL_REFERENCE = "action_get_all_reference";
//    }

}
