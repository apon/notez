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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.apon.notez.R;
import me.apon.notez.app.BaseActivity;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.database.dao.AccountDao;
import me.apon.notez.data.model.Response;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class RegisterActivity extends BaseActivity {

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
        Intent starter = new Intent(context, RegisterActivity.class);
        //starter.putExtra();
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register_act);
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

        initView();
    }

    private void initView() {
        userViewModel.registerResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                registerResponse(response);
            }
        });
    }

    private void registerResponse(Response response) {
        switch (response.status) {
            case LOADING:
                showLoadingDialog("正在注册，请稍等...");
                break;
            case SUCCESS:
                dismissLoadingDialog();
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                break;
            case ERROR:
                dismissLoadingDialog();
                Throwable e = response.error;
                if (e != null) {
                    String msg = e.getMessage();
                    Log.d("register", "========注册失败======" + msg);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @OnClick(R.id.register_bt)
    public void onViewClicked() {
        String email = emailEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        userViewModel.register(email, pwd);
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
