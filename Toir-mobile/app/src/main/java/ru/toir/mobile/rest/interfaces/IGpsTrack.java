package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.rest.ToirAPIResponse;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 09.02.17.
 */

public interface IGpsTrack {
    @POST("/gpstrack/create")
    Call<ToirAPIResponse> send(@Body List<GpsTrack> data);
}
