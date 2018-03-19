package com.market.secondhandmarket;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import com.market.secondhandmarket.bean.ToBuyItem;
import com.market.secondhandmarket.bean.User;
import com.orhanobut.logger.Logger;

import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class ChangeBuyActivity extends ToBuyActivity {
    private ToBuyItem mToBuyItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToBuyItem = (ToBuyItem) getIntent().getSerializableExtra("tobuyitem");

        mTobuyName.setText(mToBuyItem.getTitle());
        mTobuyPrice.setText(mToBuyItem.getPrice());
        mTobuyPhone.setText(mToBuyItem.getPhone());
        mTobuyContent.setText(mToBuyItem.getContent());
        mTobuyBtn.setText("更新");
    }

    @Override
    @OnClick(R.id.tobuy_btn)
    public void onViewClicked() {
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
                    Toast.makeText(ChangeBuyActivity.this, "上传求购信息成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }


}
