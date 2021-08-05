package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.EquipmentType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IEquipmentType {
    @GET("/api/equipment-type")
    Call<List<EquipmentType>> get();

    @GET("/api/equipment-type")
    Call<List<EquipmentType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/equipment-type")
    Call<List<EquipmentType>> getById(@Query("id") String id);

    @GET("/api/equipment-type")
    Call<List<EquipmentType>> getById(@Query("id[]") String[] id);

    @GET("/api/equipment-type")
    Call<List<EquipmentType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/equipment-type")
    Call<List<EquipmentType>> getByUuid(@Query("uuid[]") String[] uuid);

}
