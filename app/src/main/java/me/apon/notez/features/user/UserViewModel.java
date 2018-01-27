package me.apon.notez.features.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.apon.notez.data.model.BaseResponse;
import me.apon.notez.data.model.Login;
import me.apon.notez.data.model.Response;
import me.apon.notez.data.model.SyncState;
import me.apon.notez.data.model.User;
import me.apon.notez.network.NConsumer;
import me.apon.notez.network.NObserver;
import me.apon.notez.network.RetrofitClient;
import me.apon.notez.network.api.UserApi;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class UserViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;

    public UserViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    //登录
    private MutableLiveData<Response> loginResponse = new MutableLiveData<>();

    public LiveData<Response> loginResponse() {
        return loginResponse;
    }

    public void Login(String email, String pwd){
        RetrofitClient.service(UserApi.class)
                .login(email,pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(loginResponse,compositeDisposable))
                .subscribe(new NObserver<Login>(loginResponse));
    }

    //注销
    private MutableLiveData<Response> logoutResponse = new MutableLiveData<>();

    public LiveData<Response> logoutResponse() {
        return logoutResponse;
    }

    public void logout(String token){
        RetrofitClient.service(UserApi.class)
                .logout(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(logoutResponse,compositeDisposable))
                .subscribe(new NObserver<BaseResponse>(logoutResponse));
    }

    //注册
    private MutableLiveData<Response> registerResponse = new MutableLiveData<>();

    public LiveData<Response> registerResponse() {
        return registerResponse;
    }

    public void register(String email,String pwd){
        RetrofitClient.service(UserApi.class)
                .register(email,pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(registerResponse,compositeDisposable))
                .subscribe(new NObserver<BaseResponse>(registerResponse));
    }

    //用户信息
    private MutableLiveData<Response> userInfoResponse = new MutableLiveData<>();

    public LiveData<Response> userInfoResponse() {
        return userInfoResponse;
    }

    public void userInfo(String userid){
        RetrofitClient.service(UserApi.class)
                .userInfo(userid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(userInfoResponse,compositeDisposable))
                .subscribe(new NObserver<User>(userInfoResponse));
    }

    //修改用户名
    private MutableLiveData<Response> updateNameResponse = new MutableLiveData<>();

    public LiveData<Response> updateNameResponse() {
        return updateNameResponse;
    }

    public void updateName(String userName){
        RetrofitClient.service(UserApi.class)
                .updateUserName(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(updateNameResponse,compositeDisposable))
                .subscribe(new NObserver<BaseResponse>(updateNameResponse));
    }

    //修改密码
    private MutableLiveData<Response> updatePwdResponse = new MutableLiveData<>();

    public LiveData<Response> updatePwdResponse() {
        return updatePwdResponse;
    }

    public void updatePwd(String oldpwd,String pwd){
        RetrofitClient.service(UserApi.class)
                .updatePwd(oldpwd,pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(updatePwdResponse,compositeDisposable))
                .subscribe(new NObserver<BaseResponse>(updatePwdResponse));
    }


    //获取最新同步状态
    private MutableLiveData<Response> syncStateResponse = new MutableLiveData<>();

    public LiveData<Response> syncStateResponse() {
        return syncStateResponse;
    }

    public void getSyncState(){
        RetrofitClient.service(UserApi.class)
                .getSyncState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(syncStateResponse,compositeDisposable))
                .subscribe(new NObserver<SyncState>(syncStateResponse));
    }
}
