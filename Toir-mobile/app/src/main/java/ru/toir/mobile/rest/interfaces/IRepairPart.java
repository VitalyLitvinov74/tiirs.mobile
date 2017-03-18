package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.RepairPart;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IRepairPart {
    @GET("/api/references/repair-part")
    Call<List<RepairPart>> repairPart();

    @GET("/api/references/repair-part")
    Call<List<RepairPart>> repairPart(@Query("changedAfter") String changedAfter);

    @GET("/api/references/repair-part")
    Call<List<RepairPart>> repairPartById(@Query("id") String id);

}
