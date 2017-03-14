package ru.toir.mobile.rest.interfaces;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

/**
 * @author Dmitriy Logachov
 *         Created by koputo on 3/13/17.
 */

public interface IFileDownload {
    @GET
    Call<ResponseBody> getFile(@Url String url);
}
