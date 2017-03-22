package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
