package com.netease.nim.uikit.plugin;

import com.alibaba.fastjson.JSONObject;

public class SignInAttachment extends CustomAttachment {

    private String value;// 标题
    private static final String KEY_VALUE = "value";

    public SignInAttachment() {
        super(CustomAttachmentType.SIGININ);
    }

    @Override
    protected void parseData(JSONObject data) {
        value = data.getString(KEY_VALUE);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_VALUE, value);
        return data;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
