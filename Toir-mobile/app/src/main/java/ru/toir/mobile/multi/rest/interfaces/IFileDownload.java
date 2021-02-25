package ru.toir.mobile.multi.rest.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * @author Dmitriy Logachov
 *         Created by koputo on 3/13/17.
 */

public interface IFileDownload {
    @GET
    Call<ResponseBody> get(@Url String url);
}
