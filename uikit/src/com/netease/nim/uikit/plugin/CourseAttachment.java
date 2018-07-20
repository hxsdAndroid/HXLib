package com.netease.nim.uikit.plugin;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class CourseAttachment extends CustomAttachment {

    private String text;// 标题
    private InsertInfo insertInfo;
    private static final String KEY_TXT = "text";
    private static final String KEY_INSERTINFO = "insertInfo";

    public
    CourseAttachment() {
        super(CustomAttachmentType.COURSE);
    }

    @Override
    protected void parseData(JSONObject data) {
        text = data.getString(KEY_TXT);
        insertInfo = data.getObject(KEY_INSERTINFO,InsertInfo.class);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_TXT, text);
        data.put(KEY_INSERTINFO, insertInfo);
        return data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InsertInfo getInsertInfo() {
        return insertInfo;
    }

    public void setInsertInfo(InsertInfo insertInfo) {
        this.insertInfo = insertInfo;
    }
}
