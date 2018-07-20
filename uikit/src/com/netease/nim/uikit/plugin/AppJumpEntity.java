package com.netease.nim.uikit.plugin;

import java.io.Serializable;

/**
 * Created by muwenlei on 2018/3/23.
 */

public class AppJumpEntity implements Serializable{

    private String msgAction;
    private String msgActionParam;

    public String getMsgAction() {
        return msgAction;
    }

    public void setMsgAction(String msgAction) {
        this.msgAction = msgAction;
    }

    public String getMsgActionParam() {
        return msgActionParam;
    }

    public void setMsgActionParam(String msgActionParam) {
        this.msgActionParam = msgActionParam;
    }

    public AppJumpEntity() {
        super();
    }

    public AppJumpEntity(String msgAction, String msgActionParam) {
        this.msgAction = msgAction;
        this.msgActionParam = msgActionParam;
    }
}
