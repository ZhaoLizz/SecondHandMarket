package com.market.secondhandmarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.Utils;
import com.market.secondhandmarket.bean.User;
import com.market.secondhandmarket.constant.UserConstant;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends Activity {
    @BindView(R.id.email_sign_in_button)
    QMUIRoundButton mEmailSignInButton;
    @BindView(R.id.email)
    AutoCompleteTextView mEmail;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.register)
    TextView mRegister;
    @BindView(R.id.tourist)
    TextView mTourist;
    @BindView(R.id.login_progress)
    ProgressBar mLoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());        //Logger初始化
        Utils.init(getApplication());   //AndroidUtilCode初始化1
        Bmob.initialize(this, "fa6a000d3be3cc1df84347338bb012b4");
    }


    @OnClick(R.id.email_sign_in_button)
    public void onLoginClicked() {
        String account = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (account.length() == 0 || password.length() == 0) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            User user = new User(UserConstant.ID_USER);
            user.setUsername(account);
            user.setPassword(password);
            mLoginProgress.setVisibility(View.VISIBLE);
            user.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    mLoginProgress.setVisibility(View.GONE);
                    KeyboardUtils.hideSoftInput(LoginActivity.this);
                    if (e == null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        if (e.getErrorCode() == 101) {
                            Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }


    @OnClick({R.id.register, R.id.tourist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //注册
            case R.id.register:
                
                break;
            case R.id.tourist:
                break;
        }
    }
}
