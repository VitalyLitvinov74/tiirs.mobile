package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.rest.ToirAPIResponse;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 09.02.17.
 */

public interface IGpsTrack {
    @POST("/gpstrack/create")
    Call<ToirAPIResponse> sendGpsTrack(@Body List<GpsTrack> data);
}
