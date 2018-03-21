package com.market.secondhandmarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.market.secondhandmarket.adapter.SellAdapter;
import com.market.secondhandmarket.bean.Item;
import com.market.secondhandmarket.bean.User;
import com.market.secondhandmarket.constant.DbConstant;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by qinba on 2018/3/17.
 */

/**
 * 出售物品
 */
public class SellListFragment extends Fragment {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    @BindView(R.id.refersh_layout)
    SwipeRefreshLayout mRefershLayout;
    private boolean isUserOnly = false;

    private List<Item> mItemList = new ArrayList<>();
    private SellAdapter mSellAdapter;
    private DeleteDialogFragment mDialogFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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
        } else {
            fetchUserItem();
        }

        if (DbConstant.isManager) {
            setOnAdapterLongClick(DbConstant.isManager);
        }
    }

    private void init() {
        if (getArguments() != null) {
            isUserOnly = (Boolean) getArguments().get("isUserOnly");
        }

        /**
         * 删除数据时的会话监听
         */
        mDialogFragment = new DeleteDialogFragment();
        mDialogFragment.setOnDeleteItemListener(new DeleteDialogFragment.OnDeleteItemListener() {
            @Override
            public void onDeleteItem(int position, final boolean isWarn, final boolean isBan, final boolean isNoteAll) {
                final Item item = mItemList.remove(position);
                //处理警告，封禁
                if (isBan) {
                    //在当前管理员User的banUserList字段添加被封禁用户的objectId
                    User curUser = BmobUser.getCurrentUser(User.class);
                    List<String> banUserList;
                    if (curUser.getBanUserList() != null) {
                        Logger.d(curUser.getBanUserList().size());
                        banUserList = curUser.getBanUserList();
                    } else {
                        banUserList = new ArrayList<>();
                    }
                    banUserList.add(item.getUser().getObjectId());

                    User newUser = new User();
                    newUser.setManager(true);
                    newUser.setBanUserList(banUserList);
                    newUser.update(curUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Logger.d("done");
                            } else {
                                Logger.e(e.getMessage());
                            }
                        }
                    });
                } else if (isWarn) {
                    //在当前管理员User的banUserList字段添加被封禁用户的objectId
                    User curUser = BmobUser.getCurrentUser(User.class);
                    List<String> warnUserList;
                    if (curUser.getBanUserList() != null) {
                        Logger.d(curUser.getBanUserList().size());
                        warnUserList = curUser.getBanUserList();
                    } else {
                        warnUserList = new ArrayList<>();
                    }
                    warnUserList.add(item.getUser().getObjectId());

                    User newUser = new User();
                    newUser.setManager(true);
                    newUser.setWarnUserList(warnUserList);
                    newUser.update(curUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Logger.d("done");
                            } else {
                                Logger.e(e.getMessage());
                            }
                        }
                    });
                }
                //删除数据
                /*item.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            fetchItem();
                        } else {
                            Logger.e(e.getMessage());
                        }
                    }
                });*/
            }
        });

        mSellAdapter = new SellAdapter(mItemList, getActivity());
        mSellAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        if (isUserOnly) {
            setOnAdapterClickListener();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mSellAdapter);

        //下拉刷新
        mRefershLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isUserOnly) {
                    fetchItem();
                } else {
                    fetchUserItem();
                }
            }
        });

        if (!isUserOnly) {
            fetchItem();
        } else {
            fetchUserItem();
        }
    }

    public static SellListFragment newInstance() {
        return new SellListFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 从数据库中加载数据
     */
    public void fetchItem() {
        BmobQuery<Item> query = new BmobQuery<>();
        query.findObjects(new FindListener<Item>() {
            @Override
            public void done(List<Item> list, BmobException e) {
                if (e == null) {
                    if (list != null) {
                        mItemList.clear();
                        mItemList.addAll(list);
                        mSellAdapter.notifyDataSetChanged();
                        mRefershLayout.setRefreshing(false);
                    }
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }


    public void searchItem(final String searchStr) {
        Logger.d("searchItem");
        BmobQuery<Item> query = new BmobQuery<>();
        query.findObjects(new FindListener<Item>() {
            @Override
            public void done(List<Item> list, BmobException e) {
                if (e == null) {
                    if (list != null) {
                        List<Item> resultList = new ArrayList<>();
                        for (Item toBuyItem : list) {
                            if (toBuyItem.getTitle().contains(searchStr)) {
                                resultList.add(toBuyItem);
                            }
                        }
                        mItemList.clear();
                        mItemList.addAll(resultList);
                        mSellAdapter.notifyDataSetChanged();
                    }
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    public void fetchUserItem() {
        final User currentUser = BmobUser.getCurrentUser(User.class);
        BmobQuery<Item> query = new BmobQuery<>();
        query.addWhereEqualTo("mUser", currentUser);
        query.findObjects(new FindListener<Item>() {
            @Override
            public void done(List<Item> list, BmobException e) {
                mItemList.clear();
                mItemList.addAll(list);
                mRefershLayout.setRefreshing(false);
                mSellAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setOnAdapterClickListener() {
        mSellAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Item item = (Item) adapter.getData().get(position);
                Intent intent = new Intent(getActivity(), ChangeItemActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
    }

    /**
     * 管理员登录情况下，出售列表里 长按弹出删除对话框
     *
     * @param isSwipable
     */
    public void setOnAdapterLongClick(boolean isSwipable) {
        if (isSwipable) {
            mSellAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                    mDialogFragment.show(getActivity().getSupportFragmentManager(), position);
                    return true;
                }
            });
        }
    }

    private void noteAllUser(final String noteMessage) {
        BmobQuery<User> queryUser = new BmobQuery<>();
        final List<BmobObject> bmobObjectList = new ArrayList<>();

        queryUser.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    Logger.d(list.size());
                    for (User user : list) {
//                        user.setNoteAllMessage(noteMessage);
                        bmobObjectList.add(user);
                    }
                    new BmobBatch().updateBatch(bmobObjectList)
                            .doBatch(new QueryListListener<BatchResult>() {
                                @Override
                                public void done(List<BatchResult> list, BmobException e) {
                                    if (e == null) {
                                        Logger.d(list.size());
                                    } else {
                                        Logger.e(e.getMessage());
                                    }
                                }
                            });
                }
            }
        });
    }


}
