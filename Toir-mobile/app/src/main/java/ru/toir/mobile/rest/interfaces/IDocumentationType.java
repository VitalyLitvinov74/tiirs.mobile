package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.DocumentationType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IDocumentationType {
    @GET("/references/documentation-type")
    Call<List<DocumentationType>> documentationType(@Header("Authorization") String token);
    @GET("/references/documentation-type")
    Call<List<DocumentationType>> documentationType(@Header("Authorization") String token,
                                                    @Query("changedAfter") String changedAfter);
    @GET("/references/documentation-type")
    Call<List<DocumentationType>> documentationTypeById(@Header("Authorization") String token,
                                                        @Query("id") String id);
}
