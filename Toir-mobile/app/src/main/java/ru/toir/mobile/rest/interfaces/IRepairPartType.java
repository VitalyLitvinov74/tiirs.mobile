package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.RepairPartType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IRepairPartType {
    @GET("/repair-part-type")
    Call<List<RepairPartType>> get();

    @GET("/repair-part-type")
    Call<List<RepairPartType>> get(@Query("changedAfter") String changedAfter);

    @GET("/repair-part-type")
    Call<List<RepairPartType>> getById(@Query("id") String id);

    @GET("/repair-part-type")
    Call<List<RepairPartType>> getById(@Query("id[]") String[] id);

    @GET("/repair-part-type")
    Call<List<RepairPartType>> getByuuid(@Query("uuid") String uuid);

    @GET("/repair-part-type")
    Call<List<RepairPartType>> getByuuid(@Query("uuid[]") String[] uuid);
}
