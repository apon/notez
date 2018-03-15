package me.apon.notez.features.user;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.apon.notez.R;
import me.apon.notez.app.BaseActivity;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.database.dao.AccountDao;
import me.apon.notez.data.model.Response;
import me.apon.notez.utils.ExceptionMsgUtil;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.email_et)
    TextInputEditText emailEt;
    @BindView(R.id.pwd_et)
    TextInputEditText pwdEt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    UserViewModel userViewModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        //starter.putExtra();
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_act);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        AccountDao accountDao = AppDatabase.getInstance(this).accountDao();

        userViewModel = ViewModelProviders.of(this,new UserViewModelFactory(accountDao)).get(UserViewModel.class);

        observeLiveData();
    }

    private void observeLiveData() {
        userViewModel.loginResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                loginResponse(response);
            }
        });
    }

    private void loginResponse(Response response) {

        switch (response.status) {
            case LOADING:
                showLoadingDialog("正在登录，请稍等...");
                break;
            case SUCCESS:
                dismissLoadingDialog();
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case ERROR:
                dismissLoadingDialog();
                Throwable e = response.error;
                e.printStackTrace();
                String msg = ExceptionMsgUtil.getMsg(e);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @OnClick({R.id.login_bt,R.id.register})
    public void onViewClicked(View v) {
        switch (v.getId()){
            case R.id.login_bt:
                String email = emailEt.getText().toString();
                String pwd = pwdEt.getText().toString();
                userViewModel.Login(email, pwd);
                break;
            case R.id.register:
                RegisterActivity.start(this);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
