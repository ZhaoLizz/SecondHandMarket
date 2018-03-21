package com.market.secondhandmarket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.market.secondhandmarket.LoginActivity;
import com.market.secondhandmarket.bean.BanUser;
import com.market.secondhandmarket.bean.PushMssage;
import com.market.secondhandmarket.bean.User;
import com.market.secondhandmarket.constant.DbConstant;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Zhaolizhi on 2018/3/21.
 */

public class BmobPushMessageReceiver extends BroadcastReceiver {
    private List<User> banList;
    private List<User> warnList;
    private User curUser = BmobUser.getCurrentUser(User.class);

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            final String message = new Gson().fromJson(intent.getStringExtra("msg"), PushMssage.class).getAlert();
            Logger.d(message);

            BmobQuery<BanUser> query = new BmobQuery<>();
            query.getObject(DbConstant.BANUSER_OBJECT_ID, new QueryListener<BanUser>() {
                @Override
                public void done(BanUser banUser, BmobException e) {
                    if (e == null) {
                        Logger.d("here");
                        banList = banUser.getBannedUserList();
                        warnList = banUser.getWarnedUserList();
                        handleMessage(message, context);
                    } else {
                        Logger.e(e.getMessage());
                    }
                }
            });
        }

    }

    private void handleMessage(String message,Context context) {
        if (message != null) {
            //全体消息
            if (message.contains("封禁")) {
                //封禁消息
                if (banList != null) {
                    for (User user : banList) {
                        if (user.getObjectId().equals(curUser.getObjectId())) {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            BmobUser.logOut();
                            Intent acIntent = new Intent(context, LoginActivity.class);
//                            acIntent.putExtra("isBanned", true);
                            context.startActivity(acIntent);
                            break;
                        }
                    }
                }
            } else if (message.contains("警告")) {
                //警告消息
                if (warnList != null) {
                    for (User user : warnList) {
                        if (user.getObjectId().equals(curUser.getObjectId())) {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                } else {
                    Logger.d("warnList is null");
                }
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
