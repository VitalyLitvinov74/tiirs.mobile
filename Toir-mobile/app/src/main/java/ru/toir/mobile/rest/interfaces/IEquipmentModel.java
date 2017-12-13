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
    @GET("/equipment-model")
    Call<List<EquipmentModel>> get();

    @GET("/equipment-model")
    Call<List<EquipmentModel>> get(@Query("changedAfter") String changedAfter);

    @GET("/equipment-model")
    Call<List<EquipmentModel>> getById(@Query("id") String id);

    @GET("/equipment-model")
    Call<List<EquipmentModel>> getById(@Query("id") String[] id);

    @GET("/equipment-model")
    Call<List<EquipmentModel>> getByUuid(@Query("uuid") String uuid);

    @GET("/equipment-model")
    Call<List<EquipmentModel>> getByUuid(@Query("uuid") String[] uuid);
}
