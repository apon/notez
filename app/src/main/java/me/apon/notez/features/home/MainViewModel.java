package me.apon.notez.features.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.database.Cursor;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.apon.notez.app.NoteApp;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.model.Account;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.Response;
import me.apon.notez.data.network.NConsumer;
import me.apon.notez.data.network.NObserver;
import me.apon.notez.data.network.RetrofitClient;
import me.apon.notez.data.network.api.NoteApi;
import me.apon.notez.data.network.api.NotebookApi;
import me.apon.notez.utils.RxBus;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */

public class MainViewModel extends AndroidViewModel {
    private AppDatabase appDatabase;

    private CompositeDisposable compositeDisposable;

    public MainViewModel(Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        this.appDatabase = AppDatabase.getInstance(application);
        initRxBus();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    //RxBus
    private MutableLiveData<Response> rxBusResponse = new MutableLiveData<>();

    public LiveData<Response> rxBusResponse() {
        return rxBusResponse;
    }

    private void initRxBus(){
        compositeDisposable.add(RxBus.getInstance()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        rxBusResponse.setValue(Response.success(o));
                    }
                }));

    }

    //从本地获取笔记本数据
    private MutableLiveData<Response> noteBooksResponse = new MutableLiveData<>();

    public LiveData<Response> noteBooksResponse() {
        return noteBooksResponse;
    }

    public void getNoteBooks(){
        Account account = appDatabase.accountDao().getCurrent();
        if (account==null){
            return;
        }
        appDatabase.noteBookDao().getAllNoteBook(account.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(noteBooksResponse,compositeDisposable))
                .subscribe(new SingleObserver<List<Notebook>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<Notebook> notebooks) {
                        noteBooksResponse.setValue(Response.success(notebooks));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        noteBooksResponse.setValue(Response.error(e));
                    }
                });
    }



    //从本地获取笔记数据
    private MutableLiveData<Response> notesResponse = new MutableLiveData<>();

    public LiveData<Response> notesResponse() {
        return notesResponse;
    }

    public void getNotes(){
        Account account = appDatabase.accountDao().getCurrent();
        if (account==null){
            return;
        }
        appDatabase.noteDao().getAllNotesX(account.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(notesResponse,compositeDisposable))
                .subscribe(new SingleObserver<List<Note>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<Note> notes) {
                        notesResponse.setValue(Response.success(notes));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        notesResponse.setValue(Response.error(e));
                    }
                });
    }
    public void getNotesByBookId(String bookid){
        Account account = appDatabase.accountDao().getCurrent();
        if (account==null){
            return;
        }
        appDatabase.noteDao().getByBookIdX(account.getUserId(),bookid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(notesResponse,compositeDisposable))
                .subscribe(new SingleObserver<List<Note>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<Note> notes) {
                        notesResponse.setValue(Response.success(notes));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        notesResponse.setValue(Response.error(e));
                    }
                });
    }

    private MutableLiveData<Response> noteSearchResponse = new MutableLiveData<>();

    public LiveData<Response> noteSearchResponse() {
        return noteSearchResponse;
    }

    public void searchNotes(String key){
        Account account = appDatabase.accountDao().getCurrent();
        if (account==null){
            return;
        }
        Cursor cursor = appDatabase.noteDao().searchByTitle(account.getUserId(),key);
        noteSearchResponse.setValue(Response.success(cursor));
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe(new NConsumer(noteSearchResponse,compositeDisposable))
//                .subscribe(new SingleObserver<Cursor>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(@NonNull Cursor cursor) {
//                        noteSearchResponse.setValue(Response.success(cursor));
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        e.printStackTrace();
//                    }
//                });
    }
}
