package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Tool;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITool {
    @GET("/api/tool")
    Call<List<Tool>> get();

    @GET("/api/tool")
    Call<List<Tool>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/tool")
    Call<List<Tool>> getById(@Query("id") String id);

    @GET("/api/tool")
    Call<List<Tool>> getById(@Query("id[]") String[] id);

    @GET("/api/tool")
    Call<List<Tool>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/tool")
    Call<List<Tool>> getByUuid(@Query("uuid[]") String[] uuid);
}
