package com.netease.nim.uikit.plugin;

/**
 * Created by muwenlei on 2018/2/6.
 */

public class NimEvent_Msg {
    private String msg;
    private String userId;
    private int type;
    private boolean isSend=false;//是否是本人发送的

    public NimEvent_Msg() {
        super();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public NimEvent_Msg(String msg, String userId, int type, boolean isSend) {
        this.msg = msg;
        this.userId = userId;
        this.type = type;
        this.isSend = isSend;
    }

}
