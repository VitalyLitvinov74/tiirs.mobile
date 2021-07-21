package ru.toir.mobile.multi.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.BuildConfig;
import ru.toir.mobile.multi.ToirApplication;
import ru.toir.mobile.multi.deserializer.DateTypeDeserializer;
import ru.toir.mobile.multi.rest.interfaces.IAlertType;
import ru.toir.mobile.multi.rest.interfaces.IAttributeType;
import ru.toir.mobile.multi.rest.interfaces.ICommonFile;
import ru.toir.mobile.multi.rest.interfaces.IContragent;
import ru.toir.mobile.multi.rest.interfaces.ICriticalType;
import ru.toir.mobile.multi.rest.interfaces.IDefect;
import ru.toir.mobile.multi.rest.interfaces.IDefectLevel;
import ru.toir.mobile.multi.rest.interfaces.IDefectType;
import ru.toir.mobile.multi.rest.interfaces.IDocumentation;
import ru.toir.mobile.multi.rest.interfaces.IDocumentationType;
import ru.toir.mobile.multi.rest.interfaces.IEquipment;
import ru.toir.mobile.multi.rest.interfaces.IEquipmentAttribute;
import ru.toir.mobile.multi.rest.interfaces.IEquipmentModel;
import ru.toir.mobile.multi.rest.interfaces.IEquipmentStatus;
import ru.toir.mobile.multi.rest.interfaces.IEquipmentType;
import ru.toir.mobile.multi.rest.interfaces.IFileDownload;
import ru.toir.mobile.multi.rest.interfaces.IGpsTrack;
import ru.toir.mobile.multi.rest.interfaces.IInstruction;
import ru.toir.mobile.multi.rest.interfaces.IInstructionStageTemplate;
import ru.toir.mobile.multi.rest.interfaces.IJournal;
import ru.toir.mobile.multi.rest.interfaces.IMeasureType;
import ru.toir.mobile.multi.rest.interfaces.IMeasuredValue;
import ru.toir.mobile.multi.rest.interfaces.IMediaFile;
import ru.toir.mobile.multi.rest.interfaces.IMessage;
import ru.toir.mobile.multi.rest.interfaces.IObjectType;
import ru.toir.mobile.multi.rest.interfaces.IObjects;
import ru.toir.mobile.multi.rest.interfaces.IOperation;
import ru.toir.mobile.multi.rest.interfaces.IOperationStatus;
import ru.toir.mobile.multi.rest.interfaces.IOperationTemplate;
import ru.toir.mobile.multi.rest.interfaces.IOperationTool;
import ru.toir.mobile.multi.rest.interfaces.IOperationType;
import ru.toir.mobile.multi.rest.interfaces.IOperationVerdict;
import ru.toir.mobile.multi.rest.interfaces.IOrderLevel;
import ru.toir.mobile.multi.rest.interfaces.IOrderStatus;
import ru.toir.mobile.multi.rest.interfaces.IOrderVerdict;
import ru.toir.mobile.multi.rest.interfaces.IOrders;
import ru.toir.mobile.multi.rest.interfaces.IRepairPart;
import ru.toir.mobile.multi.rest.interfaces.IRepairPartType;
import ru.toir.mobile.multi.rest.interfaces.IStage;
import ru.toir.mobile.multi.rest.interfaces.IStageStatus;
import ru.toir.mobile.multi.rest.interfaces.IStageTemplate;
import ru.toir.mobile.multi.rest.interfaces.IStageType;
import ru.toir.mobile.multi.rest.interfaces.IStageVerdict;
import ru.toir.mobile.multi.rest.interfaces.ITaskStatus;
import ru.toir.mobile.multi.rest.interfaces.ITaskTemplate;
import ru.toir.mobile.multi.rest.interfaces.ITaskType;
import ru.toir.mobile.multi.rest.interfaces.ITaskVerdict;
import ru.toir.mobile.multi.rest.interfaces.ITasks;
import ru.toir.mobile.multi.rest.interfaces.ITokenService;
import ru.toir.mobile.multi.rest.interfaces.ITool;
import ru.toir.mobile.multi.rest.interfaces.IToolType;
import ru.toir.mobile.multi.rest.interfaces.IUserService;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public class ToirAPIFactory {
    public static final X509TrustManager tm = getTm();
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;
    public static SSLSocketFactory sslsf = getSslsf();

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
                    Headers newHeaders = origHeaders.newBuilder()
                            .add(
                                    "accessToken",
                                    user.getBearer()
                            ).build();

                    Request.Builder requestBuilder = origRequest.newBuilder().headers(newHeaders);
                    String login = user.getLogin();
                    if (login != null) {
                        HttpUrl url = origRequest.url().newBuilder()
                                .addQueryParameter("apiuser", login).build();
                        requestBuilder.url(url);
                    }

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
            .addInterceptor(new ChuckInterceptor(ToirApplication.context))
            .sslSocketFactory(sslsf, tm)
            .build();

    public static SSLSocketFactory getSslsf() {
        SSLSocketFactory value = null;
        SSLContext context;

        try {
            value = new TLSSocketFactory(new TrustManager[]{tm});
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static ToirAPIFactory.UnifiedTrustManager getTm() {
        KeyStore keyStore = getKeyStore();
        ToirAPIFactory.UnifiedTrustManager trustManager = null;
        try {
            trustManager = new ToirAPIFactory.UnifiedTrustManager(keyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return trustManager;
    }

    public static KeyStore getKeyStore() {
        InputStream inputStream1 = ToirApplication.qwvostokCA;
        InputStream inputStream2 = ToirApplication.sstalRootCA;
        InputStream inputStream3 = ToirApplication.sstalInternalCA;
        InputStream inputStream4 = ToirApplication.sstalDigicert;
        InputStream inputStream5 = ToirApplication.sstalDigicertRoot;
        InputStream inputStream6 = ToirApplication.digicertsha2CA;
        KeyStore keyStore = null;
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            inputStream1.reset();
            InputStream caInput1 = new BufferedInputStream(inputStream1);
            inputStream2.reset();
            InputStream caInput2 = new BufferedInputStream(inputStream2);
            inputStream3.reset();
            InputStream caInput3 = new BufferedInputStream(inputStream3);
            inputStream4.reset();
            InputStream caInput4 = new BufferedInputStream(inputStream4);
            inputStream5.reset();
            InputStream caInput5 = new BufferedInputStream(inputStream5);
            inputStream6.reset();
            InputStream caInput6 = new BufferedInputStream(inputStream6);
            Certificate ca1, ca2, ca3, ca4, ca5, ca6;
            try {
                ca1 = cf.generateCertificate(caInput1);
                ca2 = cf.generateCertificate(caInput2);
                ca3 = cf.generateCertificate(caInput3);
                ca4 = cf.generateCertificate(caInput4);
                ca5 = cf.generateCertificate(caInput5);
                ca6 = cf.generateCertificate(caInput6);
            } finally {
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca1", ca1);
            keyStore.setCertificateEntry("ca2", ca2);
            keyStore.setCertificateEntry("ca3", ca3);
            keyStore.setCertificateEntry("ca4", ca4);
            keyStore.setCertificateEntry("ca5", ca5);
            keyStore.setCertificateEntry("ca6", ca6);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return keyStore;
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
    public static IDefectLevel getDefectLevelService() {
        return getRetrofit().create(IDefectLevel.class);
    }

    @NonNull
    public static IDocumentation getDocumentationService() {
        return getRetrofit().create(IDocumentation.class);
    }

    @NonNull
    public static ICommonFile getCommonFileService() {
        return getRetrofit().create(ICommonFile.class);
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
    public static IMediaFile getMediaFileService() {
        return getRetrofit().create(IMediaFile.class);
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
    public static IStage getStageService() {
        return getRetrofit().create(IStage.class);
    }

    @NonNull
    public static IStageStatus getStageStatusService() {
        return getRetrofit().create(IStageStatus.class);
    }

    @NonNull
    public static IInstructionStageTemplate getInstructionStageTemplate() {
        return getRetrofit().create(IInstructionStageTemplate.class);
    }

    @NonNull
    public static IInstruction getInstructionService() {
        return getRetrofit().create(IInstruction.class);
    }

    @NonNull
    public static IStageTemplate getStageTemplateService() {
        return getRetrofit().create(IStageTemplate.class);
    }

    @NonNull
    public static IStageVerdict getStageVerdictService() {
        return getRetrofit().create(IStageVerdict.class);
    }

    @NonNull
    public static ITaskStatus getTaskStatusService() {
        return getRetrofit().create(ITaskStatus.class);
    }

    @NonNull
    public static IStageType getStageTypeService() {
        return getRetrofit().create(IStageType.class);
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
    public static IMessage getMessageService() {
        return getRetrofit().create(IMessage.class);
    }

    @NonNull
    public static IAttributeType getAttributeTypeService() {
        return getRetrofit().create(IAttributeType.class);
    }

    @NonNull
    public static IEquipmentAttribute getEquipmentAttributeService() {
        return getRetrofit().create(IEquipmentAttribute.class);
    }

    @NonNull
    public static IFileDownload getFileDownload() {
        return getRetrofit().create(IFileDownload.class);
    }

    /**
     * Метод без указания специфической обработки элементов json.
     *
     * @return Retrofit
     */
    @NonNull
    private static Retrofit getRetrofit() {
        return getRetrofit(null);
    }

    /**
     * Метод для задания специфической обработки элементов json.
     *
     * @param list Список типов и десереализаторов.
     * @return Retrofit
     */
    @NonNull
    private static Retrofit getRetrofit(List<TypeAdapterParam> list) {
        GsonBuilder builder = new GsonBuilder();

        if (list != null) {
            for (TypeAdapterParam param : list) {
                builder.registerTypeAdapter(param.getTypeClass(), param.getDeserializer());
            }
        }

        builder.registerTypeAdapter(Date.class, new DateTypeDeserializer());
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        builder.serializeNulls();
        Gson gson = builder.create();
        return new Retrofit.Builder()
                .baseUrl(ToirApplication.serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(CLIENT)
                .build();
    }

//        public static final class Actions {
//        public static final String ACTION_GET_TOKEN = "action_get_token";
//        public static final String ACTION_GET_USER = "action_get_user";
//        public static final String ACTION_GET_ALL_REFERENCE = "action_get_all_reference";
//    }

    /**
     * Класс для хранения типа и десереализатора к этому типу.
     */
    private static class TypeAdapterParam {
        Class<?> typeClass;
        JsonDeserializer<?> deserializer;

        TypeAdapterParam(Class<?> c, JsonDeserializer<?> d) {
            typeClass = c;
            deserializer = d;
        }

        public Class<?> getTypeClass() {
            return typeClass;
        }

        public void setTypeClass(Class<?> typeClass) {
            this.typeClass = typeClass;
        }

        public JsonDeserializer<?> getDeserializer() {
            return deserializer;
        }

        public void setDeserializer(JsonDeserializer<?> deserializer) {
            this.deserializer = deserializer;
        }
    }

    public static class UnifiedTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public UnifiedTrustManager(KeyStore localKeyStore) throws KeyStoreException {
            try {
                this.defaultTrustManager = createTrustManager(null);
                this.localTrustManager = createTrustManager(localKeyStore);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        private X509TrustManager createTrustManager(KeyStore store) throws NoSuchAlgorithmException, KeyStoreException {
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(store);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            return (X509TrustManager) trustManagers[0];
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkClientTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkClientTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] first = defaultTrustManager.getAcceptedIssuers();
            X509Certificate[] second = localTrustManager.getAcceptedIssuers();
            X509Certificate[] result = Arrays.copyOf(first, first.length + second.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }
    }
}
