package ru.toir.mobile.rest;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import ru.toir.mobile.db.realm.User;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface IUserService {
    @GET("/api/account/me")
    Call<User> user(@Header("Authorization") String token);
}
