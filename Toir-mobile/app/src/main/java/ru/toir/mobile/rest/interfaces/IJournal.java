package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import ru.toir.mobile.db.realm.Journal;
import ru.toir.mobile.rest.ToirAPIResponse;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 09.02.17.
 */

public interface IJournal {
    @POST("/journal/create")
    Call<ToirAPIResponse> sendJournal(@Header("Authorization") String token, @Body List<Journal> data);
}
