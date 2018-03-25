package me.apon.notez.features.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.model.Account;
import me.apon.notez.data.model.Events;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.SyncEvent;
import me.apon.notez.data.network.RetrofitClient;
import me.apon.notez.data.network.api.NoteApi;
import me.apon.notez.data.network.api.NotebookApi;
import me.apon.notez.utils.RxBus;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/14.
 */

public class SyncService extends Service {
    private static final String TAG = "SyncService";

    private AppDatabase appDatabase;
    private boolean isSyncNotesDone;
    private boolean isSyncNotebooksDone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = AppDatabase.getInstance(getApplicationContext());
        Log.d(TAG,"=========onCreate=========");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startSync();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startSync(){
        Log.d(TAG,"=========startSync=========");
        isSyncNotesDone = false;
        isSyncNotebooksDone = false;
        getSyncNotes();
        getSyncNotebooks();
    }
    public void getSyncNotes(){
        Account account = appDatabase.accountDao().getCurrent();
        if (account==null){
            return;
        }
        RetrofitClient.service(NoteApi.class)
                .getSyncNotes(account.getNoteUsn(),20)
                .flatMap(new Function<List<Note>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(@NonNull List<Note> notes) throws Exception {
                        int noteUsn = 0;
                        for (Note noteMeta : notes) {
                            Note localNote = appDatabase.noteDao().getByServerId(noteMeta.getNoteId());
                            noteUsn = noteMeta.getUsn();
                            if (localNote == null) {
                                noteMeta.updateTime();
                                appDatabase.noteDao().Insert(noteMeta);
                            } else {
                                long id = localNote.getId();
                                noteMeta.setId(id);
                                noteMeta.updateTime();
                                appDatabase.noteDao().Insert(noteMeta);
                            }
                        }
                        if(noteUsn!=0){
                            Account account = appDatabase.accountDao().getCurrent();
                            account.setNoteUsn(noteUsn);
                            appDatabase.accountDao().addAccount(account);
                        }

                        return Observable.fromArray(notes.size());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        if (integer==20){//循环
                            getSyncNotes();
                        }else{
                            isSyncNotesDone = true;
                            if (integer>0){
                                RxBus.getInstance().send(new Events.NoteEvent());
                            }
                        }
                        Log.d(TAG,"=========SyncNotes size========="+integer);
                        if (isSyncNotebooksDone&&isSyncNotesDone){
                            stopSelf();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        if (integer==20){//循环
//                            getSyncNotes();
//                        }else{
//                            isSyncNotesDone = true;
//                            if (integer>0){
//                                //RxBus.getInstance().send(new Events.NoteEvent());
//                            }
//                        }
//                        Log.d(TAG,"=========SyncNotes size========="+integer);
//                        if (isSyncNotebooksDone&&isSyncNotesDone){
//                            stopSelf();
//                        }
//                    }
//                });
    }

    public void getSyncNotebooks(){
        Account account = appDatabase.accountDao().getCurrent();
        if (account==null){
            return;
        }
        RetrofitClient.service(NotebookApi.class)
                .getSyncNotebooks(account.getNotebookUsn(),20)
                .flatMap(new Function<List<Notebook>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(@NonNull List<Notebook> notebooks) throws Exception {

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

                        return Observable.fromArray(notebooks.size());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        if (integer==20){
                            getSyncNotebooks();
                        }else {
                            isSyncNotebooksDone = true;
                            if (integer>0){
                                RxBus.getInstance().send(new Events.NoteBookEvent());
                            }
                        }
                        Log.d(TAG,"=========SyncNotebooks size========="+integer);
                        if (isSyncNotebooksDone&&isSyncNotesDone){
                            stopSelf();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        if (integer==20){
//                            getSyncNotebooks();
//                        }else {
//                            isSyncNotebooksDone = true;
//                            if (integer>0){
//                                //RxBus.getInstance().send(new Events.NoteBookEvent());
//                            }
//                        }
//                        Log.d(TAG,"=========SyncNotebooks size========="+integer);
//                        if (isSyncNotebooksDone&&isSyncNotesDone){
//                            stopSelf();
//                        }
//
//                    }
//                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"=========onDestroy=========");
    }
}
