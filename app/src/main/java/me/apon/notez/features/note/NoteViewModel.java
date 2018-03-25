package me.apon.notez.features.note;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
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
import me.apon.notez.data.model.Events;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.Response;
import me.apon.notez.data.network.NConsumer;
import me.apon.notez.data.network.NObserver;
import me.apon.notez.data.network.RetrofitClient;
import me.apon.notez.data.network.api.NoteApi;
import me.apon.notez.utils.RxBus;
import me.apon.notez.utils.TimeUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/16.
 */

public class NoteViewModel extends ViewModel{
    private AppDatabase appDatabase;

    private CompositeDisposable compositeDisposable;

    public NoteViewModel() {
        compositeDisposable = new CompositeDisposable();
        this.appDatabase = AppDatabase.getInstance(NoteApp.app);
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

    private MutableLiveData<Response> noteAndContentResponse = new MutableLiveData<>();

    public LiveData<Response> noteAndContentResponse() {
        return noteAndContentResponse;
    }

    public void getNoteAndContent(String noteid){
        RetrofitClient.service(NoteApi.class)
                .getNoteAndContent(noteid)
                .flatMap(new Function<Note, ObservableSource<Note>>() {
                    @Override
                    public ObservableSource<Note> apply(@NonNull Note note) throws Exception {
                        Note localNote = appDatabase.noteDao().getByServerId(note.getNoteId());
                        if (localNote == null) {
                            note.updateTime();
                            appDatabase.noteDao().Insert(note);
                        } else {
                            long id = localNote.getId();
                            note.setId(id);
                            note.updateTime();
                            appDatabase.noteDao().Insert(note);
                        }
                        return Observable.fromArray(note);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(noteAndContentResponse,compositeDisposable))
                .subscribe(new NObserver<Note>(noteAndContentResponse));
    }

    private MutableLiveData<Response> saveNoteResponse = new MutableLiveData<>();

    public LiveData<Response> saveNoteResponse() {
        return saveNoteResponse;
    }

    public void saveNote(Note note){
        final String content = note.getContent();
        if (TextUtils.isEmpty(content)){
            saveNoteResponse.setValue(Response.error(new Exception("笔记内容不能为空！")));
            return;
        }
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        requestBodyMap.put("NotebookId", createPartFromString(note.getNotebookId()));
        requestBodyMap.put("Title", createPartFromString(note.getTitle()));
        requestBodyMap.put("Content", createPartFromString(content));
        requestBodyMap.put("IsMarkdown", createPartFromString(note.isMarkdown()?"1":"0"));
        requestBodyMap.put("IsBlog", createPartFromString(note.isBlog()?"1":"0"));
        requestBodyMap.put("IsTrash", createPartFromString(note.isTrash()?"1":"0"));
        requestBodyMap.put("CreatedTime", createPartFromString(TimeUtils.toServerTime(note.getCreatedTimeInMills())));
        requestBodyMap.put("UpdatedTime", createPartFromString(TimeUtils.toServerTime(note.getUpdatedTimeInMills())));

        RetrofitClient.service(NoteApi.class)
                .add(requestBodyMap,null)
                .flatMap(new Function<Note, ObservableSource<Note>>() {
                    @Override
                    public ObservableSource<Note> apply(@NonNull Note note) throws Exception {
                        //保存到本地
                        Note localNote = appDatabase.noteDao().getByServerId(note.getNoteId());
                        if (localNote == null) {
                            note.setContent(content);
                            note.updateTime();
                            appDatabase.noteDao().Insert(note);
                        } else {
                            long id = localNote.getId();
                            note.setId(id);
                            note.setContent(content);
                            note.updateTime();
                            appDatabase.noteDao().Insert(note);
                        }
                        return Observable.fromArray(note);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(saveNoteResponse,compositeDisposable))
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Note note) {
                        saveNoteResponse.setValue(Response.success(note));
                        RxBus.getInstance().send(new Events.NoteEvent());//有笔记更新
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        saveNoteResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateNote(Note note){
        final String content = note.getContent();

        if (TextUtils.isEmpty(content)){
            saveNoteResponse.setValue(Response.error(new Exception("笔记内容不能为空！")));
            return;
        }
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        requestBodyMap.put("NoteId", createPartFromString(note.getNoteId()));
        requestBodyMap.put("Usn", createPartFromString(String.valueOf(note.getUsn())));
        requestBodyMap.put("NotebookId", createPartFromString(note.getNotebookId()));
        requestBodyMap.put("Title", createPartFromString(note.getTitle()));
        requestBodyMap.put("Content", createPartFromString(content));
        requestBodyMap.put("IsMarkdown", createPartFromString(note.isMarkdown()?"1":"0"));
        requestBodyMap.put("IsBlog", createPartFromString(note.isBlog()?"1":"0"));
        requestBodyMap.put("IsTrash", createPartFromString(note.isTrash()?"1":"0"));
        //requestBodyMap.put("CreatedTime", createPartFromString(TimeUtils.toServerTime(note.getCreatedTimeInMills())));
        requestBodyMap.put("UpdatedTime", createPartFromString(TimeUtils.toServerTime(note.getUpdatedTimeInMills())));

        RetrofitClient.service(NoteApi.class)
                .update(requestBodyMap,null)
                .flatMap(new Function<Note, ObservableSource<Note>>() {
                    @Override
                    public ObservableSource<Note> apply(@NonNull Note note) throws Exception {
                        //保存到本地
                        Note localNote = appDatabase.noteDao().getByServerId(note.getNoteId());
                        if (localNote == null) {
                            note.setContent(content);
                            note.updateTime();
                            appDatabase.noteDao().Insert(note);
                        } else {
                            long id = localNote.getId();
                            note.setId(id);
                            note.setContent(content);
                            note.updateTime();
                            appDatabase.noteDao().Insert(note);
                        }
                        return Observable.fromArray(note);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(saveNoteResponse,compositeDisposable))
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Note note) {
                        saveNoteResponse.setValue(Response.success(note));
                        RxBus.getInstance().send(new Events.NoteEvent());//有笔记更新
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        saveNoteResponse.setValue(Response.error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private  RequestBody createPartFromString(String content) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), content);
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
    private MutableLiveData<Response> noteResponse = new MutableLiveData<>();

    public LiveData<Response> noteResponse() {
        return noteResponse;
    }

    public void getNoteByServerId(String noteid){

        appDatabase.noteDao().getByServerIdX(noteid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new NConsumer(noteResponse,compositeDisposable))
                .subscribe(new SingleObserver<Note>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Note notes) {
                        noteResponse.setValue(Response.success(notes));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        noteResponse.setValue(Response.error(e));
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

}
