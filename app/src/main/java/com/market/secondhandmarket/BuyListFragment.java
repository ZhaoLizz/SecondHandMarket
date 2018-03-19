package com.market.secondhandmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.market.secondhandmarket.adapter.ToBuyAdapter;
import com.market.secondhandmarket.bean.Item;
import com.market.secondhandmarket.bean.ToBuyItem;
import com.market.secondhandmarket.bean.User;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by a6100890 on 2018/3/18.
 */

/**
 * 求购物品
 */
public class BuyListFragment extends Fragment {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refersh_layout)
    SwipeRefreshLayout mRefershLayout;
    Unbinder unbinder;

    private List<ToBuyItem> mToBuyItemList = new ArrayList<>();
    private ToBuyAdapter mToBuyAdapter;
    private boolean isUserOnly = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isUserOnly) {
            fetchItem();
        }
    }

    private void init() {
        if (getArguments() != null) {
            isUserOnly = (Boolean) getArguments().get("isUserOnly");
        }

        mToBuyAdapter = new ToBuyAdapter(mToBuyItemList);
        mToBuyAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mToBuyAdapter);

        mRefershLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDateByState(isUserOnly);
            }
        });

        if (isUserOnly) {
            mToBuyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ToBuyItem toBuyItem = (ToBuyItem) adapter.getData().get(position);
                    Intent intent = new Intent(getActivity(), ChangeBuyActivity.class);
                    intent.putExtra("tobuyitem", toBuyItem);
                    startActivity(intent);
                }
            });
        }

        fetchDateByState(isUserOnly);
    }
    public static BuyListFragment newInstance() {
        return new BuyListFragment();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void fetchItem() {
        BmobQuery<ToBuyItem> query = new BmobQuery<>();
        query.findObjects(new FindListener<ToBuyItem>() {
            @Override
            public void done(List<ToBuyItem> list, BmobException e) {
                if (e == null) {
                    if (list != null) {
                        mToBuyItemList.clear();
                        mToBuyItemList.addAll(list);
                        mToBuyAdapter.notifyDataSetChanged();
                        mRefershLayout.setRefreshing(false);
                    }
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    public void searchItem(final String searchStr) {
        BmobQuery<ToBuyItem> query = new BmobQuery<>();
        query.findObjects(new FindListener<ToBuyItem>() {
            @Override
            public void done(List<ToBuyItem> list, BmobException e) {
                if (e == null) {
                    if (list != null) {
                        List<ToBuyItem> resultList = new ArrayList<>();
                        for (ToBuyItem toBuyItem : list) {
                            if (toBuyItem.getTitle().contains(searchStr)) {
                                resultList.add(toBuyItem);
                            }
                        }
                        mToBuyItemList.clear();
                        mToBuyItemList.addAll(resultList);
                        mToBuyAdapter.notifyDataSetChanged();
                    }
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    public void fetchUserItem() {
        User currentUser = BmobUser.getCurrentUser(User.class);
        BmobQuery<ToBuyItem> query = new BmobQuery<>();
        query.addWhereEqualTo("mUser", currentUser);
        query.findObjects(new FindListener<ToBuyItem>() {
            @Override
            public void done(List<ToBuyItem> list, BmobException e) {
                Logger.d(list.size());
                mToBuyItemList.clear();
                mToBuyItemList.addAll(list);
                mToBuyAdapter.notifyDataSetChanged();
                mRefershLayout.setRefreshing(false);
            }
        });
    }

    public void fetchDateByState(boolean isUserOnly) {
        if (isUserOnly) {
            fetchUserItem();
        } else {
            fetchItem();
        }
    }
}
