package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Equipment;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 25.01.17.
 */

public interface IEquipment {
    @GET("/api/equipment")
    Call<List<Equipment>> equipment();

    @GET("/api/equipment")
    Call<List<Equipment>> equipment(@Query("changedAfter") String changedAfter);

    @GET("/api/equipment")
    Call<List<Equipment>> equipmentById(@Query("id") String id);
}
