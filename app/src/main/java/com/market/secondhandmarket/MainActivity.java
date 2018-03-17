package com.market.secondhandmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.publish_sale)
    FloatingActionButton mPublishSale;
    @BindView(R.id.publish_tobuy)
    FloatingActionButton mPublishTobuy;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private SellFragment mSellFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:     //列表
                    replaceFragment(mSellFragment);
                    return true;
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSellFragment = SellFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_coninter, mSellFragment)
                .show(mSellFragment)
                .commit();

    }


    @OnClick({R.id.publish_sale, R.id.publish_tobuy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.publish_sale:
                startActivity(new Intent(this, PublishActivity.class));
                break;
            case R.id.publish_tobuy:
                startActivity(new Intent(this, ToBuyActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);

        //找到searchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView

        return super.onCreateOptionsMenu(menu);
    }


    private void replaceFragment(Fragment fragment) {
        Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_coninter);
        getSupportFragmentManager().beginTransaction()
                .hide(curFragment)
                .show(fragment)
                .commit();
    }

}
