package com.market.secondhandmarket.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Zhaolizhi on 2018/3/21.
 */

public class BanUser extends BmobObject {
    private List<User> bannedUserList;
    private List<User> warnedUserList;

    public List<User> getWarnedUserList() {
        return warnedUserList;
    }

    public void setWarnedUserList(List<User> warnedUserList) {
        this.warnedUserList = warnedUserList;
    }

    public List<User> getBannedUserList() {
        return bannedUserList;
    }

    public void setBannedUserList(List<User> bannedUserList) {
        this.bannedUserList = bannedUserList;
    }
}
