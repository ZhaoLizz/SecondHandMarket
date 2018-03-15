package com.market.secondhandmarket.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by qinba on 2018/3/15.
 */

public class User extends BmobUser {
    private String identity;

    public User(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
