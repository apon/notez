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
import me.apon.notez.data.model.BaseResponse;
import me.apon.notez.data.model.Response;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.email_et)
    TextInputEditText emailEt;
    @BindView(R.id.pwd_et)
    TextInputEditText pwdEt;

    UserViewModel userViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register_act);
        ButterKnife.bind(this);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

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
                Log.d("register","========开始注册======");
                break;
            case SUCCESS:

                BaseResponse baseResponse = (BaseResponse) response.data;
                Log.d("register","========注册成功======"+baseResponse.getMsg());

//                if (baseResponse != null && baseResponse.isOk()) {
//                    Log.d("register","========注册成功======"+baseResponse.getMsg());
//                } else {
//                    Log.d("register","========注册失败======"+baseResponse.getMsg());
//                }
                break;
            case ERROR:
                Throwable e = response.error;
                //e.printStackTrace();
                if (e != null) {
                    String msg = e.getMessage();
                    Log.d("register","========注册失败======"+msg);
                }
                break;
        }
    }

    @OnClick(R.id.register_bt)
    public void onViewClicked() {
        String email = emailEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        userViewModel.register(email,pwd);
    }
}
