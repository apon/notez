package me.apon.notez.features.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.apon.notez.R;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.model.Account;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.Response;
import me.apon.notez.features.note.NoteEditorActivity;
import me.apon.notez.features.note.NoteListFragment;
import me.apon.notez.features.service.SyncService;
import me.apon.notez.features.user.LoginActivity;
import me.apon.notez.features.user.SettingActivity;
import me.apon.notez.features.user.UserViewModel;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    int position = 0;

    List<Fragment> fragmentList = new ArrayList<>();

    UserViewModel userViewModel;
    MainViewModel mainViewModel;
    SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        //////

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        observeLiveData();
        ////////
        initView();
        replace(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Account account = AppDatabase.getInstance(MainActivity.this).accountDao().getCurrent();
        initHeader(account);
        if (account!=null&& TextUtils.isEmpty(account.getLogo())){
            userViewModel.userInfo(account.getUserId());
        }

        startService(new Intent(this, SyncService.class));
        mainViewModel.getNoteBooks();
        //mainViewModel.getSyncNotebooks();//同步笔记本
        //mainViewModel.getSyncNotes();//同步笔记
        mainViewModel.getNotes();
    }

    private void initView() {
        // Set behavior of Navigation drawer
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        if (menuItem.getItemId()==R.id.menu_about){
                            AboutActivity.start(MainActivity.this);
                        }else if (menuItem.getItemId()==R.id.menu_setting){
                            SettingActivity.start(MainActivity.this);
                        }else {
                            menuItem.setChecked(true);
                            toolbar.setTitle(menuItem.getTitle());
                            switch (menuItem.getItemId()){
                                case R.id.menu_home:
                                    position = 0;
                                    break;
                                case R.id.menu_book:
                                    position = 1;
                                    break;
                                case R.id.menu_cat:
                                    position = 2;
                                    break;
                                case R.id.menu_tag:
                                    position = 3;
                                    break;
                            }
                            replace(position);
                            invalidateOptionsMenu();
                            // Closing drawer on item click
                            drawer.closeDrawers();
                        }

                        return true;
                    }
                });
        navView.getHeaderView(0).findViewById(R.id.user_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.start(MainActivity.this);
            }
        });
        // Adding Floating Action Button to bottom right of main view
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, "Hello Snackbar!",Snackbar.LENGTH_LONG).show();
                NoteEditorActivity.start(MainActivity.this);
            }
        });
        fragmentList.add(NoteListFragment.newInstance());
        fragmentList.add(RecentNoteFragment.newInstance());
        fragmentList.add(RecentNoteFragment.newInstance());
        fragmentList.add(RecentNoteFragment.newInstance());

        mSearchView = new SearchView(this);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainViewModel.searchNotes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainViewModel.searchNotes(newText);
                return false;
            }
        });

    }

    private void replace(int position){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragmentList.get(position));
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, 500, Menu.NONE, R.string.action_settings).setActionView(mSearchView).setIcon(R.drawable.ic_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (position==0){
            menu.add(0, 100, Menu.NONE, R.string.action_settings).setIcon(R.drawable.ic_home).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }else if (position==1){
            menu.add(0, 200, Menu.NONE, R.string.action_settings).setIcon(R.drawable.ic_book).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }else if (position==2){
            menu.add(0, 300, Menu.NONE, R.string.action_settings).setIcon(R.drawable.ic_list).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }else if (position==3){
            menu.add(0, 400, Menu.NONE, R.string.action_settings).setIcon(R.drawable.ic_loyalty_white).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
        }
        switch (id){
            case 100:
                Toast.makeText(this, "首页", Toast.LENGTH_SHORT).show();
                break;
            case 200:
                Toast.makeText(this, "笔记", Toast.LENGTH_SHORT).show();
                break;
            case 300:
                Toast.makeText(this, "分类", Toast.LENGTH_SHORT).show();
                break;
            case 400:
                Toast.makeText(this, "标签", Toast.LENGTH_SHORT).show();
                break;
            case 500:
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void observeLiveData(){
        userViewModel.userInfoResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                userInfoResponse(response);
            }
        });

        mainViewModel.noteBooksResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                noteBooksResponse(response);
            }
        });
        mainViewModel.notesResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                notesResponse(response);
            }
        });
        mainViewModel.noteSearchResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                noteSearchResponse(response);
            }
        });
    }

    private void noteSearchResponse(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                Log.d("MainActivit","======noteSearchResponse======");
                final Cursor cursor = (Cursor) response.data;
                if (mSearchView.getSuggestionsAdapter() == null) {
                    mSearchView.setSuggestionsAdapter(new SimpleCursorAdapter(this, R.layout.note_search_item, cursor, new String[]{"title"}, new int[]{R.id.title}));
                } else {
                    mSearchView.getSuggestionsAdapter().changeCursor(cursor);
                }
                mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                    @Override
                    public boolean onSuggestionSelect(int position) {
                        return false;
                    }

                    @Override
                    public boolean onSuggestionClick(int position) {
                        cursor.move(position);
                        String title = cursor.getString(cursor.getColumnIndex("title"));
                        Toast.makeText(MainActivity.this, ""+title, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

    private void noteBooksResponse(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                List<Notebook> notebooks = (List<Notebook>) response.data;
                Log.d("MainActivit","======笔记本从本地数据库获取======"+notebooks.size());
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

    private void notesResponse(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                List<Note> notebooks = (List<Note>) response.data;
                Log.d("MainActivit","======笔记从本地数据库获取======"+notebooks.size());
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

    private void userInfoResponse(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                Account account = (Account) response.data;
                initHeader(account);
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

    private void initHeader(Account account){
        View headerView = navView.getHeaderView(0);
        ImageView userImg = headerView.findViewById(R.id.user_img);
        TextView userName = headerView.findViewById(R.id.user_name);
        TextView userEmail = headerView.findViewById(R.id.user_email);
        if (account==null){
            userName.setText("点击登录");
            userEmail.setVisibility(View.INVISIBLE);
        }else {
            userName.setText(account.getUsername());
            userEmail.setText(account.getEmail());
            userEmail.setVisibility(View.VISIBLE);
        }

    }

}
