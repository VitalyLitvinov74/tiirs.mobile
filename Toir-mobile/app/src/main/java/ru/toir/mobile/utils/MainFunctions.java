package ru.toir.mobile.utils;

import android.content.Context;
import android.os.Environment;
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

    private Realm realmDB;

    public static String getIMEI(Context context) {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    public static void addToJournal(final String description) {
        final Realm realmDB = Realm.getDefaultInstance();
        final User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user != null) {
            realmDB.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Journal record = realmDB.createObject(Journal.class);
                    long next_id = realm.where(Journal.class).max("_id").intValue() + 1;
                    record.set_id(next_id);
                    record.setDate(new Date());
                    record.setDescription(description);
                    record.setUserUuid(user.getUuid());
                }
            });
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


