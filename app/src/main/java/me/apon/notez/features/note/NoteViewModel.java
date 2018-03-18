package me.apon.notez.features.note;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.apon.notez.app.NoteApp;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Response;
import me.apon.notez.data.network.NConsumer;
import me.apon.notez.data.network.NObserver;
import me.apon.notez.data.network.RetrofitClient;
import me.apon.notez.data.network.api.NoteApi;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/16.
 */

public class NoteViewModel extends ViewModel{
    private AppDatabase appDatabase;

    private CompositeDisposable compositeDisposable;

    public NoteViewModel() {
        compositeDisposable = new CompositeDisposable();
        this.appDatabase = AppDatabase.getInstance(NoteApp.app);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
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
}
