package me.apon.notez.features.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.apon.notez.R;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.database.dao.AccountDao;
import me.apon.notez.data.model.Account;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.Response;
import me.apon.notez.features.note.NoteEditorActivity;
import me.apon.notez.features.user.LoginActivity;
import me.apon.notez.features.user.SettingActivity;
import me.apon.notez.features.user.UserViewModel;
import me.apon.notez.features.user.UserViewModelFactory;

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
        AccountDao accountDao = AppDatabase.getInstance(this).accountDao();

        userViewModel = ViewModelProviders.of(this,new UserViewModelFactory(accountDao)).get(UserViewModel.class);
        mainViewModel = ViewModelProviders.of(this,new MainViewModelFactory(AppDatabase.getInstance(this))).get(MainViewModel.class);
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
        //mainViewModel.getNoteBooks();
        mainViewModel.getSyncNotebooks();
        //mainViewModel.getNotes();
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
        fragmentList.add(RecentNoteFragment.newInstance());
        fragmentList.add(RecentNoteFragment.newInstance());
        fragmentList.add(RecentNoteFragment.newInstance());
        fragmentList.add(RecentNoteFragment.newInstance());

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

        mainViewModel.syncNotebooksResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                syncNotebooks(response);
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
        if (position==0){
            menu.add(0, 100, Menu.NONE, R.string.action_settings).setIcon(R.drawable.ic_home).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }else if (position==1){
            menu.add(0, 101, Menu.NONE, R.string.action_settings).setIcon(R.drawable.ic_book).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }else if (position==2){
            menu.add(0, 102, Menu.NONE, R.string.action_settings).setIcon(R.drawable.ic_list).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }else if (position==3){
            menu.add(0, 103, Menu.NONE, R.string.action_settings).setIcon(R.drawable.ic_loyalty_white).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
        return super.onOptionsItemSelected(item);
    }


    private void noteBooksResponse(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                List<Notebook> notebooks = (List<Notebook>) response.data;
                if (notebooks.size()==2){
                    mainViewModel.getNoteBooks();
                }
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

    private void syncNotebooks(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                List<Notebook> notebooks = (List<Notebook>) response.data;
                if (notebooks.size()==2){
                    mainViewModel.getSyncNotebooks();//从网络同步
                }else {
                    Log.d("MainActivit","======同步完成！======");
                    mainViewModel.getNoteBooks();//从本地数据库获取
                }
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
