package ru.toir.mobile.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.Date;

import io.realm.Realm;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.db.realm.Journal;
import ru.toir.mobile.db.realm.User;

public class MainFunctions {

private Realm realmDB;

public static String getIMEI(Context context){
    TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
    return mngr.getDeviceId();
 }

public static void addToJournal(final String description){
    final Realm realmDB = Realm.getDefaultInstance();
    final User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
    if (user!=null) {
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Journal record = realmDB.createObject(Journal.class);
                record.setDate(new Date());
                record.setDescription(description);
                record.setUserUuid(user.getUuid());
            }
        });
        }
    }
}
