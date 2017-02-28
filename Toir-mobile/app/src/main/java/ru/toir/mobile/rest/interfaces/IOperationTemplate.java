package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OperationTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationTemplate {
    @GET("/api/operation/template")
    Call<List<OperationTemplate>> operationTemplate();

    @GET("/api/operation/template")
    Call<List<OperationTemplate>> operationTemplate(@Query("changedAfter") String changedAfter);

    @GET("/api/operation/template")
    Call<List<OperationTemplate>> operationTemplateById(@Query("id") String id);

}
