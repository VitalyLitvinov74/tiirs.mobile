package ru.toir.mobile.multi.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import io.realm.Realm;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.Equipment;
import ru.toir.mobile.multi.db.realm.Journal;
import ru.toir.mobile.multi.db.realm.OrderStatus;
import ru.toir.mobile.multi.db.realm.Orders;
import ru.toir.mobile.multi.db.realm.User;

public class MainFunctions {

    //private Realm realmDB;
    private static final String BOT = "bot489333537:AAFWzSpAuWl0v1KJ3sTQKYABpjY0ERgcIcY";
    private BlockingDeque<String> queue = new LinkedBlockingDeque<>();
    private ConnectionFactory factory = new ConnectionFactory();

    /**
     * Хз зачем было реализовано.
     *
     * @param context Context
     * @return String | null
     */
    public static String getIMEI(Context context) {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mngr == null) {
            return null;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            return mngr.getDeviceId();
        } else {
            return null;
        }

    }

    public static void addToJournal(final String description) {
        Realm realmDB = Realm.getDefaultInstance();
        AuthorizedUser authUser = AuthorizedUser.getInstance();
        User user = realmDB.where(User.class)
                .equalTo("login", authUser.getLogin())
                .findFirst();
        if (user != null) {
            realmDB.beginTransaction();
            Journal record = new Journal();
            long next_id = Journal.getLastId() + 1;
            record.set_id(next_id);
            record.setDate(new Date());
            record.setDescription(description);
            record.setUserUuid(user.getUuid());
            realmDB.copyToRealm(record);
            realmDB.commitTransaction();
        }

        realmDB.close();
    }

    public static int getActiveOrdersCount() {
        int count = 0;
        final Realm realmDB = Realm.getDefaultInstance();
        AuthorizedUser authUser = AuthorizedUser.getInstance();
        final User user = realmDB.where(User.class)
                .equalTo("login", authUser.getLogin())
                .findFirst();
        if (user != null) {
            count = realmDB.where(Orders.class)
                    .equalTo("user.uuid", user.getUuid())
                    .findAll()
                    .where()
                    .equalTo("orderStatus.uuid", OrderStatus.Status.IN_WORK)
                    .or()
                    .equalTo("orderStatus.uuid", OrderStatus.Status.NEW)
                    .or()
                    .equalTo("orderStatus.uuid", OrderStatus.Status.UN_COMPLETE)
                    .findAll().size();
        }

        realmDB.close();
        return count;
    }

    public static String getPicturesDirectory(Context context) {
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + "Android"
                + File.separator
                + "data"
                + File.separator
                + context.getPackageName()
                + File.separator;
        return filename;
    }

    //  функция возвращает путь до фотографии оборудования
    public static String getEquipmentImage(String path, Equipment equipment) {
        if (equipment != null) {
            if (equipment.getImageFileName() != null && equipment.getImageFileName().length() > 5) {
                return equipment.getImageFileName();
            }

            if (equipment.getEquipmentModel() != null) {
                return equipment.getEquipmentModel().getImageFileName();
            }
        }

        return null;
    }

    /**
     * md5 hash
     *
     * @param string Строка
     * @return String|null
     */
    public static String md5(final String string) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(string.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte byteMessageDigest : messageDigest) {
                String hash = Integer.toHexString(0xFF & byteMessageDigest);
                if (hash.length() < 2) {
                    hexString.append("0").append(hash);
                } else {
                    hexString.append(hash);
                }
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    //  функция отправляет сообщение от бота Тоирус с чат с текущим пользователем данного устройства и на все его клиенты
    public int sendMessageToTelegram(Context context, String message) {
        SharedPreferences sharedPref = context.getSharedPreferences("messendgers", Context.MODE_PRIVATE);
        String chat_id = sharedPref.getString(context.getString(R.string.telegram_chat_id), "0");
        new AsyncRequest().execute(chat_id, message);
        return 0;
    }

    //  функция отправляет сообщение от бота Тоирус с чат с текущим пользователем данного устройства и на все его клиенты
    public int sendMessageToAMPQ(User currentUser, String channel, String message, String messageType) {
        setupConnectionFactory();
        publishToAMQP(channel);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageText", message);
            jsonObject.put("user", currentUser.getName());
            jsonObject.put("messageType", messageType);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        String jsonString = jsonObject.toString();
        publishMessage(jsonString);
        return 0;
    }

    private void publishMessage(String message) {
        try {
            Log.d("", "[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setupConnectionFactory() {
        String uri = "amqp://root:root@192.168.1.71";
        try {
            factory.setAutomaticRecoveryEnabled(false);
            factory.setUri(uri);
            //factory.setHost(uri);
        } catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    private void publishToAMQP(final String channel) {
        Thread publishThread;
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        //ch.queueDeclare(channel, true, false, false, null);
                        ch.exchangeDeclare(channel, "direct");
                        //ch.confirmSelect();
                        //ch.exchangeDeclare(channel, "fanout");

                        while (true) {
                            String message = queue.takeFirst();
                            try {
                                //ch.basicPublish("amq.fanout", "chat", null, message.getBytes());
                                ch.basicPublish(channel, "chat", null, message.getBytes());
                                //ch.basicPublish("", channel, null, message.getBytes());
                                //Log.d("", "[s][" + channel + "] " + message);
                                //ch.waitForConfirmsOrDie();
                            } catch (Exception e) {
                                //Log.d("","[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }

    private class AsyncRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                //https://api.telegram.org/bot<Bot_token>/sendMessage?chat_id=<chat_id>&text=Привет%20мир
                URL url = new URL("https://api.telegram.org/" + BOT + "/sendMessage?chat_id=" + arg[0] + "&text=" + arg[1]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                String jsonString = result.toString();
                if (result.length() > 0) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray res = jsonObject.getJSONArray("result");
                    JSONObject res0 = res.getJSONObject(0);
                    JSONObject message = res0.getJSONObject("message");
                    JSONObject chat = message.getJSONObject("chat");
                }
                return "";
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (urlConnection != null)
                urlConnection.disconnect();
            return "";
        }
    }
}


