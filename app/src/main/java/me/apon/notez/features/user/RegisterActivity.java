package me.apon.notez.features.user;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.apon.notez.data.model.BaseResponse;
import me.apon.notez.data.model.Response;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class RegisterActivity extends AppCompatActivity{

    UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void registerResponse(Response response){
        switch (response.status){
            case LOADING:
                break;
            case SUCCESS:
                BaseResponse baseResponse = (BaseResponse) response.data;
                if (baseResponse!=null&&baseResponse.isOk()){

                }else {

                }
                break;
            case ERROR:
                Throwable e = response.error;
                if (e!=null) {
                    String msg = e.getMessage();
                }
                break;
        }
    }
}
