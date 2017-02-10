package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Tool;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITool {
    @GET("/references/tool")
    Call<List<Tool>> tool(@Header("Authorization") String token);

    @GET("/references/tool")
    Call<List<Tool>> tool(@Header("Authorization") String token,
                          @Query("changedAfter") String changedAfter);

    @GET("/references/tool")
    Call<List<Tool>> toolById(@Header("Authorization") String token,
                              @Query("id") String id);

}
