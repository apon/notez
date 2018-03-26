package me.apon.notez.features.user;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.apon.notez.app.NoteApp;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.database.dao.AccountDao;
import me.apon.notez.data.model.Account;
import me.apon.notez.data.model.BaseResponse;
import me.apon.notez.data.model.Login;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Response;
import me.apon.notez.data.model.SyncState;
import me.apon.notez.data.model.User;
import me.apon.notez.data.network.NConsumer;
import me.apon.notez.data.network.NObserver;
import me.apon.notez.data.network.RetrofitClient;
import me.apon.notez.data.network.api.UserApi;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class UserViewModel extends AndroidViewModel {

    private AccountDao accountDataSource;

    private CompositeDisposable compositeDisposable;

    public UserViewModel(Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        this.accountDataSource = AppDatabase.getInstance(application).accountDao();
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
        if (TextUtils.isEmpty(email)){
            loginResponse.setValue(Response.error(new Exception("邮箱地址不能为空！")));
            return;
        }
        if (TextUtils.isEmpty(pwd)){
            loginResponse.setValue(Response.error(new Exception("密码不能为空！")));
            return;
        }
        RetrofitClient.service(UserApi.class)
                .login(email,pwd)
                .flatMap(new Function<Login, ObservableSource<Account>>() {
                    @Override
                    public ObservableSource<Account> apply(@NonNull Login login) throws Exception {
                        //登录信息保存到数据库
                        Account localAccount = accountDataSource.getAccount(login.getEmail());
                        if (localAccount == null) {
                            localAccount = new Account();
                        }
                        localAccount.setEmail(login.getEmail());
                        localAccount.setToken(login.getToken());
                        localAccount.setUserId(login.getUserId());
                        localAccount.setUsername(login.getUsername());
                        accountDataSource.addAccount(localAccount);
                        return Observable.fromArray(localAccount);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(loginResponse,compositeDisposable))
                .subscribe(new NObserver<Account>(loginResponse));
    }

    //注销
    private MutableLiveData<Response> logoutResponse = new MutableLiveData<>();

    public LiveData<Response> logoutResponse() {
        return logoutResponse;
    }

    public void logout(){

        RetrofitClient.service(UserApi.class)
                .logout()
                .flatMap(new Function<BaseResponse, ObservableSource<BaseResponse>>() {
                    @Override
                    public ObservableSource<BaseResponse> apply(@NonNull BaseResponse baseResponse) throws Exception {
                        Account account = accountDataSource.getCurrent();
                        String token = account.getToken();
                        accountDataSource.deleteAccout(token);//删除本地用户信息
                        return Observable.fromArray(baseResponse);
                    }
                })
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
        if (TextUtils.isEmpty(email)){
            registerResponse.setValue(Response.error(new Exception("邮箱地址不能为空！")));
            return;
        }
        if (TextUtils.isEmpty(pwd)){
            registerResponse.setValue(Response.error(new Exception("密码不能为空！")));
            return;
        }
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
                .flatMap(new Function<User, ObservableSource<Account>>() {
                    @Override
                    public ObservableSource<Account> apply(@NonNull User user) throws Exception {
                        //用户信息保存到数据库
                        Account localAccount = accountDataSource.getAccount(user.getEmail());
                        if (localAccount == null) {
                            localAccount = new Account();
                        }
                        localAccount.setEmail(user.getEmail());
                        localAccount.setUserId(user.getUserId());
                        localAccount.setUsername(user.getUsername());
                        localAccount.setLogo(user.getLogo());
                        localAccount.setVerified(user.isVerified());
                        accountDataSource.addAccount(localAccount);
                        return Observable.fromArray(localAccount);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(userInfoResponse,compositeDisposable))
                .subscribe(new NObserver<Account>(userInfoResponse));
    }

    //修改用户名
    private MutableLiveData<Response> updateNameResponse = new MutableLiveData<>();

    public LiveData<Response> updateNameResponse() {
        return updateNameResponse;
    }

    public void updateName(final String userName){
        if (TextUtils.isEmpty(userName)){
            updateNameResponse.setValue(Response.error(new Exception("用户名不能为空！")));
            return;
        }
        RetrofitClient.service(UserApi.class)
                .updateUserName(userName)
                .flatMap(new Function<BaseResponse, ObservableSource<Account>>() {
                    @Override
                    public ObservableSource<Account> apply(@NonNull BaseResponse baseResponse) throws Exception {
                        Account localAccount = accountDataSource.getCurrent();//修改成功后修改本地数据
                        localAccount.setUsername(userName);
                        accountDataSource.addAccount(localAccount);
                        return Observable.fromArray(localAccount);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(updateNameResponse,compositeDisposable))
                .subscribe(new NObserver<Account>(updateNameResponse));
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
