package ru.toir.mobile.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.toir.mobile.db.realm.GpsTrack;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 09.02.17.
 */

public interface IGpsTrack {
    @POST("/gpstrack/create")
    Call<ResponseBody> send(@Body List<GpsTrack> data);
}
