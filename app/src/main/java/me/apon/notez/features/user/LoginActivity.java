package me.apon.notez.features.user;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.apon.notez.R;
import me.apon.notez.data.model.Login;
import me.apon.notez.data.model.Response;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email_et)
    TextInputEditText emailEt;
    @BindView(R.id.pwd_et)
    TextInputEditText pwdEt;

    UserViewModel userViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_act);
        ButterKnife.bind(this);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        initView();
    }

    private void initView() {
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
                Log.d("login","========开始登录======");
                break;
            case SUCCESS:
                Login login = (Login) response.data;
                Log.d("login","========登录成功======"+login.getEmail());
//                if (login!=null&&login.isOk()){
//                    Log.d("register","========登录成功======"+login.getEmail());
//
//                }else {
//                    Log.d("register","========登录失败======");
//                }
                break;
            case ERROR:
                Throwable e = response.error;
                String msg = (e).getMessage();
                Log.d("login","========登录失败======"+msg);
                break;
        }

    }

    @OnClick(R.id.login_bt)
    public void onViewClicked() {
        String email = emailEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        userViewModel.Login(email,pwd);
    }
}
