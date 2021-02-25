package ru.toir.mobile.multi.rfid;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.lang.reflect.Constructor;

import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.rfid.driver.RfidDriverNull;
import ru.toir.mobile.multi.rfid.driver.RfidDriverUHF;

/**
 * @author Dmitriy Logachev
 */
public class RfidDialog extends DialogFragment {

    public static final int READER_COMMAND_READ_ID = 1;
    public static final int READER_COMMAND_READ_DATA = 2;
    public static final int READER_COMMAND_READ_DATA_ID = 3;
    public static final int READER_COMMAND_WRITE_DATA = 4;
    public static final int READER_COMMAND_WRITE_DATA_ID = 5;
    public static final int READER_COMMAND_READ_MULTI_ID = 6;

    public static final String TAG = "RfidDialog";
    private Class<?> driverClass;
    private RfidDriverBase driver;

    // команда драйвера которая должна быть выполнена при старте диалога
    private int command;

    // параметры передаваемые в драйвер
    private String tagPassword;
    private String tagId;
    private int tagMemoryBank;
    private int tagAddress;
    private String tagWriteData;
    private int tagReadCount;
    private String[] tagIds;
    private String driverClassName;

    /*
     * проверка для защиты от повтороного выполнения комманды драйвера при
     * старте фрагмента диалога после запуска стронней activity
     */
    private boolean isStarted = false;

    /*
     * обработчик передаётся в драйвер, в том числе используется для отправки
     * сообщений об ошибках если драйвер не удаётся запустить
     */
    private Handler mHandler;

