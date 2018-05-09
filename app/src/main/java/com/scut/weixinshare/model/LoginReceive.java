package com.scut.weixinshare.model;

/**
 * Created by skyluo on 2018/5/9.
 */

public class LoginReceive {
    private String token;
    private User user;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
