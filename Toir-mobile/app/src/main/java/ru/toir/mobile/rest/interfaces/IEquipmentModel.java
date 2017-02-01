package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.EquipmentModel;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 25.01.17.
 */

public interface IEquipmentModel {
    @GET("/references/equipment-model")
    Call<List<EquipmentModel>> equipmentModel(@Header("Authorization") String token);
    @GET("/references/equipment-model")
    Call<List<EquipmentModel>> equipmentModel(@Header("Authorization") String token,
                                              @Query("changedAfter") String changedAfter);
    @GET("/references/equipment-model")
    Call<List<EquipmentModel>> equipmentModelById(@Header("Authorization") String token,
                                                  @Query("id") String id);
}
