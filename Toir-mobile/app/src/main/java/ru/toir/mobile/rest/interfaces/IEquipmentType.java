package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.EquipmentType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IEquipmentType {
    @GET("/equipment-type")
    Call<List<EquipmentType>> get();

    @GET("/equipment-type")
    Call<List<EquipmentType>> get(@Query("changedAfter") String changedAfter);

    @GET("/equipment-type")
    Call<List<EquipmentType>> getById(@Query("id") String id);

    @GET("/equipment-type")
    Call<List<EquipmentType>> getById(@Query("id") String[] id);

    @GET("/equipment-type")
    Call<List<EquipmentType>> getByUuid(@Query("uuid") String uuid);

    @GET("/equipment-type")
    Call<List<EquipmentType>> getByUuid(@Query("uuid") String[] uuid);

}
