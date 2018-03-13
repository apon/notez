package me.apon.notez.data.network.api;

import io.reactivex.Observable;
import me.apon.notez.data.model.BaseResponse;
import me.apon.notez.data.model.Login;
import me.apon.notez.data.model.SyncState;
import me.apon.notez.data.model.User;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public interface UserApi {

    @GET("/api/auth/login")
    Observable<Login> login(@Query("email")String email,@Query("pwd")String pwd);

    @GET("/api/auth/logout")
    Observable<BaseResponse> logout();

    @POST("/api/auth/register")
    Observable<BaseResponse> register(@Query("email") String email,@Query("pwd") String pwd);

    @GET("/api/user/info")
    Observable<User> userInfo(@Query("userId")String userId);

    @FormUrlEncoded
    @POST("/api/user/updateUsername")
    Observable<BaseResponse> updateUserName(@Field("username") String username);

    @FormUrlEncoded
    @POST("/api/user/updatePwd")
    Observable<BaseResponse> updatePwd(@Field("oldPwd")String oldPwd,@Field("pwd")String pwd);

    @GET("/api/user/getSyncState")
    Observable<SyncState> getSyncState();

}
