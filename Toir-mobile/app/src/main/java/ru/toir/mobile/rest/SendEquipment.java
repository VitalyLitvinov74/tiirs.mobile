package ru.toir.mobile.rest;

import android.os.AsyncTask;
import android.os.Handler;

import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.toir.mobile.db.realm.Equipment;

public class SendEquipment extends AsyncTask<Equipment, Void, Integer> {

    private Equipment equipment;

    public SendEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    protected Integer doInBackground(Equipment... equipment) {
        Call<ResponseBody> call = ToirAPIFactory.getEquipmentService().send(this.equipment);
        try {
            call.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer d) {
        super.onPostExecute(d);
        if (d == 0) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    SendEquipment task = new SendEquipment(equipment);
                    task.execute(equipment);
                }
            }, 60000);
        }
    }
}
