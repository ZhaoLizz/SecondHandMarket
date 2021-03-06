package com.market.secondhandmarket;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.Utils;
import com.market.secondhandmarket.bean.BanUser;
import com.market.secondhandmarket.bean.User;
import com.market.secondhandmarket.constant.DbConstant;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends AppCompatActivity {

    private TextView mTourist;
    private EditText mEmail;
    private EditText mPassword;
    private Button btGo;
    private CardView cv;
    private FloatingActionButton fab;
    private List<User> banList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("main", "onCreate: ");
        setContentView(R.layout.activity_login);

        //应用初始化
        Logger.addLogAdapter(new AndroidLogAdapter());        //Logger初始化
        Utils.init(getApplication());   //AndroidUtilCode初始化1
        Bmob.initialize(this, "fa6a000d3be3cc1df84347338bb012b4");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);
        ButterKnife.bind(this);
        initView();

        QMUIStatusBarHelper.supportTransclentStatusBar6();
        QMUIStatusBarHelper.translucent(LoginActivity.this);

        setListener();
        /*if (getIntent() != null) {
            boolean isBanned = getIntent().getBooleanExtra("isBanned", false);
            if (!isBanned) {

            }
        }*/
        //判断是否登录
        if (BmobUser.getCurrentUser(User.class) != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        mEmail = findViewById(R.id.et_username);
        mPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        cv = findViewById(R.id.cv);
        fab = findViewById(R.id.fab);
        mTourist = findViewById(R.id.tourist);
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);

                String account = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (account.length() == 0 || password.length() == 0) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    final User user = new User();
                    user.setUsername(account);
                    user.setPassword(password);
                    user.login(new SaveListener<User>() {
                        @Override
                        //bmobUser是当前登录上去的user
                        public void done(final User bmobUser, BmobException e) {
                            KeyboardUtils.hideSoftInput(LoginActivity.this);
                            if (e == null) {
                                //判断登录的用户是不是在封禁名单中
                                BmobQuery<BanUser> query = new BmobQuery<>();
                                query.getObject(DbConstant.BANUSER_OBJECT_ID, new QueryListener<BanUser>() {
                                    @Override
                                    public void done(BanUser banUser, BmobException e) {
                                        if (e == null) {
                                            banList = banUser.getBannedUserList();
                                            List<String> banUserObjects = new ArrayList<>();
                                            if (banList != null) {
                                                for (User bannedUser : banList) {
                                                    banUserObjects.add(bannedUser.getObjectId());
                                                }
                                                if (banUserObjects.contains(bmobUser.getObjectId())) {
                                                    BmobUser.logOut();
                                                    Toast.makeText(LoginActivity.this, "您的账户已被封禁,无法登录!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
                                                    Intent i2 = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(i2, oc2.toBundle());
                                                }
                                            }
                                        } else {
                                            Logger.e(e.getMessage());
                                        }
                                    }
                                });

                            } else {
                                if (e.getErrorCode() == 101) {
                                    Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class), options.toBundle());
            }
        });
        mTourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }


}
