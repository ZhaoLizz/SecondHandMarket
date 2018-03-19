package com.market.secondhandmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.market.secondhandmarket.constant.DbConstant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;

/**
 * Created by Zhaolizhi on 2018/3/18.
 */

public class UserFragment extends Fragment {
    @BindView(R.id.user_sell)
    TextView mUserSell;
    @BindView(R.id.user_tobuy)
    TextView mUserTobuy;
    @BindView(R.id.user_exit)
    TextView mUserExit;
    Unbinder unbinder;
    @BindView(R.id.user_set)
    TextView mUserSet;

    private OnUserViewClickListener mOnUserViewClickListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.user_sell, R.id.user_tobuy, R.id.user_exit,R.id.user_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_sell:
                mOnUserViewClickListener.onUserSellClick();
                break;
            case R.id.user_tobuy:
                mOnUserViewClickListener.onUserBuyClick();
                break;
            case R.id.user_exit:
                BmobUser.logOut();
                DbConstant.isManager = false;
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.user_set:
                startActivity(new Intent(getActivity(), ChangeUserActivity.class));
                break;
        }
    }



    public interface OnUserViewClickListener {
        void onUserSellClick();

        void onUserBuyClick();
    }

    public void setOnUserViewClickListener(OnUserViewClickListener listener) {
        mOnUserViewClickListener = listener;
    }
}
