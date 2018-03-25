package me.apon.notez.features.note;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.apon.notez.R;
import me.apon.notez.app.BaseFragment;
import me.apon.notez.data.model.Events;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Response;
import me.apon.notez.data.model.SyncEvent;
import me.apon.notez.features.home.MainViewModel;
import me.apon.notez.features.note.adapter.NoteListAdapter;
import me.apon.notez.utils.RxBus;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */

public class NoteListFragment extends BaseFragment {
    private final CompositeDisposable disposables = new CompositeDisposable();
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private NoteViewModel mNoteViewModel;
    private NoteListAdapter mNoteListAdapter;
    private String bookId;
    public static NoteListFragment newInstance(String bookId) {
        
        Bundle args = new Bundle();
        args.putString("bookid",bookId);
        NoteListFragment fragment = new NoteListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_list_fra, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        bookId = getArguments().getString("bookid");
        observeLiveData();
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private void initView() {
        mNoteListAdapter = new NoteListAdapter();
        recyclerView.setAdapter(mNoteListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mNoteListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Note note = (Note) adapter.getItem(position);
                NotePreviewActivity.start(getActivity(),note.getNoteId());
            }
        });
        mNoteListAdapter.getData().clear();
        if (TextUtils.isEmpty(bookId)){
            mNoteViewModel.getNotes();
        }else {
            mNoteViewModel.getNotesByBookId(bookId);
        }
    }

    private void observeLiveData(){
        mNoteViewModel.notesResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                notesResponse(response);
            }
        });
        mNoteViewModel.rxBusResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                Object o = response.data;
                if (o instanceof Events.NoteEvent){//note同步完成
                    mNoteListAdapter.getData().clear();
                    if (TextUtils.isEmpty(bookId)){
                        mNoteViewModel.getNotes();
                    }else {
                        mNoteViewModel.getNotesByBookId(bookId);
                    }
                }
            }
        });
//        disposables.add(RxBus.getInstance()
//                .toObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) throws Exception {
//                        if (o instanceof Events.NoteEvent){//note同步完成
//                            mNoteListAdapter.getData().clear();
//                            if (TextUtils.isEmpty(bookId)){
//                                mMainViewModel.getNotes();
//                            }else {
//                                mMainViewModel.getNotesByBookId(bookId);
//                            }
//                        }
//                    }
//                })
//        );
    }

    private void notesResponse(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                List<Note> notes = (List<Note>) response.data;
                mNoteListAdapter.addData(notes);
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

}
