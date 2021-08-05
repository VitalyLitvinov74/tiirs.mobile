package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.RepairPart;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IRepairPart {
    @GET("/api/repair-part")
    Call<List<RepairPart>> get();

    @GET("/api/repair-part")
    Call<List<RepairPart>> get(@Query("changedAfter") String changedAfter);

    //@GET("/api/repair-part")
    //Call<List<RepairPart>> getById(@Query("id") String id);

    //@GET("/api/repair-part")
    //Call<List<RepairPart>> getById(@Query("id[]") String[] id);

    @GET("/api/repair-part")
    Call<List<RepairPart>> getByOrderUuid(@Query("uuid") String uuid);

    @GET("/api/repair-part")
    Call<List<RepairPart>> getByUuid(@Query("uuid[]") String[] uuid);
}
