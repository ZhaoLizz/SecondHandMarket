package com.market.secondhandmarket;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.market.secondhandmarket.bean.Item;
import com.market.secondhandmarket.bean.ToBuyItem;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by qinba on 2018/3/19.
 */

public class DeleteDialogFragment extends DialogFragment {
    private Item mItem;
    private ToBuyItem mToBuyItem;
    private OnDeleteItemListener mDeleteItemListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] items = new String[]{"警告该用户?", "封禁该用户?","通知所有用户?"};
        boolean[] checked = new boolean[]{false, false, false};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("确定删除该条数据?")
                .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Logger.d(which + "  " + isChecked);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mItem != null) {
                            mItem.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(getContext(), "成功删除数据!", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Logger.e(e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(false);
        return builder.create();
    }

    public void show(FragmentManager manager, Item item) {
        mItem = item;
        super.show(manager, "1");
    }

    interface OnDeleteItemListener {
        void onDeleteItem();
    }

    public void setOnDeleteItemListener(OnDeleteItemListener listener) {
        mDeleteItemListener = listener;
    }


}
