package ru.toir.mobile.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import java.io.File;
import java.util.Date;

import io.realm.Realm;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.Journal;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.User;

public class MainFunctions {

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
        User user = realmDB.where(User.class)
                .equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
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
        final User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
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
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + "Android"
                + File.separator
                + "data"
                + File.separator
                + context.getPackageName()
                + File.separator;
    }

    //  функция возвращает путь до фотографии оборудования
    public static String getEquipmentImage(String path, Equipment equipment) {
        if (equipment != null) {
            if (equipment.getImage() != null && equipment.getImage().length() > 5) {
                return equipment.getImage();
            }

            if (equipment.getEquipmentModel() != null) {
                return equipment.getEquipmentModel().getImage();
            }
        }

        return null;
    }
}


