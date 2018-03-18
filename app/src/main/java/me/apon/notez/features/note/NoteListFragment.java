package me.apon.notez.features.note;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.apon.notez.R;
import me.apon.notez.app.BaseFragment;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Response;
import me.apon.notez.features.home.MainViewModel;
import me.apon.notez.features.note.adapter.NoteListAdapter;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */

public class NoteListFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MainViewModel mMainViewModel;
    private NoteListAdapter mNoteListAdapter;

    public static NoteListFragment newInstance() {
        
        Bundle args = new Bundle();
        
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
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        observeLiveData();
        initView();
    }

    private void initView() {
        mNoteListAdapter = new NoteListAdapter();
        recyclerView.setAdapter(mNoteListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNoteListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Note note = (Note) adapter.getItem(position);
                NotePreviewActivity.start(getActivity(),note.getNoteId());
            }
        });

    }

    private void observeLiveData(){
        mMainViewModel.notesResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                notesResponse(response);
            }
        });
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
