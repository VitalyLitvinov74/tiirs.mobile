package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.ToolType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IToolType {
    @GET("/api/references/tool-type")
    Call<List<ToolType>> toolType();

    @GET("/api/references/tool-type")
    Call<List<ToolType>> toolType(@Query("changedAfter") String changedAfter);

    @GET("/api/references/tool-type")
    Call<List<ToolType>> toolTypeById(@Query("id") String id);

}
