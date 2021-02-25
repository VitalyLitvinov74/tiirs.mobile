package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Message;

/**
 * @author Olejek
 * Created by olejek on 26.09.19.
 */

public interface IMessage {
    @GET("/message")
    Call<List<Message>> get();

    @GET("/message")
    Call<List<Message>> get(@Query("changedAfter") String changedAfter);

    @GET("/message")
    Call<List<Message>> getByUuid(@Query("uuid") String uuid);

    @GET("/message")
    Call<List<Message>> getByUuid(@Query("uuid[]") String[] uuid);

    @POST("/message/upload-message")
    Call<ResponseBody> send(@Body List<Message> values);
}
