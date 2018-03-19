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
import cn.bmob.v3.listener.UpdateListener;

/**
 * 更新求购物品信息
 */
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

        item.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Logger.d("求购信息更新成功");
                    Toast.makeText(ChangeBuyActivity.this, "求购信息更新成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }


}
