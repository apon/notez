package me.apon.notez.features.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import me.apon.notez.R;
import me.apon.notez.app.BaseFragment;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.features.home.RecentNoteFragment.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-02-02
 */

public class RecentNoteFragment extends BaseFragment {

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
        initView();
    }

    private void initView() {

    }
}
