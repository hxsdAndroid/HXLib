package com.netease.nim.uikit.plugin;

import com.alibaba.fastjson.JSONObject;

public class GiftAttachment extends CustomAttachment {

    private String sendName;// 标题
    private static final String KEY_SENDName = "sendName";

    public GiftAttachment() {
        super(CustomAttachmentType.GIFT);
        sendName="123";
    }

    @Override
    protected void parseData(JSONObject data) {
        sendName = data.getString(KEY_SENDName);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_SENDName, sendName);
        return data;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String briberyName) {
        this.sendName = briberyName;
    }


}
