package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OperationTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationTemplate {
    @GET("/references/operation-template")
    Call<List<OperationTemplate>> operationTemplate(@Header("Authorization") String token);

    @GET("/references/operation-template")
    Call<List<OperationTemplate>> operationTemplate(@Header("Authorization") String token,
                                                    @Query("changedAfter") String changedAfter);

    @GET("/references/operation-template")
    Call<List<OperationTemplate>> operationTemplateById(@Header("Authorization") String token,
                                                        @Query("id") String id);

}
