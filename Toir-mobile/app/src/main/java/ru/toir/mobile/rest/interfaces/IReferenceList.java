package ru.toir.mobile.rest.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import ru.toir.mobile.serverapi.ReferenceListSrv;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 16.09.16.
 */
public interface IReferenceList {
    @GET("/api/references")
    Call<ReferenceListSrv> getReferenceList(@Header("Authorization") String token);
}
