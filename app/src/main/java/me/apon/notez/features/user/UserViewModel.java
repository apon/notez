package me.apon.notez.features.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.apon.notez.data.model.BaseResponse;
import me.apon.notez.data.model.Login;
import me.apon.notez.data.model.Response;
import me.apon.notez.data.model.SyncState;
import me.apon.notez.data.model.User;
import me.apon.notez.network.RetrofitClient;
import me.apon.notez.network.api.UserApi;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class UserViewModel extends ViewModel {



    //登录
    private MutableLiveData<Response> loginResponse = new MutableLiveData<>();

    public LiveData<Response> loginResponse() {
        return loginResponse;
    }

    public void Login(String email, String pwd){
        RetrofitClient.service(UserApi.class)
                .login(email,pwd)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        loginResponse.setValue(Response.loading());
                    }
                })
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Login login) {
                        loginResponse.setValue(Response.success(login));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        loginResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //注销
    private MutableLiveData<Response> logoutResponse = new MutableLiveData<>();

    public LiveData<Response> logoutResponse() {
        return logoutResponse;
    }

    public void logout(String token){
        RetrofitClient.service(UserApi.class)
                .logout(token)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        logoutResponse.setValue(Response.loading());
                    }
                })
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseResponse baseResponse) {
                        logoutResponse.setValue(Response.success(baseResponse));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        logoutResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //注册
    private MutableLiveData<Response> registerResponse = new MutableLiveData<>();

    public LiveData<Response> registerResponse() {
        return registerResponse;
    }

    public void register(String email,String pwd){
        RetrofitClient.service(UserApi.class)
                .register(email,pwd)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        registerResponse.setValue(Response.loading());
                    }
                })
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseResponse baseResponse) {
                        registerResponse.setValue(Response.success(baseResponse));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        registerResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //用户信息
    private MutableLiveData<Response> userInfoResponse = new MutableLiveData<>();

    public LiveData<Response> userInfoResponse() {
        return userInfoResponse;
    }

    public void userInfo(String userid){
        RetrofitClient.service(UserApi.class)
                .userInfo(userid)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        userInfoResponse.setValue(Response.loading());
                    }
                })
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull User user) {
                        registerResponse.setValue(Response.success(user));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        registerResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //修改用户名
    private MutableLiveData<Response> updateNameResponse = new MutableLiveData<>();

    public LiveData<Response> updateNameResponse() {
        return updateNameResponse;
    }

    public void updateName(String userName){
        RetrofitClient.service(UserApi.class)
                .updateUserName(userName)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        updateNameResponse.setValue(Response.loading());
                    }
                })
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseResponse baseResponse) {
                        updateNameResponse.setValue(Response.success(baseResponse));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        updateNameResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //修改密码
    private MutableLiveData<Response> updatePwdResponse = new MutableLiveData<>();

    public LiveData<Response> updatePwdResponse() {
        return updatePwdResponse;
    }

    public void updatePwd(String oldpwd,String pwd){
        RetrofitClient.service(UserApi.class)
                .updatePwd(oldpwd,pwd)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        updatePwdResponse.setValue(Response.loading());
                    }
                })
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseResponse baseResponse) {
                        updatePwdResponse.setValue(Response.success(baseResponse));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        updatePwdResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //获取最新同步状态
    private MutableLiveData<Response> syncStateResponse = new MutableLiveData<>();

    public LiveData<Response> syncStateResponse() {
        return syncStateResponse;
    }

    public void getSyncState(){
        RetrofitClient.service(UserApi.class)
                .getSyncState()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        syncStateResponse.setValue(Response.loading());
                    }
                })
                .subscribe(new Observer<SyncState>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull SyncState syncState) {
                        syncStateResponse.setValue(Response.success(syncState));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        syncStateResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
