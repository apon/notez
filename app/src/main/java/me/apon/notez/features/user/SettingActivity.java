package me.apon.notez.features.user;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.apon.notez.R;
import me.apon.notez.app.BaseActivity;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.database.dao.AccountDao;
import me.apon.notez.data.model.Account;
import me.apon.notez.data.model.Response;
import me.apon.notez.utils.ExceptionMsgUtil;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/5.
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_email)
    TextView tvEmail;

    UserViewModel userViewModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
        //starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting_act);
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


        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        observeLiveData();
    }

    private void observeLiveData() {
        userViewModel.updateNameResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                updateNameResponse(response);
            }
        });

        userViewModel.updatePwdResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                updatePwdResponse(response);
            }
        });

        userViewModel.logoutResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                logoutResponse(response);
            }
        });
    }

    private void updateNameResponse(Response response){
        switch (response.status) {
            case LOADING:
                showLoadingDialog("正在更改用户名，请稍等...");
                break;
            case SUCCESS:
                dismissLoadingDialog();
                Account account = (Account) response.data;
                if (account!=null){
                    tvName.setText(account.getUsername());
                    tvEmail.setText(account.getEmail());
                }
                Toast.makeText(this, "更改用户成功!", Toast.LENGTH_SHORT).show();
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

    private void updatePwdResponse(Response response){
        switch (response.status) {
            case LOADING:
                showLoadingDialog("正在更改密码，请稍等...");
                break;
            case SUCCESS:
                dismissLoadingDialog();
                Toast.makeText(this, "更改密码成功!", Toast.LENGTH_SHORT).show();
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

    private void logoutResponse(Response response){
        switch (response.status) {
            case LOADING:
                showLoadingDialog("正在退出，请稍等...");
                break;
            case SUCCESS:
                dismissLoadingDialog();
                Toast.makeText(this, "退出成功!", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.ll_change_name, R.id.tv_change_pass, R.id.tv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_change_name:
                changeName();
                break;
            case R.id.tv_change_pass:
                changePass();
                break;
            case R.id.tv_logout:
                logout();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Account account = AppDatabase.getInstance(this).accountDao().getCurrent();
        if (account!=null){
            tvName.setText(account.getUsername());
            tvEmail.setText(account.getEmail());
        }
        Log.d("SettingActivity","======onResume========");
    }

    private void changeName(){
        new MaterialDialog.Builder(this)
                .title("修改用户名")
                //.content(R.string.input_content)
                .inputRangeRes(4, 20, R.color.colorPrimary)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("输入新用户名", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        userViewModel.updateName(input.toString());
                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .show();
    }

    private void changePass(){
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("修改密码")
                //.content(R.string.input_content)
                .customView(R.layout.change_pass_dialog, false)
                .positiveText("确定")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View view = dialog.getCustomView();
                        EditText oldPass = view.findViewById(R.id.et_old_pass);
                        EditText newPass = view.findViewById(R.id.et_new_pass);
                        userViewModel.updatePwd(oldPass.getText().toString().trim(),newPass.getText().toString().trim());
                        //Toast.makeText(SettingActivity.this, oldPass.getText() + ":" + newPass.getText(), Toast.LENGTH_SHORT).show();
                    }
                }).build();

                final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                positiveAction.setEnabled(false);
                View view = dialog.getCustomView();
                final EditText oldPass = view.findViewById(R.id.et_old_pass);
                EditText newPass = view.findViewById(R.id.et_new_pass);
                final String[] oldPassStr = {""};
                final String[] newPassStr = {""};
                oldPass.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                oldPassStr[0] = s.toString().trim();
                                positiveAction.setEnabled(oldPassStr[0].length() > 0 && newPassStr[0].length() > 0);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                newPass.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                newPassStr[0] = s.toString().trim();
                                positiveAction.setEnabled(oldPassStr[0].length() > 0 && newPassStr[0].length() > 0);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });

                dialog.show();
    }

    private void logout(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("退出")
                .content("你确定要退出吗？")
                .positiveText("是")
                .negativeText("否")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userViewModel.logout();
                        //Toast.makeText(SettingActivity.this, "退出", Toast.LENGTH_SHORT).show();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }
}
