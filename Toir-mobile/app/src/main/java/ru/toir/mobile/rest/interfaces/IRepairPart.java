package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.RepairPart;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IRepairPart {
    @GET("/references/repair-part")
    Call<List<RepairPart>> repairPart();

    @GET("/references/repair-part")
    Call<List<RepairPart>> repairPart(@Query("changedAfter") String changedAfter);

    @GET("/references/repair-part")
    Call<List<RepairPart>> repairPartById(@Query("id") String id);

}