    private boolean isInited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        getDialog().setTitle("Считайте метку");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity()
                .getApplicationContext());

        if (driverClassName == null || driverClassName.isEmpty()) {
            Toast.makeText(getActivity(), "Пожалуйста выберите драйвер в меню и укажите сервер!",
                    Toast.LENGTH_LONG).show();
            return null;
        }

        // если драйвер UHF, получаем реальный класс
        if (driverClassName.equals(RfidDriverUHF.class.getCanonicalName())) {
            driverClassName = sp.getString(getActivity().getApplicationContext()
                    .getString(R.string.default_uhf_driver_key), RfidDriverNull.class.getCanonicalName());
        }

        // пытаемся получить класс драйвера
        try {
            driverClass = Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.toString());
            Message message = new Message();
            message.what = RfidDriverBase.RESULT_RFID_CLASS_NOT_FOUND;
            mHandler.sendMessage(message);
        }

        // пытаемся создать объект драйвера
        try {
            Constructor<?> c = driverClass.getConstructor();
            driver = (RfidDriverBase) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = RfidDriverBase.RESULT_RFID_CLASS_NOT_FOUND;
            mHandler.sendMessage(message);
        }

        // т.к. работаем из DialogFragment, устанавливаем в качестве элемента
        // интеграции DialogFragment
        driver.setIntegration(this);

        // передаём в драйвер контекст
        driver.setContext(getActivity().getApplicationContext());

        // передаём в драйвер обработчик
        driver.setHandler(mHandler);

        // инициализируем драйвер
        if (!driver.init()) {
            Message message = new Message();
            message.what = RfidDriverBase.RESULT_RFID_INIT_ERROR;
            mHandler.sendMessage(message);
        } else {
            isInited = true;
        }

        View view = null;
        if (isInited) {
            view = driver.getView(inflater, viewGroup);
        }

        return view;
    }

    @Override
    public void onStart() {

        super.onStart();

        if (!isInited) {
            return;
        }

        if (isStarted) {
            return;
        }

        isStarted = true;

        switch (command) {
            case READER_COMMAND_READ_ID:
                driver.readTagId();
                break;
            case READER_COMMAND_READ_MULTI_ID:
                driver.readMultiplyTagId(tagIds);
                break;
            case READER_COMMAND_READ_DATA:
                driver.readTagData(tagPassword, tagMemoryBank, tagAddress, tagReadCount);
                break;
            case READER_COMMAND_READ_DATA_ID:
                driver.readTagData(tagPassword, tagId, tagMemoryBank, tagAddress, tagReadCount);
                break;
            case READER_COMMAND_WRITE_DATA:
                driver.writeTagData(tagPassword, tagMemoryBank, tagAddress, tagWriteData);
                break;
            case READER_COMMAND_WRITE_DATA_ID:
                driver.writeTagData(tagPassword, tagId, tagMemoryBank, tagAddress, tagWriteData);
                break;
            default:
                driver.readTagId();
                break;
        }
    }

    /**
     * Необходимо потому что один из драйверов стартует отдельную activity для своих нужд.
     * В частности RfidDriverBarcode.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Message message = new Message();
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (data != null) {
                    String result = data.getStringExtra("SCAN_RESULT");
                    if (result != null && !result.equals("")) {
                        RfidDriverMsg msg = RfidDriverMsg.tagMsg("0000" + Tag.Type.TAG_TYPE_GRAPHIC_CODE + ":" + result);
                        // Так как в зависимости от вызванного метода разнится тип возвращаемиого результата,
                        // проверяем какой именно метод был вызван.
                        if (command == READER_COMMAND_READ_ID) {
                            message.what = RfidDriverBase.RESULT_RFID_SUCCESS;
                            message.obj = msg;
                        } else if (command == READER_COMMAND_READ_MULTI_ID) {
                            message.what = RfidDriverBase.RESULT_RFID_SUCCESS;
                            message.obj = new RfidDriverMsg[]{msg};
                        } else {
                            message.what = RfidDriverBase.RESULT_RFID_READ_ERROR;
                        }
                    } else {
                        message.what = RfidDriverBase.RESULT_RFID_READ_ERROR;
                    }
                } else {
                    message.what = RfidDriverBase.RESULT_RFID_CANCEL;
                }

                break;
            default:
                message.what = RfidDriverBase.RESULT_RFID_CANCEL;
                break;
        }

        mHandler.sendMessage(message);
    }

    /**
     * <p>
     * Чтение(поиск первой попавшейся метки) Id метки.
     * </p>
     * Устанавливаем команду которую нужно будет выполнить при старте диалога.
     *
     * @param driverClass   driverClass
     */
    public void readTagId(String driverClass) {
        driverClassName = driverClass;
        command = READER_COMMAND_READ_ID;
    }

    /**
     * <p>
     * Поиск всех меток в поле считывателя.
     * </p>
     * Устанавливаем команду которую нужно будет выполнить при старте диалога.
     *
     * @param driverClass   driverClass
     */
    public void readMultiTagId(String driverClass) {
        driverClassName = driverClass;
        command = READER_COMMAND_READ_MULTI_ID;
        tagIds = new String[]{};
    }

    /**
     * <p>
     * Поиск всех меток в поле считывателя.
     * </p>
     * Устанавливаем команду которую нужно будет выполнить при старте диалога.
     *
     * @param driverClass   driverClass
     * @param id   id метки которую мы хотим считать
     */
    public void readMultiTagId(String driverClass, String id) {
        driverClassName = driverClass;
        command = READER_COMMAND_READ_MULTI_ID;
        tagIds = new String[]{id};
    }

    /**
     * <p>
     * Поиск всех меток в поле считывателя.
     * </p>
     * Устанавливаем команду которую нужно будет выполнить при старте диалога.
     *
     * @param driverClass   driverClass
     * @param ids   Список id меток которые мы хотим считать
     */
    public void readMultiTagId(String driverClass, String[] ids) {
        driverClassName = driverClass;
        command = READER_COMMAND_READ_MULTI_ID;
        tagIds = ids;
    }

    /**
     * <p>
     * Чтение данных из памяти конкретной метки.
     * </p>
     * Устанавливаем команду которую нужно будет выполнить при старте диалога.
     *
     * @param driverClass   driverClass
     * @param password   password
     * @param id         tagId
     * @param memoryBank memoryBank
     * @param address    address
     * @param count      count
     */
    public void readTagData(String driverClass, String password, String id, int memoryBank, int address, int count) {
        driverClassName = driverClass;
        tagPassword = password;
        tagId = id;
        tagMemoryBank = memoryBank;
        tagAddress = address;
        tagReadCount = count;

        command = READER_COMMAND_READ_DATA_ID;
    }

    /**
     * <p>
     * Чтение данных из памяти первой найденной метки.
     * </p>
     * Устанавливаем команду которую нужно будет выполнить при старте диалога.
     *
     * @param driverClass   driverClass
     * @param password   password
     * @param memoryBank memoryBank
     * @param address    address
     * @param count      count
     */
    public void readTagData(String driverClass, String password, int memoryBank, int address, int count) {
        driverClassName = driverClass;
        tagPassword = password;
        tagMemoryBank = memoryBank;
        tagAddress = address;
        tagReadCount = count;

        command = READER_COMMAND_READ_DATA;
    }

    /**
     * <p>
     * Пишем в память конкретной метки.
     * </p>
     * Устанавливаем команду которую нужно будет выполнить при старте диалога.
     *
     * @param driverClass   driverClass
     * @param password   password
     * @param id         id
     * @param memoryBank memoryBank
     * @param address    address
     * @param data       data
     */
    public void writeTagData(String driverClass, String password, String id, int memoryBank, int address, String data) {
        driverClassName = driverClass;
        tagPassword = password;
        tagId = id;
        tagMemoryBank = memoryBank;
        tagAddress = address;
        tagWriteData = data;

        command = READER_COMMAND_WRITE_DATA_ID;
    }

    /**
     * <p>
     * Пишем в память первой найденной метки.
     * </p>
     * Устанавливаем команду которую нужно будет выполнить при старте диалога.
     *
     * @param driverClass   driverClass
     * @param password   password
     * @param memoryBank memoryBank
     * @param address    address
     * @param data       data
     */
    public void writeTagData(String driverClass, String password, int memoryBank, int address, String data) {
        driverClassName = driverClass;
        tagPassword = password;
        tagMemoryBank = memoryBank;
        tagAddress = address;
        tagWriteData = data;

        command = READER_COMMAND_WRITE_DATA;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        if (driver != null)
            driver.close();
        driver = null;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }
}
