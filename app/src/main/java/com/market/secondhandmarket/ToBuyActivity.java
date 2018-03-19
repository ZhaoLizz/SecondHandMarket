package com.market.secondhandmarket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.market.secondhandmarket.bean.ToBuyItem;
import com.market.secondhandmarket.bean.User;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class ToBuyActivity extends AppCompatActivity {
    @BindView(R.id.tobuy_name)
    EditText mTobuyName;
    @BindView(R.id.tobuy_price)
    EditText mTobuyPrice;
    @BindView(R.id.tobuy_phone)
    EditText mTobuyPhone;
    @BindView(R.id.tobuy_content)
    EditText mTobuyContent;
    @BindView(R.id.tobuy_btn)
    QMUIRoundButton mTobuyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_buy);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tobuy_btn)
    public void onViewClicked() {
        if (BmobUser.getCurrentUser(User.class) != null) {
            ToBuyItem item = new ToBuyItem();
            item.setTitle(mTobuyName.getText().toString());
            item.setPrice(mTobuyPrice.getText().toString());
            item.setPhone(mTobuyPhone.getText().toString());
            item.setContent(mTobuyContent.getText().toString());
            item.setUser(BmobUser.getCurrentUser(User.class));

            item.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Logger.d("上传求购信息成功");
                        Toast.makeText(ToBuyActivity.this, "上传求购信息成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Logger.e(e.getMessage());
                    }
                }
            });
        } else {
            Toast.makeText(this, "请先登录!", Toast.LENGTH_SHORT).show();
        }

    }
}
