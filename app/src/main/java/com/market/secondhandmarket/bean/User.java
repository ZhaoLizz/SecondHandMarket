package com.market.secondhandmarket.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by qinba on 2018/3/15.
 */

public class User extends BmobUser {
    private String identity;        //昵称
    private boolean isManager = false;

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
