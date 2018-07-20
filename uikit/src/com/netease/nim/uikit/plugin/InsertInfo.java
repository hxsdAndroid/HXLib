package com.netease.nim.uikit.plugin;

import java.io.Serializable;

/**
 * Created by muwenlei on 2018/3/23.
 */

public class InsertInfo implements Serializable {
    private String title;
    private String desc;
    private String cover_img;
    private AppJumpEntity appJump;

    public InsertInfo(String title, String desc, String cover_img, AppJumpEntity appJump) {
        this.title = title;
        this.desc = desc;
        this.cover_img = cover_img;
        this.appJump = appJump;
    }

    public InsertInfo() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    public AppJumpEntity getAppJump() {
        return appJump;
    }

    public void setAppJump(AppJumpEntity appJump) {
        this.appJump = appJump;
    }
}
