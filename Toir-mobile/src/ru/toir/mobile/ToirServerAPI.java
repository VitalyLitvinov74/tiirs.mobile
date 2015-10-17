package ru.toir.mobile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * <p>Класс для взаимодействия с сервером ТОиР</p>
 * @author koputo
 *
 */
public class ToirServerAPI {
	private Context context;
	private String SERVER_API = "serverApiVersion";
	
	public ToirServerAPI(Context _context){
		context = _context;
	}

	/**
	 * <p>Возвращает текущую версию АПИ с которой работает приложение.</p>
	 * @return int Текущая версия АПИ с которой работает приложение.
	 */
	public int getVersion() {
		ApplicationInfo applicatinInfo;
		try {
			applicatinInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if(applicatinInfo.metaData.containsKey(SERVER_API)){
				return applicatinInfo.metaData.getInt(SERVER_API);
			}
			else {
				return -1;
			}
		}catch(NameNotFoundException e){
			System.out.println(e.toString());
		}
		return -1;
	}
}
