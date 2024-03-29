package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.ToolType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IToolType {
    @GET("/api/tool-type")
    Call<List<ToolType>> getType();

    @GET("/api/tool-type")
    Call<List<ToolType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/tool-type")
    Call<List<ToolType>> getById(@Query("id") String id);

    @GET("/api/tool-type")
    Call<List<ToolType>> getById(@Query("id[]") String[] id);

    @GET("/api/tool-type")
    Call<List<ToolType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/tool-type")
    Call<List<ToolType>> getByUuid(@Query("uuid[]") String[] uuid);
}
