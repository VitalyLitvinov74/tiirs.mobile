package ru.toir.mobile.rest.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import ru.toir.mobile.db.realm.User;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface IUserService {
    @GET("/api/account/me")
    Call<User> user();
}
