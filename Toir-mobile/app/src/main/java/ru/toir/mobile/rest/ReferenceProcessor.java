/**
 *
 */
package ru.toir.mobile.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.Realm;
import retrofit.Call;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.EquipmentType;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationType;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.db.tables.OperationPatternStep;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serverapi.EquipmentDocumentationSrv;
import ru.toir.mobile.serverapi.DocumentationTypeSrv;
import ru.toir.mobile.serverapi.EquipmentSrv;
import ru.toir.mobile.serverapi.OperationPatternSrv;
import ru.toir.mobile.serverapi.OperationPatternStepSrv;
import ru.toir.mobile.serverapi.OperationResultSrv;
import ru.toir.mobile.serverapi.ReferenceListSrv;
import ru.toir.mobile.serverapi.TokenSrv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 */
public class ReferenceProcessor {

    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private Context mContext;

    /**
     * @param context context
     */
    public ReferenceProcessor(Context context) throws Exception {

        mContext = context;

        if (ToirApplication.serverUrl.equals("")) {
            throw new Exception("URL сервера не указан!");
        }
    }

    // TODO нужно изменить условие на сервере с >= на > для lastChangedAt
    // внятного ответа не получено, оставляем костыль в виде +1 секунды
    // это черевато тем, что если на сервере при создании записи дата изменения
    // не будет равна дате создания, новые данные не получим, до тех пор пока
    // запись не будет изменена

