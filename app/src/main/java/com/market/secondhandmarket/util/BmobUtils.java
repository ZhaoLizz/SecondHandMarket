package com.market.secondhandmarket.util;

import com.orhanobut.logger.Logger;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;

/**
 * Created by Zhaolizhi on 2018/3/21.
 */

public class BmobUtils {
    /**
     * 发送bmob推送
     */
    public static void pushBmobMessage(String message) {
        BmobPushManager bmobPushManager = new BmobPushManager();
        bmobPushManager.pushMessage(message, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Logger.d("push succeed");
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }
}
