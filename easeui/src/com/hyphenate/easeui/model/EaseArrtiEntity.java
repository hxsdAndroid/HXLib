package com.hyphenate.easeui.model;


import java.io.Serializable;

/**
 * Created by muwenlei on 2017/5/16.
 * 消息体扩展字段
 */

public class EaseArrtiEntity implements Serializable{
    private String nickname;//用户昵称
    private int tag;//用户标签值
    private String tagStr;//用户标签值的涵义
    private String timestamp;//时间戳，格式是1494835938000，13位的时间戳

    public EaseArrtiEntity(String nickname, int tag, String tagStr, String timestamp) {
        this.nickname = nickname;
        this.tag = tag;
        this.tagStr = tagStr;
        this.timestamp = timestamp;
    }

    public EaseArrtiEntity() {
        super();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getTagStr() {
        return tagStr;
    }

    public void setTagStr(String tagStr) {
        this.tagStr = tagStr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}
