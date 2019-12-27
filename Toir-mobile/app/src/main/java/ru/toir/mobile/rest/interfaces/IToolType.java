package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.ToolType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IToolType {
    @GET("/tool-type")
    Call<List<ToolType>> getType();

    @GET("/tool-type")
    Call<List<ToolType>> get(@Query("changedAfter") String changedAfter);

    @GET("/tool-type")
    Call<List<ToolType>> getById(@Query("id") String id);

    @GET("/tool-type")
    Call<List<ToolType>> getById(@Query("id[]") String[] id);

    @GET("/tool-type")
    Call<List<ToolType>> getByUuid(@Query("uuid") String uuid);

    @GET("/tool-type")
    Call<List<ToolType>> getByUuid(@Query("uuid[]") String[] uuid);
}
