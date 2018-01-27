package me.apon.notez.network;

import android.arch.lifecycle.MutableLiveData;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.apon.notez.data.model.Response;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/27.
 */

public class NConsumer implements Consumer<Disposable> {

    private MutableLiveData<Response> responseMutableLiveData;

    private CompositeDisposable compositeDisposable;

    public NConsumer(MutableLiveData<Response> responseMutableLiveData, CompositeDisposable compositeDisposable) {
        this.responseMutableLiveData = responseMutableLiveData;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void accept(Disposable disposable) throws Exception {
        responseMutableLiveData.setValue(Response.loading());
        compositeDisposable.add(disposable);
    }
}
