package com.market.secondhandmarket;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.market.secondhandmarket.bean.User;
import com.market.secondhandmarket.constant.DbConstant;
import com.market.secondhandmarket.constant.UIConstant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.publish_sale)
    FloatingActionButton mPublishSale;
    @BindView(R.id.publish_tobuy)
    FloatingActionButton mPublishTobuy;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_progressbar)
    ProgressBar mMainProgressbar;

    private SellListFragment mSellFragment;
    private BuyListFragment mBuyListFragment;
    private UserFragment mUserFragment;
    private Fragment[] mFragments;
    private int lastShowFragment = 0;
    private String fragmentTAG = UIConstant.TAG_SELL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initPremission();
        Toast.makeText(this, "正在加载数据", Toast.LENGTH_SHORT).show();
        verifyIsManager();
    }

    private void initFragment() {
        mSellFragment = SellListFragment.newInstance();
        mBuyListFragment = BuyListFragment.newInstance();
        mUserFragment = UserFragment.newInstance();
        mFragments = new Fragment[]{mSellFragment, mBuyListFragment, mUserFragment};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_coninter, mUserFragment)
                .add(R.id.fragment_coninter, mBuyListFragment)
                .add(R.id.fragment_coninter, mSellFragment)
                .hide(mBuyListFragment)
                .hide(mUserFragment)
                .show(mSellFragment)
                .commit();

        /**
         * "我的" fragment里的点击事件
         */
        mUserFragment.setOnUserViewClickListener(new UserFragment.OnUserViewClickListener() {
            @Override
            public void onUserSellClick() {
                Intent intent = new Intent(MainActivity.this, UserItemActivity.class);
                intent.putExtra("activity_category", "sell");
                startActivity(intent);
            }

            @Override
            public void onUserBuyClick() {
                Intent intent = new Intent(MainActivity.this, UserItemActivity.class);
                intent.putExtra("activity_category", "buy");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bmob.initialize(this, "fa6a000d3be3cc1df84347338bb012b4");
    }

    private void initPremission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "权限申请被拒绝", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 悬浮按钮点击事件
     *
     * @param view
     */
    @OnClick({R.id.publish_sale, R.id.publish_tobuy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.publish_sale:
                if (BmobUser.getCurrentUser() != null) {
                    Intent intent = new Intent(this, PublishActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "请先登录!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.publish_tobuy:
                if (BmobUser.getCurrentUser() != null) {
                    Intent intent2 = new Intent(this, ToBuyActivity.class);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, "请先登录!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * 搜索栏点击事件
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);
        //找到searchView
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("搜索物品关键字");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                KeyboardUtils.hideSoftInput(MainActivity.this);
                searchView.clearFocus();
                switch (fragmentTAG) {
                    case UIConstant.TAG_SELL:
                        mSellFragment.fetchItem();
                        break;
                    case UIConstant.TAG_TOBUY:
                        mBuyListFragment.fetchItem();
                        break;
                }
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String searchStr) {
                switch (lastShowFragment) {
                    case 0:
                        mSellFragment.searchItem(searchStr);
                        break;
                    case 1:
                        mBuyListFragment.searchItem(searchStr);
                        break;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 底部导航栏点击事件
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:     //列表
                    if (lastShowFragment != 0) {
                        switchFragment(lastShowFragment, 0);
                    }
                    fragmentTAG = UIConstant.TAG_SELL;
                    return true;
                case R.id.navigation_home:
                    if (lastShowFragment != 1) {
                        switchFragment(lastShowFragment, 1);
                    }
                    fragmentTAG = UIConstant.TAG_TOBUY;
                    return true;
                case R.id.navigation_notifications:
                    if (lastShowFragment != 2) {
                        switchFragment(lastShowFragment, 2);
                    }
                    fragmentTAG = UIConstant.TAG_SETTING;
                    return true;
            }
            return false;
        }
    };

    private void switchFragment(int lastIndex, int index) {
        lastShowFragment = index;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mFragments[lastIndex]);
        if (!mFragments[index].isAdded()) {
            transaction.add(R.id.fragment_coninter, mFragments[index]);
        }
        transaction.show(mFragments[index]).commit();
    }

    public void verifyIsManager() {
        User curUser = BmobUser.getCurrentUser(User.class);
        if (curUser != null) {
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("objectId", curUser.getObjectId());
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (list.size() > 0) {
                        DbConstant.isManager = list.get(0).isManager();
                        initFragment();
                    } else {
                        DbConstant.isManager = false;
                        initFragment();
                    }
                }
            });
        } else {
            initFragment();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
