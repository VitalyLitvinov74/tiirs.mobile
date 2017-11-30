package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Tool;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITool {
    @GET("/tool")
    Call<List<Tool>> tool();

    @GET("/tool")
    Call<List<Tool>> tool(@Query("changedAfter") String changedAfter);

    @GET("/tool")
    Call<List<Tool>> toolById(@Query("id") String id);

    @GET("/tool")
    Call<List<Tool>> toolById(@Query("id") String[] id);

    @GET("/tool")
    Call<List<Tool>> toolByUuid(@Query("uuid") String uuid);

    @GET("/tool")
    Call<List<Tool>> toolByUuid(@Query("uuid") String[] uuid);
}
