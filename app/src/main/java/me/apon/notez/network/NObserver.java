package me.apon.notez.network;

import android.arch.lifecycle.MutableLiveData;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import me.apon.notez.data.model.Response;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/27.
 */

public class NObserver<T> implements Observer<T> {

    private MutableLiveData<Response> responseMutableLiveData;


    public NObserver(MutableLiveData<Response> responseMutableLiveData) {
        this.responseMutableLiveData = responseMutableLiveData;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        //responseMutableLiveData.setValue(Response.loading());
    }

    @Override
    public void onNext(@NonNull T t) {
        responseMutableLiveData.setValue(Response.success(t));
    }

    @Override
    public void onError(@NonNull Throwable e) {
        responseMutableLiveData.setValue(Response.error(e));
    }

    @Override
    public void onComplete() {

    }
}
