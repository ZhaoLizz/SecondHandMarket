package com.market.secondhandmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.market.secondhandmarket.bean.Item;

import butterknife.ButterKnife;

public class UserSellActivity extends FragmentActivity {
    private SellListFragment mSellListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_fragment);
        ButterKnife.bind(this);

        mSellListFragment = SellListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_full_container, mSellListFragment)
                .show(mSellListFragment)
                .commit();

        setListener();
    }

    private void setListener() {
        mSellListFragment.setOnAdapterClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Item item = (Item) adapter.getData().get(position);
                Intent intent = new Intent(UserSellActivity.this, ChangeItemActivity.class);
                /*intent.putExtra("title", item.getTitle());
                intent.putExtra("content", item.getContent());
                intent.putExtra("price", item.getPrice());
                intent.putExtra("phone", item.getPhone());*/
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
    }
}