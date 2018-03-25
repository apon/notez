package me.apon.notez.features.note;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.apon.notez.R;
import me.apon.notez.app.BaseFragment;
import me.apon.notez.data.model.Events;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.Response;
import me.apon.notez.features.home.MainViewModel;
import me.apon.notez.features.home.RecentNoteFragment;
import me.apon.notez.utils.RxBus;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/20.
 */

public class NoteCategoryFragment extends BaseFragment {
    //private final CompositeDisposable disposables = new CompositeDisposable();
    private Unbinder unbinder;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private MainViewModel mMainViewModel;
    private MPagerAdapter mPagerAdapter;

    public static NoteCategoryFragment newInstance() {
        
        Bundle args = new Bundle();
        
        NoteCategoryFragment fragment = new NoteCategoryFragment();
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
        View rootView = inflater.inflate(R.layout.note_category_fra, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        observeLiveData();
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //disposables.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {
        mPagerAdapter = new MPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        mMainViewModel.getNoteBooks();
    }

    private void observeLiveData() {
//        disposables.add(
//                RxBus.getInstance()
//                .toObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) throws Exception {
//                        if (o instanceof Events.NoteBookEvent) {//notebook同步成功
//                            mMainViewModel.getNoteBooks();
//                        }
//                    }
//                })
//        );

        mMainViewModel.noteBooksResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                noteBooksResponse(response);
            }
        });
        mMainViewModel.rxBusResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                Object o = response.data;
                if (o instanceof Events.NoteBookEvent) {//notebook同步成功
                    mMainViewModel.getNoteBooks();
                }
            }
        });
    }

    private void noteBooksResponse(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                List<Notebook> notebooks = (List<Notebook>) response.data;
                initPaper(notebooks);
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

    private void initPaper(List<Notebook> notebooks){
        if (notebooks==null){
            return;
        }
        mPagerAdapter.clearedAll();
        for (Notebook n:notebooks) {
            mPagerAdapter.addFragment(NoteListFragment.newInstance(n.getNotebookId()),n.getTitle());
        }
        mPagerAdapter.notifyDataSetChanged();
    }

    class MPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        public void clearedAll() {
            fragments.clear();
            titles.clear();
        }

        public int getTitlePosition(String title) {
            return titles.indexOf(title);
        }

        public MPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }



}
