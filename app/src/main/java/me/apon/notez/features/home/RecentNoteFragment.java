package me.apon.notez.features.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import me.apon.notez.R;
import me.apon.notez.app.BaseFragment;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.Response;
import me.apon.notez.utils.ExceptionMsgUtil;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.features.home.RecentNoteFragment.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-02-02
 */

public class RecentNoteFragment extends BaseFragment {

    public static final String TAG = "RecentNoteFragment";

    MainViewModel mainViewModel;

    public static RecentNoteFragment newInstance() {

        Bundle args = new Bundle();
        RecentNoteFragment fragment = new RecentNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adviserLiveViewModel = ViewModelProviders.of(this).get(AdviserLiveViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recent_note_fra, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        observeLiveData();
    }

    private void observeLiveData() {
        mainViewModel.noteBooksResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                noteBooksResponse(response);
            }
        });
    }

    private void noteBooksResponse(Response response){
        switch (response.status) {
            case LOADING:
                Log.d(TAG,"=======LOADING=========");
                break;
            case SUCCESS:
                List<Notebook> notebooks = (List<Notebook>) response.data;
                Log.d(TAG,"=======SUCCESS========="+notebooks.size());

                break;
            case ERROR:
                Log.d(TAG,"=======ERROR=========");
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }
}
