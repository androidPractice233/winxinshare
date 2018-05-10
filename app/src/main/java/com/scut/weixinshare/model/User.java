package com.scut.weixinshare.model;

public class User {
    private String userId="";
    private String userName="";
    private String nickName="";
    private int sex=1;
    private String birthday="";
    private String location="";
    private String portrait="";
    private String userPwd="";
    //构造器参数之外的属性可以为空
    public User(String userId,String userName,String nickName){
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
    }

    public User(String userId, String userName, String nickName, int sex, String birthday, String location, String portrait) {
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.sex = sex;
        this.birthday = birthday;
        this.location = location;
        this.portrait = portrait;
    }

    public User() {

    }

    //以下为getter和setter
    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public int getSex() {
        return sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getPortrait() {
        return portrait;
    }
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
    public String getUserPwd() {
        return userPwd;
    }
    public void setUserId(String userId){this.userId=userId;};

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
