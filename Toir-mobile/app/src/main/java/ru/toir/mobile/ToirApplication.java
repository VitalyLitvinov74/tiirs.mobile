package ru.toir.mobile;

import android.app.Application;
import ru.toir.mobile.db.ToirRealm;

/**
 * @author Oleg Ivanov
 *
 */
@SuppressWarnings("unused")
public class ToirApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

        // инициализируем базу данных Realm
        ToirRealm.init(this);
    }
}
