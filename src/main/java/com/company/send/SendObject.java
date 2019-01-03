package com.company.send;

/**
 * Created by lelezhang on 2019/1/3.
 */
public class SendObject {

    private String reqTypep;
    private Perception perception;
    private UserInfo userInfo;


    public String getReqTypep() {
        return reqTypep;
    }

    public void setReqTypep(String reqTypep) {
        this.reqTypep = reqTypep;
    }

    public Perception getPerception() {
        return perception;
    }

    public void setPerception(Perception perception) {
        this.perception = perception;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
