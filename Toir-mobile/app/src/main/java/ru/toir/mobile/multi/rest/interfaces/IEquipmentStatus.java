package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.EquipmentStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IEquipmentStatus {
    @GET("/api/equipment-status")
    Call<List<EquipmentStatus>> get();

    @GET("/api/equipment-status")
    Call<List<EquipmentStatus>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/equipment-status")
    Call<List<EquipmentStatus>> getById(@Query("id") String id);

    @GET("/api/equipment-status")
    Call<List<EquipmentStatus>> getById(@Query("id[]") String[] id);

    @GET("/api/equipment-status")
    Call<List<EquipmentStatus>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/equipment-status")
    Call<List<EquipmentStatus>> getByUuid(@Query("uuid[]") String[] uuid);
}
