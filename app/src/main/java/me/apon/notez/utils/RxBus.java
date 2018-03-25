package me.apon.notez.utils;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/20.
 */

public class RxBus {

    private static class SingletonHolder {
        private static final RxBus INSTANCE = new RxBus();
    }
    private PublishSubject<Object> bus;

    private RxBus() {
        bus = PublishSubject.create();;
    }

    public static final RxBus getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

}
