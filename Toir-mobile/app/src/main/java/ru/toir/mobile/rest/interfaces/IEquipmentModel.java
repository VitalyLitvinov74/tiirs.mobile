package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.EquipmentModel;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 25.01.17.
 */

public interface IEquipmentModel {
    @GET("/api/equipment/model")
    Call<List<EquipmentModel>> equipmentModel();

    @GET("/api/equipment/model")
    Call<List<EquipmentModel>> equipmentModel(@Query("changedAfter") String changedAfter);

    @GET("/api/equipment/model")
    Call<List<EquipmentModel>> equipmentModelById(@Query("id") String id);
}
