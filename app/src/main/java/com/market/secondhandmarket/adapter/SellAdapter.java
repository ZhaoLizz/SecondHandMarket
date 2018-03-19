package com.market.secondhandmarket.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.market.secondhandmarket.R;
import com.market.secondhandmarket.bean.Item;

import java.util.List;

/**
 * Created by qinba on 2018/3/17.
 */

public class SellAdapter extends BaseItemDraggableAdapter<Item, BaseViewHolder> {

    private Context mContext;
    private RecyclerView childRecyclerView;

    public SellAdapter(List<Item> data, Context context) {
        super(R.layout.item_list_sell, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Item item) {
        String username = "";
        String price = item.getPrice();
        String content = item.getContent();
        String title = item.getTitle();
        /*if (item.getUser() != null) {
            username = item.getUser().getUsername();
            helper.setText(R.id.sell_user_id,username);
            Logger.d(username);
        }*/

        if (price != null) {
            helper.setText(R.id.tobuy_price, item.getPrice());
        }
        if (content != null) {
            helper.setText(R.id.sell_content, item.getContent());
        }
        if (title != null) {
            helper.setText(R.id.sell_user_id, title);
        }

        ChildRecyclerAdapter adapter = new ChildRecyclerAdapter(mContext, item.getUrls());
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        childRecyclerView = helper.getView(R.id.sell_horizental_rv);
        childRecyclerView.setLayoutManager(manager);
        childRecyclerView.setAdapter(adapter);
    }
}
