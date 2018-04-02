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
    @GET("/equipment")
    Call<List<Equipment>> get();

    @GET("/equipment")
    Call<List<Equipment>> get(@Query("changedAfter") String changedAfter);

    @GET("/equipment")
    Call<List<Equipment>> getById(@Query("id") String id);

    @GET("/equipment")
    Call<List<Equipment>> getById(@Query("id[]") String[] id);

    @GET("/equipment")
    Call<List<Equipment>> getByUuid(@Query("uuid") String uuid);

    @GET("/equipment")
    Call<List<Equipment>> getByUuid(@Query("uuid[]") String[] uuid);
}
