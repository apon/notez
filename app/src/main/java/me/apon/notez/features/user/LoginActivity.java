package me.apon.notez.features.user;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.apon.notez.data.model.Login;
import me.apon.notez.data.model.Response;
import me.apon.notez.network.ApiException;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class LoginActivity extends AppCompatActivity{

    UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void loginResponse(Response response){

        switch (response.status){
            case LOADING:
                break;
            case SUCCESS:
                Login login = (Login) response.data;
                break;
            case ERROR:
                Throwable e = response.error;
                String msg = (e).getMessage();
                break;
        }

    }

}
