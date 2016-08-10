package ru.toir.mobile;

import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.db.tables.Task;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import android.app.Dialog;
import android.text.Html;
import android.text.util.Linkify;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutDialog extends Dialog {

    private static Context mContext = null;
    private static final String TAG = "AboutDialog";

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");

        TextView tv;

        setContentView(R.layout.about);

        tv = (TextView) findViewById(R.id.legal_text);
        tv.setText(readRawTextFile(R.raw.legal));
        tv.setOnClickListener(new View.OnClickListener() {

            private int count = 0;

            @Override
            public void onClick(View v) {

                count++;
                if (count == 7) {
                    ToirDatabaseContext databaseContext = new
                            ToirDatabaseContext(
                            mContext);
                    DatabaseHelper helper = DatabaseHelper
                            .getInstance(databaseContext);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    TaskDBAdapter taskDBAdapter = new TaskDBAdapter(
                            databaseContext);
                    EquipmentOperationDBAdapter operationDBAdapter = new
                            EquipmentOperationDBAdapter(
                            databaseContext);
                    EquipmentOperationResultDBAdapter
                            equipmentOperationResultDBAdapter = new
                            EquipmentOperationResultDBAdapter(databaseContext);
                    boolean success;

                    List<Task> tasks = taskDBAdapter.getOrders();
                    success = true;
                    db.beginTransaction();
                    for (Task task : tasks) {
                        task.setTask_status_uuid(TaskStatusDBAdapter.Status
                                .IN_WORK);
                        if (taskDBAdapter.replace(task) == -1) {
                            success = false;
                            break;
                        }
                    }
                    if (success) {
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        Toast.makeText(mContext,
                                "Статусы нарядов изменены.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        db.endTransaction();
                        Toast.makeText(mContext,
                                "Ошибка при изменении статусов нарядов!",
                                Toast.LENGTH_LONG).show();
                    }

                    List<EquipmentOperation> operations = operationDBAdapter
                            .getEquipmentOperations();
                    success = true;
                    db.beginTransaction();
                    for (EquipmentOperation operation : operations) {
                        operation.setOperation_status_uuid
                                (OperationStatusDBAdapter.Status.NEW);
                        if (operationDBAdapter.replace(operation) == -1) {
                            success = false;
                            break;
                        }

                        EquipmentOperationResult operationResult =
                                equipmentOperationResultDBAdapter
                                        .getItemByOperation(operation.getUuid
                                                ());
                        if (operationResult != null) {
                            equipmentOperationResultDBAdapter.delete(operation.getUuid());
                        }
                    }
                    if (success) {
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        Toast.makeText(mContext,
                                "Статусы операций изменены.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        db.endTransaction();
                        Toast.makeText(mContext,
                                "Ошибка при изменении статусов операций!",
                                Toast.LENGTH_LONG).show();
                    }

                    count = 0;
                } else if (count == 4) {
                    Toast.makeText(mContext, "Давай Джексон, еще разочек!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv = (TextView) findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
        tv.setLinkTextColor(Color.WHITE);
        Linkify.addLinks(tv, Linkify.ALL);
    }

    public static String readRawTextFile(int id) {
        InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while ((line = buf.readLine()) != null)
                text.append(line);
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
