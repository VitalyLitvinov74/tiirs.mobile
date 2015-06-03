package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class OrderDBAdapter {

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private final Context context;

	public OrderDBAdapter(Context context){
		this.context = context;
	}

}
