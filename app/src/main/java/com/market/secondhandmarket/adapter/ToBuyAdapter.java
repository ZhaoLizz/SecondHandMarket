package com.market.secondhandmarket.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.market.secondhandmarket.R;
import com.market.secondhandmarket.bean.ToBuyItem;

import java.util.List;

/**
 * Created by a6100890 on 2018/3/18.
 */

public class ToBuyAdapter extends BaseQuickAdapter<ToBuyItem,BaseViewHolder> {
    public ToBuyAdapter(@Nullable List<ToBuyItem> data) {
        super(R.layout.item_list_buy, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ToBuyItem item) {
        String price = item.getPrice();
        String content = item.getContent();
        String title = item.getTitle();
        String phone = item.getPhone();

        helper.setText(R.id.tobuy_name, title);
        helper.setText(R.id.tobuy_price, price);
        helper.setText(R.id.tobuy_phone, phone);
        helper.setText(R.id.tobuy_content, content);
    }
}