    /**
     * Делает запрос по переданному url и возвращает строку данных
     *
     * @param url url
     * @return String
     */
    private String getReferenceData(String url) {

        try {
            URI requestUri = new URI(url);
            Log.d("test", "requestUri = " + requestUri.toString());

            Map<String, List<String>> headers = new ArrayMap<>();
            List<String> tList = new ArrayList<>();
            tList.add("bearer " + AuthorizedUser.getInstance().getToken());
            headers.put("Authorization", tList);

            Request request = new Request(Method.GET, requestUri, headers, null);
            Response response = new RestClient().execute(request);

            if (response.mStatus == 200) {
                String jsonString = new String(response.mBody);
                Log.d("test", jsonString);
                return jsonString;
            } else {
                throw new Exception(
                        "Не удалось получить данные справочника. URL: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Получаем шаблон выполнения операции с шагами и вариантами выполнения
     * шагов
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getOperationPattern(Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        StringBuilder url = new StringBuilder();
        String jsonString;
        ArrayList<String> patternUuids = bundle.getStringArrayList(
                ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID);
        boolean inParrentTransaction;

        SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
                .getWritableDatabase();
        inParrentTransaction = db.inTransaction();

        // если транзакция не открыта раньше, открываем её
        if (!inParrentTransaction) {
            db.beginTransaction();
        }

        if (patternUuids == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        for (String uuid : patternUuids) {
            url.setLength(0);
            url.append(ToirApplication.serverUrl).append("/api/operationpatterns/")
                    .append(uuid);
            jsonString = getReferenceData(url.toString());

            if (jsonString != null) {
                Gson gson = new GsonBuilder().setDateFormat(dateFormat)
                        .create();

                // разбираем и сохраняем полученные данные
                result = savePattern(gson.fromJson(jsonString,
                        OperationPatternSrv.class));
                boolean success = result.getBoolean(IServiceProvider.RESULT);
                if (!success) {
                    if (!inParrentTransaction) {
                        db.endTransaction();
                    }
                    return result;
                }
            } else {
                if (!inParrentTransaction) {
                    db.endTransaction();
                }
                result = new Bundle();
                result.putBoolean(IServiceProvider.RESULT, false);
                return result;
            }
        }

        // получаем изображения к шагам шаблона операции
        OperationPatternStepDBAdapter stepDBAdapter = new OperationPatternStepDBAdapter(
                new ToirDatabaseContext(mContext));
        for (String patternUuid : patternUuids) {
            ArrayList<OperationPatternStep> steps = stepDBAdapter
                    .getItems(patternUuid);

            for (OperationPatternStep step : steps) {
                if (step.getImage() == null) {
                    continue;
                }
                url.setLength(0);
                url.append(ToirApplication.serverUrl).append("/api/operationpatterns/")
                        .append(patternUuid).append("/steps/")
                        .append(step.getUuid()).append("/images/")
                        .append(step.getUuid()).append(".jpg");

                try {
                    URI requestUri = new URI(url.toString());
                    Log.d("test", "requestUri = " + requestUri.toString());

                    Map<String, List<String>> headers = new ArrayMap<>();
                    List<String> tList = new ArrayList<>();
                    tList.add("bearer "
                            + AuthorizedUser.getInstance().getToken());
                    headers.put("Authorization", tList);

                    Request request = new Request(Method.GET, requestUri,
                            headers, null);
                    Response response = new RestClient().execute(request);

                    if (response.mStatus == 200) {
                        File file = new File(
                                mContext.getExternalFilesDir("patterns") + "/"
                                        + patternUuid, step.getUuid() + ".jpg");
                        if (!file.getParentFile().exists()) {
                            if (file.getParentFile().mkdirs()) {
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(response.mBody);
                                fos.close();
                                step.setImage(file.getPath());
                                stepDBAdapter.replace(step);
                            }
                        }
                    } else {
                        throw new Exception("Не удалось получить файл. URL: "
                                + url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result = new Bundle();
                    result.putBoolean(IServiceProvider.RESULT, false);
                    result.putString(IServiceProvider.MESSAGE, e.getMessage());
                    return result;
                }
            }
        }

        if (!inParrentTransaction) {
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        result = new Bundle();
        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * Получаем возможные результаты выполнения операции
     *
     * @param bundle bundle
     * @return bundle
     */
    public Bundle getOperationResult(Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        String[] operationTypeUuids = bundle.getStringArray(
                ReferenceServiceProvider.Methods.GET_OPERATION_RESULT_PARAMETER_UUID);
        StringBuilder url = new StringBuilder();
        String jsonString;
        boolean inParentTransaction;

        SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
                .getWritableDatabase();
        inParentTransaction = db.inTransaction();

        String referenceUrl = getReferenceURL(ReferenceName.OperationResult);
        if (referenceUrl == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();

        if (!inParentTransaction) {
            db.beginTransaction();
        }

        if (operationTypeUuids == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        for (String typeUuid : operationTypeUuids) {
            url.setLength(0);
            url.append(ToirApplication.serverUrl).append('/').append(referenceUrl).append('?')
                    .append("OperationTypeId=").append(typeUuid);
            jsonString = getReferenceData(url.toString());
            if (jsonString != null) {
                // разбираем и сохраняем полученные данные
                ArrayList<OperationResultSrv> results = gson.fromJson(
                        jsonString,
                        new TypeToken<ArrayList<OperationResultSrv>>() {
                            @SuppressWarnings("unused")
                            private static final long serialVersionUID = 1;
                        }.getType());
                result = saveOperationResult(results);
                boolean success = result.getBoolean(IServiceProvider.RESULT);
                if (!success) {
                    if (!inParentTransaction) {
                        db.endTransaction();
                    }
                    return result;
                }
            } else {
                if (!inParentTransaction) {
                    db.endTransaction();
                }
                result = new Bundle();
                result.putBoolean(IServiceProvider.RESULT, false);
                return result;
            }
        }

        if (!inParentTransaction) {
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        result = new Bundle();
        result.putBoolean(IServiceProvider.RESULT, true);
        return result;

    }

    /**
     * Получаем типы документов
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getDocumentationType(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        Realm realm = Realm.getDefaultInstance();

        // TODO: реализовать механизм хранения даты последней модификации в таблице
        // TODO: реализовать выборку даты последней модификации и отправки её на сервер
        Date changed = realm.where(DocumentationType.class).findFirst().getChangedAt();
        String lastChangedAt = new SimpleDateFormat(dateFormat, Locale.US).format(changed);
        Call<List<DocumentationType>> call = ToirAPIFactory.getDocumentationTypeService()
                .documentationType("bearer " + AuthorizedUser.getInstance().getToken(),
                        lastChangedAt);
        try {
            retrofit.Response<List<DocumentationType>> response = call.execute();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем файл документации
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getDocumentationFile(Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        StringBuilder url = new StringBuilder();
        String fileUuids[] = bundle.getStringArray(
                ReferenceServiceProvider.Methods.GET_DOCUMENTATION_FILE_PARAMETER_UUID);
        EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(
                new ToirDatabaseContext(mContext));
        EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
                new ToirDatabaseContext(mContext));

        if (fileUuids == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        for (String fileUuid : fileUuids) {
            EquipmentDocumentation document = documentationDBAdapter
                    .getItem(fileUuid);
            Equipment equipment = equipmentDBAdapter.getItem(document
                    .getEquipment_uuid());

            url.setLength(0);
            url.append(ToirApplication.serverUrl).append("/api/Equipment/")
                    .append(equipment.getUuid()).append("/Documents/")
                    .append(document.getUuid()).append("/file");

            try {
                URI requestUri = new URI(url.toString());
                Log.d("test", "requestUri = " + requestUri.toString());

                Map<String, List<String>> headers = new ArrayMap<>();
                List<String> tList = new ArrayList<>();
                tList.add("bearer " + AuthorizedUser.getInstance().getToken());
                headers.put("Authorization", tList);

                Request request = new Request(Method.GET, requestUri, headers,
                        null);
                Response response = new RestClient().execute(request);

                if (response.mStatus == 200) {
                    File file = new File(
                            mContext.getExternalFilesDir("documentation") + "/"
                                    + equipment.getUuid(), document.getPath());
                    if (!file.getParentFile().exists()) {
                        if (file.getParentFile().mkdirs()) {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(response.mBody);
                            fos.close();
                            document.setPath(file.getPath());
                            documentationDBAdapter.replace(document);
                        }
                    }
                } else {
                    throw new Exception("Не удалось получить файл. URL: " + url);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = new Bundle();
                result.putBoolean(IServiceProvider.RESULT, false);
                result.putString(IServiceProvider.MESSAGE, e.getMessage());
                return result;
            }
        }

        result = new Bundle();
        result.putBoolean(IServiceProvider.RESULT, true);
        result.putStringArray(
                ReferenceServiceProvider.Methods.RESULT_GET_DOCUMENTATION_FILE_UUID,
                fileUuids);
        return result;
    }

    /**
     * Получаем файл изображения оборудования
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getEquipmentFile(Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        StringBuilder url = new StringBuilder();
        String equipmentsUuids[] = bundle.getStringArray(
                ReferenceServiceProvider.Methods.GET_IMAGE_FILE_PARAMETER_UUID);

        EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
                new ToirDatabaseContext(mContext));

        if (equipmentsUuids == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        for (String equipmentsUuid : equipmentsUuids) {
            Equipment equipment = equipmentDBAdapter.getItem(equipmentsUuid);

            File imgFile = new File(equipment.getImage());
            String fileName = imgFile.getName();
            String fileNameEncoded;
            String filePath = imgFile.getParent();

            String charset = "UTF-8";
            if (Charset.isSupported(charset)) {
                try {
                    fileNameEncoded = URLEncoder.encode(fileName, charset)
                            .replace("+", "%20");
                } catch (Exception e) {
                    e.printStackTrace();
                    fileNameEncoded = fileName;
                }
            } else {
                fileNameEncoded = fileName;
            }

            url.setLength(0);
            url.append(ToirApplication.serverUrl).append("/").append(filePath).append("/")
                    .append(fileNameEncoded);

            try {
                URI requestUri = new URI(url.toString());
                Log.d("test", "requestUri = " + requestUri.toString());

                Map<String, List<String>> headers = new ArrayMap<>();
                List<String> tList = new ArrayList<>();
                tList.add("bearer " + AuthorizedUser.getInstance().getToken());
                headers.put("Authorization", tList);

                Request request = new Request(Method.GET, requestUri, headers,
                        null);
                Response response = new RestClient().execute(request);

                if (response.mStatus == 200) {
                    File file = new File(
                            mContext.getExternalFilesDir("documentation") + "/"
                                    + equipment.getUuid(), fileName);
                    if (!file.getParentFile().exists()) {
                        if (file.getParentFile().mkdirs()) {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(response.mBody);
                            fos.close();
                            equipment.setImage(file.getPath());
                            equipmentDBAdapter.replace(equipment);
                        }
                    }
                } else {
                    throw new Exception("Не удалось получить файл. URL: " + url);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = new Bundle();
                result.putBoolean(IServiceProvider.RESULT, false);
                result.putString(IServiceProvider.MESSAGE, e.getMessage());
                return result;
            }
        }

        result = new Bundle();
        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * Получаем файл изображения результата измерения
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getMeasureValueFile(Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        StringBuilder url = new StringBuilder();
        String measureValueUuids[] = bundle.getStringArray(
                ReferenceServiceProvider.Methods.GET_IMAGE_FILE_PARAMETER_UUID);

        MeasureValueDBAdapter measureValueDBAdapter = new MeasureValueDBAdapter(
                new ToirDatabaseContext(mContext));

        if (measureValueUuids == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        for (String measureValueUuid : measureValueUuids) {
            MeasureValue measureValue = measureValueDBAdapter
                    .getItem(measureValueUuid);

            File imgFile = new File(measureValue.getValue());
            String fileName = imgFile.getName();
            String fileNameEncoded;
            String filePath = imgFile.getParent();

            String charset = "UTF-8";
            if (Charset.isSupported(charset)) {
                try {
                    fileNameEncoded = URLEncoder.encode(fileName, charset)
                            .replace("+", "%20");
                } catch (Exception e) {
                    e.printStackTrace();
                    fileNameEncoded = fileName;
                }
            } else {
                fileNameEncoded = fileName;
            }

            url.setLength(0);
            url.append(ToirApplication.serverUrl).append("/").append(filePath).append("/")
                    .append(fileNameEncoded);

            try {
                URI requestUri = new URI(url.toString());
                Log.d("test", "requestUri = " + requestUri.toString());

                Map<String, List<String>> headers = new ArrayMap<>();
                List<String> tList = new ArrayList<>();
                tList.add("bearer " + AuthorizedUser.getInstance().getToken());
                headers.put("Authorization", tList);

                Request request = new Request(Method.GET, requestUri, headers,
                        null);
                Response response = new RestClient().execute(request);

                if (response.mStatus == 200) {
                    String extPath;
                    File extFile = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    if (extFile != null) {
                        extPath = extFile.getAbsolutePath();
                    } else {
                        result = new Bundle();
                        result.putBoolean(IServiceProvider.RESULT, false);
                        return result;
                    }

                    File file = new File(extPath, fileName);
                    if (!file.getParentFile().exists()) {
                        if (file.getParentFile().mkdirs()) {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(response.mBody);
                            fos.close();
                            measureValue.setValue(file.getPath());
                            measureValueDBAdapter.replace(measureValue);
                        }
                    }

                } else {
                    throw new Exception("Не удалось получить файл. URL: " + url);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = new Bundle();
                result.putBoolean(IServiceProvider.RESULT, false);
                result.putString(IServiceProvider.MESSAGE, e.getMessage());
                return result;
            }
        }

        result = new Bundle();
        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * Получаем статусы оборудования
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getEquipmentStatus(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        Realm realm = Realm.getDefaultInstance();

        // TODO: реализовать механизм хранения даты последней модификации в таблице
        // TODO: реализовать выборку даты последней модификации и отправки её на сервер
        Date changed = realm.where(EquipmentStatus.class).findFirst().getChangedAt();
        String lastChangedAt = new SimpleDateFormat(dateFormat, Locale.US).format(changed);
        Call<List<EquipmentStatus>> call = ToirAPIFactory.getEquipmentStatusService()
                .equipmentStatus("bearer " + AuthorizedUser.getInstance().getToken(),
                        lastChangedAt);
        try {
            retrofit.Response<List<EquipmentStatus>> response = call.execute();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем типы оборудования
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getEquipmentType(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        Realm realm = Realm.getDefaultInstance();

        // TODO: реализовать механизм хранения даты последней модификации в таблице
        // TODO: реализовать выборку даты последней модификации и отправки её на сервер
        Date changed = realm.where(EquipmentType.class).findFirst().getChangedAt();
        String lastChangedAt = new SimpleDateFormat(dateFormat, Locale.US).format(changed);
        Call<List<EquipmentType>> call = ToirAPIFactory.getEquipmentTypeService()
                .equipmentType("bearer " + AuthorizedUser.getInstance().getToken(), lastChangedAt);
        try {
            retrofit.Response<List<EquipmentType>> response = call.execute();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем типы измерений
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getMeasureType(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        Realm realm = Realm.getDefaultInstance();

        // TODO: реализовать механизм хранения даты последней модификации в таблице
        // TODO: реализовать выборку даты последней модификации и отправки её на сервер
        Date changed = realm.where(MeasureType.class).findFirst().getChangedAt();
        String lastChangedAt = new SimpleDateFormat(dateFormat, Locale.US).format(changed);
        Call<List<MeasureType>> call = ToirAPIFactory.getMeasureTypeService()
                .measureType("bearer " + AuthorizedUser.getInstance().getToken(), lastChangedAt);
        try {
            retrofit.Response<List<MeasureType>> response = call.execute();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем статусы операций
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getOperationStatus(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        Realm realm = Realm.getDefaultInstance();

        // TODO: реализовать механизм хранения даты последней модификации в таблице
        // TODO: реализовать выборку даты последней модификации и отправки её на сервер
        Date changed = realm.where(OperationStatus.class).findFirst().getChangedAt();
        String lastChangedAt = new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(changed);
        Call<List<OperationStatus>> call = ToirAPIFactory.getOperationStatus()
                .operationStatus("bearer " + AuthorizedUser.getInstance().getToken(),
                        lastChangedAt);
        try {
            retrofit.Response<List<OperationStatus>> response = call.execute();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }
    /**
     * Получаем типы операций
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getOperationType(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        Realm realm = Realm.getDefaultInstance();

        // TODO: реализовать механизм хранения даты последней модификации в таблице
        // TODO: реализовать выборку даты последней модификации и отправки её на сервер
        Date changed = realm.where(OperationType.class).findFirst().getChangedAt();
        String lastChangedAt = new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(changed);
        Call<List<OperationType>> call = ToirAPIFactory.getOperationType()
                .operationType("bearer " + AuthorizedUser.getInstance().getToken(), lastChangedAt);
        try {
            retrofit.Response<List<OperationType>> response = call.execute();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем статусы нарядов
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getTaskStatus(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        Realm realm = Realm.getDefaultInstance();

        // TODO: реализовать механизм хранения даты последней модификации в таблице
        // TODO: реализовать выборку даты последней модификации и отправки её на сервер
        Date changed = realm.where(TaskStatus.class).findFirst().getChangedAt();
        String lastChangedAt = new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(changed);
        Call<List<TaskStatus>> call = ToirAPIFactory.getTaskStatus()
                .taskStatus("bearer " + AuthorizedUser.getInstance().getToken(), lastChangedAt);
        try {
            retrofit.Response<List<TaskStatus>> response = call.execute();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем оборудование
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getEquipment(Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        StringBuilder url = new StringBuilder();
        String jsonString;

        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();

        String[] equipmentUuids = bundle
                .getStringArray(ReferenceServiceProvider.Methods.GET_EQUIPMENT_PARAMETER_UUID);

        SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
                .getWritableDatabase();
        db.beginTransaction();

        if (equipmentUuids == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        for (String equipmentUuid : equipmentUuids) {
            url.setLength(0);
            url.append(ToirApplication.serverUrl).append("/api/equipment/")
                    .append(equipmentUuid);

            jsonString = getReferenceData(url.toString());
            if (jsonString != null) {
                // разбираем и сохраняем полученные данные
                EquipmentSrv equipment = gson.fromJson(jsonString,
                        new TypeToken<EquipmentSrv>() {
                            @SuppressWarnings("unused")
                            private static final long serialVersionUID = 1;
                        }.getType());

                result = saveEquipment(equipment);
                boolean success = result.getBoolean(IServiceProvider.RESULT);
                if (!success) {
                    db.endTransaction();
                    return result;
                }
            } else {
                db.endTransaction();
                result = new Bundle();
                result.putBoolean(IServiceProvider.RESULT, false);
                result.putString(IServiceProvider.MESSAGE,
                        "Ошибка получения данных справочника.");
                return result;
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        result = new Bundle();
        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * Получаем типы критичности оборудования
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getCriticalType(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        Realm realm = Realm.getDefaultInstance();

        // TODO: реализовать механизм хранения даты последней модификации в таблице
        // TODO: реализовать выборку даты последней модификации и отправки её на сервер
        Date changed = realm.where(CriticalType.class).findFirst().getChangedAt();
        String lastChangedAt = new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(changed);
        Call<List<CriticalType>> call = ToirAPIFactory.getCriticalTypeService()
                .criticalType("bearer " + AuthorizedUser.getInstance().getToken(), lastChangedAt);
        try {
            retrofit.Response<List<CriticalType>> response = call.execute();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем документацию
     *
     * @param bundle Bundle
     * @return Bundle
     */
    public Bundle getDocumentation(Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        String[] equipmentUuids = bundle.getStringArray(
                ReferenceServiceProvider.Methods.GET_DOCUMENTATION_PARAMETER_UUID);
        StringBuilder url = new StringBuilder();
        String jsonString;

        SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
                .getWritableDatabase();
        db.beginTransaction();

        if (equipmentUuids == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }
        for (String equipmentUuid : equipmentUuids) {
            url.setLength(0);
            url.append(ToirApplication.serverUrl)
                    .append(String.format("/api/equipment/%s/documents",
                            equipmentUuid));
            jsonString = getReferenceData(url.toString());
            if (jsonString != null) {
                Gson gson = new GsonBuilder().setDateFormat(dateFormat)
                        .create();
                // разбираем и сохраняем полученные данные
                ArrayList<EquipmentDocumentationSrv> list = gson.fromJson(
                        jsonString,
                        new TypeToken<ArrayList<EquipmentDocumentationSrv>>() {
                            @SuppressWarnings("unused")
                            private static final long serialVersionUID = 1;
                        }.getType());
                result = saveDocumentations(list, equipmentUuid);
                boolean success = result.getBoolean(IServiceProvider.RESULT);
                if (!success) {
                    db.endTransaction();
                    return result;
                }
            } else {
                db.endTransaction();
                result = new Bundle();
                result.putBoolean(IServiceProvider.RESULT, false);
                result.putString(IServiceProvider.MESSAGE,
                        "Ошибка получения данных справочника.");
                return result;
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        result = new Bundle();
        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * Получаем все справочники
     *
     * @param bundle Bundle
     * @return Bundle
     */
    public Bundle getAll(Bundle bundle) {

        Bundle result;
        boolean success;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        // TODO определиться как всё-таки будут обновляться справочники
        // на каждом устройстве будет копия всех данных с сервера?
        // совершенно не нужно тащить все объекты оборудования на каждое
        // устройство.
        // обновлять будем только те данные которые есть на устройстве?
        // можно пропустить новые данные.
        result = getCriticalType(bundle);
        success = result.getBoolean(IServiceProvider.RESULT);
        if (!success) {
            return result;
        }

        result = getDocumentationType(bundle);
        success = result.getBoolean(IServiceProvider.RESULT);
        if (!success) {
            return result;
        }

        result = getEquipmentStatus(bundle);
        success = result.getBoolean(IServiceProvider.RESULT);
        if (!success) {
            return result;
        }

        result = getEquipmentType(bundle);
        success = result.getBoolean(IServiceProvider.RESULT);
        if (!success) {
            return result;
        }

        result = getMeasureType(bundle);
        success = result.getBoolean(IServiceProvider.RESULT);
        if (!success) {
            return result;
        }

        EquipmentOperationDBAdapter operationAdapter = new EquipmentOperationDBAdapter(
                new ToirDatabaseContext(mContext));
        ArrayList<EquipmentOperation> operations = operationAdapter
                .getItems(null);
        if (operations != null) {
            Set<String> typeUuids = new HashSet<>();
            for (EquipmentOperation operation : operations) {
                typeUuids.add(operation.getOperation_type_uuid());
            }
            bundle.putStringArray(
                    ReferenceServiceProvider.Methods.GET_OPERATION_RESULT_PARAMETER_UUID,
                    typeUuids.toArray(new String[]{""}));
            result = getOperationResult(bundle);
            success = result.getBoolean(IServiceProvider.RESULT);
            if (!success) {
                return result;
            }
        }

        result = getOperationStatus(bundle);
        success = result.getBoolean(IServiceProvider.RESULT);
        if (!success) {
            return result;
        }

        result = getOperationType(bundle);
        success = result.getBoolean(IServiceProvider.RESULT);
        if (!success) {
            return result;
        }

        result = getTaskStatus(bundle);
        success = result.getBoolean(IServiceProvider.RESULT);
        if (!success) {
            return result;
        }

        EquipmentDBAdapter equipmentAdapter = new EquipmentDBAdapter(
                new ToirDatabaseContext(mContext));
        ArrayList<Equipment> equipments = equipmentAdapter.getAllItems("", "");
        if (equipments != null) {
            Set<String> uuids = new HashSet<>();
            for (Equipment equipment : equipments) {
                uuids.add(equipment.getUuid());
            }

            bundle.putStringArray(
                    ReferenceServiceProvider.Methods.GET_DOCUMENTATION_PARAMETER_UUID,
                    uuids.toArray(new String[]{""}));
            result = getDocumentation(bundle);
            success = result.getBoolean(IServiceProvider.RESULT);
            if (!success) {
                return result;
            }

            bundle.clear();
            bundle.putStringArray(
                    ReferenceServiceProvider.Methods.GET_EQUIPMENT_PARAMETER_UUID,
                    uuids.toArray(new String[]{""}));
            result = getEquipment(bundle);
            success = result.getBoolean(IServiceProvider.RESULT);
            if (!success) {
                return result;
            }
        }

        result = new Bundle();
        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * @param referenceName referenceName
     * @return String
     */
    private String getReferenceURL(String referenceName) {

        String referenceUrl = null;
        String url = ToirApplication.serverUrl.concat("/api/references");
        String jsonString;

        jsonString = getReferenceData(url);
        if (jsonString != null) {
            Gson gson = new GsonBuilder().create();
            // разбираем полученные данные
            ArrayList<ReferenceListSrv> list = gson.fromJson(jsonString,
                    new TypeToken<ArrayList<ReferenceListSrv>>() {
                        @SuppressWarnings("unused")
                        private static final long serialVersionUID = 1;
                    }.getType());
            for (ReferenceListSrv item : list) {
                if (item.getReferenceName().equals(referenceName)) {
                    referenceUrl = item.getLinks().get(0).getLink();
                    break;
                }
            }
        }
        return referenceUrl;
    }

    /**
     * Сохраняем в базу информацию по шаблону операции и связанные с ним данные.
     *
     * @param pattern pattern
     * @return Bundle
     */
    private Bundle savePattern(OperationPatternSrv pattern) {

        Bundle result = new Bundle();

        OperationPatternDBAdapter adapter0 = new OperationPatternDBAdapter(
                new ToirDatabaseContext(mContext));
        if (adapter0.replace(pattern.getLocal()) == -1) {
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        OperationPatternStepDBAdapter adapter1 = new OperationPatternStepDBAdapter(
                new ToirDatabaseContext(mContext));
        if (!adapter1.saveItems(OperationPatternSrv
                .getOperationPatternSteps(pattern))) {
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        OperationPatternStepResultDBAdapter adapter2 = new OperationPatternStepResultDBAdapter(
                new ToirDatabaseContext(mContext));
        if (!adapter2.saveItems(OperationPatternSrv
                .getOperationPatternStepResults(pattern))) {
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        MeasureTypeDBAdapter adapter3 = new MeasureTypeDBAdapter(
                new ToirDatabaseContext(mContext));
        if (!adapter3.saveItems(OperationPatternStepSrv.getMeasureTypes(pattern
                .getSteps()))) {
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * Сохраняем в базу возможные результаты выполнения и типы операций
     *
     * @param results results
     * @return Bundle
     */
    private Bundle saveOperationResult(ArrayList<OperationResultSrv> results) {

        Bundle result = new Bundle();

        OperationResultDBAdapter adapter0 = new OperationResultDBAdapter(
                new ToirDatabaseContext(mContext));
        if (!adapter0
                .saveItems(OperationResultSrv.getOperationResults(results))) {
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        OperationTypeDBAdapter adapter1 = new OperationTypeDBAdapter(
                new ToirDatabaseContext(mContext));

        if (!adapter1.saveItems(OperationResultSrv.getOperationTypes(results))) {
            result.putBoolean(IServiceProvider.RESULT, false);
            return result;
        }

        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * Сохраняем в базу типы документов
     *
     * @param array array
     * @return Bundle
     */
    private Bundle saveDocumentType(ArrayList<DocumentationTypeSrv> array) {

        Bundle result = new Bundle();

        if (array == null) {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Остсутствуют типы документации для сохранения.");
            return result;
        }

        DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(
                new ToirDatabaseContext(mContext));

        if (adapter
                .saveItems(DocumentationTypeSrv.getDocumentationTypes(array))) {
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } else {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Ошибка при сохранении типов документации.");
            return result;
        }
    }

    /**
     * Сохраняем в базу оборудование
     *
     * @param element element
     * @return Bundle
     */
    private Bundle saveEquipment(EquipmentSrv element) {

        Bundle result = new Bundle();

        if (element == null) {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Остсутствует оборудование для сохранения.");
            return result;
        }

        EquipmentDBAdapter equipmentAdapter = new EquipmentDBAdapter(
                new ToirDatabaseContext(mContext));

        if (equipmentAdapter.replace(element.getLocal()) == -1) {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Ошибка сохранения оборудования.");
            return result;
        }

        EquipmentTypeDBAdapter equipmentTypeAdapter = new EquipmentTypeDBAdapter(
                new ToirDatabaseContext(mContext));
        if (equipmentTypeAdapter.replace(element.getEquipmentType().getLocal()) == -1) {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Ошибка сохранения типа оборудования.");
            return result;
        }

        CriticalTypeDBAdapter criticalTypeAdapter = new CriticalTypeDBAdapter(
                new ToirDatabaseContext(mContext));
        if (criticalTypeAdapter
                .replace(element.getCriticalityType().getLocal()) == -1) {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Ошибка сохранения критичности оборудования.");
            return result;
        }

        EquipmentStatusDBAdapter equipmentStatusAdapter = new EquipmentStatusDBAdapter(
                new ToirDatabaseContext(mContext));
        if (equipmentStatusAdapter.replace(element.getEquipmentStatus()
                .getLocal()) == -1) {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Ошибка сохранения статуса оборудования.");
            return result;
        }

        EquipmentDocumentationDBAdapter documentationAdapter = new EquipmentDocumentationDBAdapter(
                new ToirDatabaseContext(mContext));
        ArrayList<EquipmentSrv> elements = new ArrayList<>();
        elements.add(element);
        if (!documentationAdapter.saveItems(EquipmentSrv
                .getEquipmentDocumentations(elements))) {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Ошибка сохранения документации оборудования.");
            return result;
        }

        DocumentationTypeDBAdapter documentationTypeAdapter = new DocumentationTypeDBAdapter(
                new ToirDatabaseContext(mContext));
        if (!documentationTypeAdapter.saveItems(EquipmentSrv
                .getDocumentationTypes(elements))) {
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Ошибка сохранения типов документации оборудования.");
            return result;
        }

        result.putBoolean(IServiceProvider.RESULT, true);
        return result;
    }

    /**
     * Сохраняем документацию
     *
     * @param array         Список документации в серверном представлении
     * @param equipmentUuid UUID оборудования к которому привязана документация
     * @return Bundle
     */
    private Bundle saveDocumentations(
            ArrayList<EquipmentDocumentationSrv> array, String equipmentUuid) {

        Bundle result;

        if (array == null) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Остсутствует документация для сохранения.");
            return result;
        }

        EquipmentDocumentationDBAdapter adapter = new EquipmentDocumentationDBAdapter(
                new ToirDatabaseContext(mContext));

        if (!adapter.saveItems(EquipmentDocumentationSrv
                .getEquipmentDocumentations(array, equipmentUuid))) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE,
                    "Ошибка сохранения документации.");
            return result;
        }

        result = saveDocumentType(EquipmentDocumentationSrv
                .getDocumentationTypesSrv(array));

        return result;
    }

    /**
     * Получаем токен. Метод использульзуется для проверки наличия токена, так
     * как может сложится ситуация когда пользователь вошел в систему но токен
     * не получил из за отсутствия связи.
     *
     * @return boolean
     */
    private boolean checkToken() {

        AuthorizedUser au = AuthorizedUser.getInstance();
        if (au.getToken() == null) {
            Call<TokenSrv> call = ToirAPIFactory.getTokenService().user(au.getTagId());
            try {
                retrofit.Response<TokenSrv> response = call.execute();
                TokenSrv token = response.body();
                au.setToken(token.getAccessToken());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    private static class ReferenceName {
        public static String OperationResult = "OperationResult";
    }

}
