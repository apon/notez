package me.apon.notez.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import me.apon.notez.R;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/1.
 */

public class BaseActivity extends AppCompatActivity {

    MaterialDialog loadingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();

    }

    protected void showLoadingDialog(){
        loadingDialog.show();
    }
    protected void showLoadingDialog(String content){
        loadingDialog.setContent(content);
        loadingDialog.show();
    }

    protected void dismissLoadingDialog(){
        loadingDialog.dismiss();
    }

}
