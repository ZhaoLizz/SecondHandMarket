package com.market.secondhandmarket.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.market.secondhandmarket.R;

import java.util.List;

/**
 * Created by a6100890 on 2018/3/16.
 */

public class SelectedPhotoAdapter extends BaseQuickAdapter<Uri, BaseViewHolder> {
    /*public SelectedPhotoAdapter(int layoutResId, @Nullable List<Bitmap> data) {
        super(layoutResId, data);
    }*/
    private Context mContext;

    public SelectedPhotoAdapter(@Nullable List<Uri> data, Context context) {
        super(R.layout.item_select_photo,data);
        mContext = context;
    }

    public SelectedPhotoAdapter(Context context) {
        super(R.layout.item_select_photo);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Uri item) {
        Glide.with(mContext).load(item).into((ImageView) helper.getView(R.id.photo_holder));
    }
}
