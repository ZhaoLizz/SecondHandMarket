package com.market.secondhandmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.market.secondhandmarket.adapter.ToBuyAdapter;
import com.market.secondhandmarket.bean.BanUser;
import com.market.secondhandmarket.bean.Item;
import com.market.secondhandmarket.bean.ToBuyItem;
import com.market.secondhandmarket.bean.User;
import com.market.secondhandmarket.constant.DbConstant;
import com.market.secondhandmarket.util.BmobUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

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
    private DeleteDialogFragment mDialogFragment;
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
        } else {
            fetchUserItem();
        }

        if (DbConstant.isManager) {
            setOnAdapterLongClick();
        }
    }

    private void init() {
        if (getArguments() != null) {
            isUserOnly = (Boolean) getArguments().get("isUserOnly");
        }

        /**
         * 删除会话的监听
         */
        mDialogFragment = new DeleteDialogFragment();
        mDialogFragment.setOnDeleteItemListener(new DeleteDialogFragment.OnDeleteItemListener() {
            @Override
            public void onDeleteItem(int position, boolean isWarn, boolean isBan, boolean isNoteAll) {
                Logger.d("buy: onDeleteItem");
                final ToBuyItem buyItem = mToBuyItemList.remove(position);

                if (isBan) {

                    //更新封禁列表
                    BmobQuery<BanUser> query = new BmobQuery<>();
                    query.getObject(DbConstant.BANUSER_OBJECT_ID, new QueryListener<BanUser>() {
                        @Override
                        public void done(BanUser banUser, BmobException e) {
                            if (e == null) {
                                //封禁名单中没人
                                if (banUser.getBannedUserList() == null) {
                                    List<User> list = new ArrayList<>();
                                    list.add(buyItem.getUser());
                                    banUser.setBannedUserList(list);
                                    banUser.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(getActivity(), "封禁成功!", Toast.LENGTH_SHORT).show();
                                                Logger.d("封禁成功");
                                                //发送通知
                                                BmobUtils.pushBmobMessage("您由于涉嫌违反社区制度被封禁账号处理!");
                                            } else {
                                                Logger.e(e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    //封禁名单中有人
                                    List<User> list = banUser.getBannedUserList();
                                    list.add(buyItem.getUser());
                                    banUser.setBannedUserList(list);
                                    banUser.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(getActivity(), "封禁成功!", Toast.LENGTH_SHORT).show();
                                                Logger.d("封禁成功");
                                                //发送通知
                                                BmobUtils.pushBmobMessage("您由于涉嫌违反社区制度被封禁账号处理!");
                                            } else {
                                                Logger.e(e.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else if (isWarn) {
                    //更新封禁列表
                    BmobQuery<BanUser> query = new BmobQuery<>();
                    query.getObject(DbConstant.BANUSER_OBJECT_ID, new QueryListener<BanUser>() {
                        @Override
                        public void done(BanUser banUser, BmobException e) {
                            if (e == null) {
                                //封禁名单中没人
                                if (banUser.getWarnedUserList() == null) {
                                    List<User> list = new ArrayList<>();
                                    list.add(buyItem.getUser());
                                    banUser.setWarnedUserList(list);
                                    banUser.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(getActivity(), "警告成功!", Toast.LENGTH_SHORT).show();
                                                Logger.d("警告成功");
                                                //发送通知
                                                BmobUtils.pushBmobMessage("您由于涉嫌违反社区制度被警告!");
                                            } else {
                                                Logger.e(e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    //封禁名单中有人
                                    List<User> list = banUser.getWarnedUserList();
                                    list.add(buyItem.getUser());
                                    banUser.setWarnedUserList(list);
                                    banUser.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(getActivity(), "警告成功!", Toast.LENGTH_SHORT).show();
                                                Logger.d("警告成功  " + buyItem.getUser().getUsername());
                                                //发送通知
                                                BmobUtils.pushBmobMessage("您由于涉嫌违反社区制度被警告!");
                                            } else {
                                                Logger.e(e.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
                if (isNoteAll) {
                    if (isBan) {
                        BmobUtils.pushBmobMessage("全体消息: 用户 " + buyItem.getUser().getObjectId() + " 由于涉嫌违反社区规定被警告");
                    } else if (isWarn) {
                        BmobUtils.pushBmobMessage("全体消息: 用户 " + buyItem.getUser().getObjectId() + " 由于涉嫌违反社区规定被封禁账号");
                    }
                }


                buyItem.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            fetchItem();
                        } else {
                            Logger.e(e.getMessage());
                        }
                    }
                });
            }
        });


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
                if (list.size() == 0) {
                    Toast.makeText(getActivity(), "没有发布过任何信息!", Toast.LENGTH_SHORT).show();
                }
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

    /**
     * 只对管理员开放的长按点击事件
     */
    private void setOnAdapterLongClick() {
        mToBuyAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                mDialogFragment.show(getActivity().getSupportFragmentManager(), position);
                return true;
            }
        });
    }
}
