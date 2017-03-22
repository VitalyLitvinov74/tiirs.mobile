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
    @GET("/api/references/repair-part-type")
    Call<List<RepairPartType>> repairPartType();

    @GET("/api/references/repair-part-type")
    Call<List<RepairPartType>> repairPartType(@Query("changedAfter") String changedAfter);

    @GET("/api/references/repair-part-type")
    Call<List<RepairPartType>> repairPartTypeById(@Query("id") String id);
}
