package com.market.secondhandmarket.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.market.secondhandmarket.R;

import java.util.List;

/**
 * Created by qinba on 2018/3/17.
 */

public class ChildRecyclerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    Context mContext;

    public ChildRecyclerAdapter(Context context, @Nullable List<String> urlList) {
        super(R.layout.item_image, urlList);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, String url) {
        Glide.with(mContext).load(url).into((ImageView) helper.getView(R.id.big_photo_holder));
    }
}
