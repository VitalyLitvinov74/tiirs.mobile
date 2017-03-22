package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
