package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.EquipmentStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IEquipmentStatus {
    @GET("/references/equipment-status")
    Call<List<EquipmentStatus>> equipmentStatus();

    @GET("/references/equipment-status")
    Call<List<EquipmentStatus>> equipmentStatus(@Query("changedAfter") String changedAfter);

    @GET("/references/equipment-status")
    Call<List<EquipmentStatus>> equipmentStatusById(@Query("id") String id);
}
