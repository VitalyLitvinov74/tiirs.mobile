package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.EquipmentStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IEquipmentStatus {
    @GET("/api/equipment/status")
    Call<List<EquipmentStatus>> equipmentStatus();

    @GET("/api/equipment/status")
    Call<List<EquipmentStatus>> equipmentStatus(@Query("changedAfter") String changedAfter);

    @GET("/api/equipment/status")
    Call<List<EquipmentStatus>> equipmentStatusById(@Query("id") String id);
}
