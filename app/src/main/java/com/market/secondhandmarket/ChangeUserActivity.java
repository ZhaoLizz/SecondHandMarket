package com.market.secondhandmarket;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.market.secondhandmarket.bean.User;
import com.market.secondhandmarket.constant.DbConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ChangeUserActivity extends Activity {

    @BindView(R.id.set_emil)
    MaterialEditText mSetEmil;
    @BindView(R.id.set_phone)
    MaterialEditText mSetPhone;
    @BindView(R.id.set_nick)
    MaterialEditText mSetNick;
    @BindView(R.id.set_btn)
    QMUIRoundButton mSetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.set_btn)
    public void onViewClicked() {
        User curuser = BmobUser.getCurrentUser(User.class);
        User user = new User();
        user.setManager(DbConstant.isManager);
        user.setEmail(mSetEmil.getText().toString());
        user.setMobilePhoneNumber(mSetPhone.getText().toString());
        user.setIdentity(mSetNick.getText().toString());
        user.update(curuser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(ChangeUserActivity.this, "更新用户信息成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }
}
