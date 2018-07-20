package com.netease.nim.uikit.plugin;

import com.alibaba.fastjson.JSONObject;

public class ImageAttachment extends CustomAttachment {

    private String image;// 标题
    private static final String KEY_SENDName = "image";

    public ImageAttachment() {
        super(CustomAttachmentType.IMAGE);
    }

    @Override
    protected void parseData(JSONObject data) {
        image = data.getString(KEY_SENDName);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_SENDName, image);
        return data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
