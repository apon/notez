package me.apon.notez.features.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
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

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */

public class MainViewModel extends ViewModel {
    private AppDatabase appDatabase;

    private CompositeDisposable compositeDisposable;

    public MainViewModel(AppDatabase appDatabase) {
        compositeDisposable = new CompositeDisposable();
        this.appDatabase = appDatabase;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
    //从本地获取笔记本数据
    private MutableLiveData<Response> noteBooksResponse = new MutableLiveData<>();

    public LiveData<Response> noteBooksResponse() {
        return noteBooksResponse;
    }

    public void getNoteBooks(){
        appDatabase.noteBookDao().getAllNoteBook()
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


    //从网络获取笔记本数据并保存到本地
    private MutableLiveData<Response> syncNotebooksResponse = new MutableLiveData<>();

    public LiveData<Response> syncNotebooksResponse() {
        return syncNotebooksResponse;
    }

    public void getSyncNotebooks(){
        Account account = appDatabase.accountDao().getCurrent();
        if (account==null){
            return;
        }
        RetrofitClient.service(NotebookApi.class)
                .getSyncNotebooks(account.getNotebookUsn(),2)
                .flatMap(new Function<List<Notebook>, ObservableSource<List<Notebook>>>() {
                    @Override
                    public ObservableSource<List<Notebook>> apply(@NonNull List<Notebook> notebooks) throws Exception {

                        int notebookUsn = 0;
                        for (Notebook remoteNotebook : notebooks) {
                            Notebook localNotebook = appDatabase.noteBookDao().getByServerId(remoteNotebook.getNotebookId());
                            if (localNotebook == null) {
                                appDatabase.noteBookDao().Insert(remoteNotebook);
                            } else {
                                remoteNotebook.setId(localNotebook.getId());
                                remoteNotebook.setDirty(false);
                                appDatabase.noteBookDao().Insert(remoteNotebook);
                            }
                            notebookUsn = remoteNotebook.getUsn();
                        }
                        if (notebookUsn!=0){
                            Account account = appDatabase.accountDao().getCurrent();
                            account.setNotebookUsn(notebookUsn);
                            appDatabase.accountDao().addAccount(account);
                        }

                        return Observable.fromArray(notebooks);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(syncNotebooksResponse,compositeDisposable))
                .subscribe(new NObserver<List<Notebook>>(syncNotebooksResponse));
    }

    //从网络获取笔记数据并保存到本地
    private MutableLiveData<Response> syncNotesResponse = new MutableLiveData<>();

    public LiveData<Response> syncNotesResponse() {
        return syncNotesResponse;
    }

    public void getSyncNotes(){
        Account account = appDatabase.accountDao().getCurrent();
        if (account==null){
            return;
        }
        RetrofitClient.service(NoteApi.class)
                .getSyncNotes(account.getNoteUsn(),20)
                .flatMap(new Function<List<Note>, ObservableSource<List<Note>>>() {
                    @Override
                    public ObservableSource<List<Note>> apply(@NonNull List<Note> notes) throws Exception {
                        int noteUsn = 0;
                        for (Note noteMeta : notes) {
                            Note localNote = appDatabase.noteDao().getByServerId(noteMeta.getNoteId());
                            noteUsn = noteMeta.getUsn();
                            if (localNote == null) {
                                appDatabase.noteDao().Insert(noteMeta);
                            } else {
                                long id = localNote.getId();
                                noteMeta.setId(id);
                                appDatabase.noteDao().Insert(noteMeta);
                            }
                        }
                        if(noteUsn!=0){
                            Account account = appDatabase.accountDao().getCurrent();
                            account.setNoteUsn(noteUsn);
                            appDatabase.accountDao().addAccount(account);
                        }

                        return Observable.fromArray(notes);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(syncNotesResponse,compositeDisposable))
                .subscribe(new NObserver<List<Note>>(syncNotesResponse));
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
}
