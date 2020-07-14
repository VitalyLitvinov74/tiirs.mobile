package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.MediaFile;

public interface IMediaFile {
    @Multipart
    @POST("/media-file/upload")
    Call<ResponseBody> upload(@Part("descr") RequestBody descr, @Part List<MultipartBody.Part> files);

    @GET("/media-file")
    Call<List<MediaFile>> get();

    @GET("/media-file")
    Call<List<MediaFile>> get(@Query("entity") String entityUuid);

    @GET("/media-file")
    Call<String> getUrl(@Query("media_uuid") String mediaFileUuid, @Query("url") String url);

    @GET("/media-file")
    Call<List<MediaFile>> get(@Query("id[]") List<String> id, @Query("uuid[]") List<String> uuid);
}
