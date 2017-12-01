package ru.toir.mobile.rest.interfaces;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * @author Dmitriy Logachov
 *         Created by koputo on 3/13/17.
 */

public interface IOperationFile {
    @Multipart
    @POST("/operation-file/upload")
    Call<ResponseBody> upload(@Part("descr") RequestBody descr, @Part List<MultipartBody.Part> files);
}
