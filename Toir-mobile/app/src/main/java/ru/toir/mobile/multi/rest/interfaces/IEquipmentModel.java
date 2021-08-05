package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.EquipmentModel;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 25.01.17.
 */

public interface IEquipmentModel {
    @GET("/api/equipment-model")
    Call<List<EquipmentModel>> get();

    @GET("/api/equipment-model")
    Call<List<EquipmentModel>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/equipment-model")
    Call<List<EquipmentModel>> getById(@Query("id") String id);

    @GET("/api/equipment-model")
    Call<List<EquipmentModel>> getById(@Query("id[]") String[] id);

    @GET("/api/equipment-model")
    Call<List<EquipmentModel>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/equipment-model")
    Call<List<EquipmentModel>> getByUuid(@Query("uuid[]") String[] uuid);
}
