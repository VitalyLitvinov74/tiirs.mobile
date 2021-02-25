package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.toir.mobile.multi.db.realm.Journal;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 09.02.17.
 */

public interface IJournal {
    @POST("/journal/create")
    Call<ResponseBody> send(@Body List<Journal> data);
}
