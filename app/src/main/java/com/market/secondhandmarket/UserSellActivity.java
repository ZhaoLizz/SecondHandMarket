package com.market.secondhandmarket;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import butterknife.ButterKnife;

public class UserSellActivity extends FragmentActivity {
    private SellListFragment mSellListFragment;
    private BuyListFragment mBuyListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_fragment);
        ButterKnife.bind(this);

        String activityCategory = getIntent().getStringExtra("activity_category");

        Bundle bundle = new Bundle();
        bundle.putBoolean("isUserOnly", true);

        mSellListFragment = SellListFragment.newInstance();
        mSellListFragment.setArguments(bundle);

        mBuyListFragment = BuyListFragment.newInstance();
        mBuyListFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_full_container, mSellListFragment)
                .add(R.id.fragment_full_container, mBuyListFragment)
                .show(mSellListFragment)
                .commit();

        switch (activityCategory) {
            case "sell":
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_full_container, mSellListFragment)
                        .show(mSellListFragment)
                        .commit();
                break;
            case "buy":
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_full_container, mBuyListFragment)
                        .show(mSellListFragment)
                        .commit();
                break;
        }
    }

}