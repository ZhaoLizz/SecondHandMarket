package com.market.secondhandmarket.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by qinba on 2018/3/17.
 */

public class ToBuyItem extends BmobObject implements Serializable {
    private String title;
    private User mUser;
    private String phone;
    private String price;
    private String content;

    public ToBuyItem() {

    }

    public ToBuyItem(String title, User user, String phone, String price, String content) {
        this.title = title;
        mUser = user;
        this.phone = phone;
        this.price = price;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
